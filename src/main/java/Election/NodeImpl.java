package Election;


import Mess.AppendEntriesRpc;
import Mess.RequestVoteResult;
import Mess.RequestVoteRpc;
import Mess.RequestVoteRpcMessage;
import Role.*;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Node.NodeId;
import Node.GroupMember;

import java.util.Objects;

public class NodeImpl implements Node{

    private static final Logger logger = LoggerFactory.getLogger(NodeImpl.class);
    // 上下文信息，一些关联的组件
    private final NodeContext context;
    private boolean started;
    // 角色
    private AbstractNodeRole role;

    NodeImpl(NodeContext context){
        this.context = context;
    }
    // 初始化
    @Override
    public synchronized void start() {
        if (started){
            return;
        }
        // 将自己注册到eventBus
        context.getEventBus().register(this);
        context.getConnector().initialize();
        NodeStore store = context.getStore();
        changeToRole(new FollowerNodeRole(
                store.getTerm(), store.getVotedFor(), null, scheduleElectionTimeout()
        ));
        started = true;
    }

    private ElectionTimeout scheduleElectionTimeout(){
        return context.getScheduler().scheduleElectionTimeout(this::electionTimeout);
    }

    private void electionTimeout() {
        context.getTaskExecutor().submit(this::doProcessElectionTimeout);
    }

    private void doProcessElectionTimeout() {
        //leader 角色不会有选举超时
        if (role.getName() == RoleName.LEADER){
            logger.warn("leader 不应该选举超时！！");
            return;
        }

        int newTerm = role.getTerm() + 1;
        role.cancelTimeoutOrTask();
        logger.info("start election!!");

        changeToRole(new CandidateNodeRole(newTerm, scheduleElectionTimeout()));

        RequestVoteRpc rpc = new RequestVoteRpc();
        rpc.setTerm(newTerm);
        rpc.setCandidateId(context.selfId());
        rpc.setLastLogIndex(0);
        rpc.setLastLogTerm(0);

        context.getConnector().sendRequestVote(rpc, context.getGroup().listEndpointExceptSelf());

    }

    private void changeToRole(AbstractNodeRole newRole){
        logger.debug("node {}, role state changed -> {}", context.selfId(), newRole);
        NodeStore store = context.getStore();
        store.setTerm(newRole.getTerm());
        if (newRole.getName() == RoleName.FOLLOWER){
            store.setVotedFor(((FollowerNodeRole)newRole).getVotedFor());
        }
        role = newRole;
    }

    @Override
    public void stop() throws InterruptedException {
        if (!started){
            throw new IllegalStateException("node not started!");
        }

        context.getScheduler().stop();
        context.getConnector().close();
        context.getTaskExecutor().shutdown();
        started = false;
    }

    /**
     * 收到投票消息
     * @param rpcMessage
     */
    @Subscribe
    public void onReceiveRequestVoteRpc(RequestVoteRpcMessage rpcMessage){
        context.getTaskExecutor().submit(
                () -> context.getConnector().replyRequestVote(
                        doProcessRequestVoteRpc(rpcMessage),
                        context.findMember(rpcMessage.getNodeId()).getEndpoint();
                );
        );
    }

    @Subscribe
    public void onReceiveRequestVoteResult(RequestVoteResult result){
        context.getTaskExecutor().submit(
                () -> doProcessRequestVoteRpcResult(result)
        );
    }

    private void doProcessRequestVoteRpcResult(RequestVoteResult result) {
        if (result.getTerm() > role.getTerm()){
            becomeFollower(result.getTerm(), null, null, true);
            return;
        }

        //如果自己不是Candidate ,则忽略
        if (role.getName() != RoleName.CANDIDATE){
            return;
        }

        if (result.getTerm() < role.getTerm()|| !result.isVoteGranted()){
            return;
        }

        int currentVotesCount = ((CandidateNodeRole)role).getVotesCount() + 1;

        int countOfMajor = context.getGroup().getCount();

        logger.debug("votes count {}, node count {}", currentVotesCount, countOfMajor);

        role.cancelTimeoutOrTask();

        // 票数过半
        if (currentVotesCount > countOfMajor / 2){
            changeToRole(new LeaderNodeRole(role.getTerm(), scheduleLogReplicationTask()));

        }else{
            changeToRole(new CandidateNodeRole(role.getTerm(), currentVotesCount, scheduleElectionTimeout()));
        }
    }

    private LogReplicationTask scheduleLogReplicationTask() {
        return context.getScheduler().scheduleLogReplicationTask(this::replicateLog);
    }

    void replicateLog(){
        context.getTaskExecutor().submit(this::doReplicateLog);
    }

    private void doReplicateLog(){
        for (GroupMember member : context.getGroup().listReplicationTarget()) {
            doReplicateLog(member);
        }
    }

    private void doReplicateLog( GroupMember member){
        AppendEntriesRpc rpc = new AppendEntriesRpc();
        rpc.setTerm(role.getTerm());
        rpc.setLeaderId(context.selfId());
        rpc.setPrevLogIndex(0);
        rpc.setPrevLogTerm(0);
        rpc.setLeaderCommit(0);
        context.getConnector().sendAppendEntries(rpc, member.getEndpoint());
    }

    private RequestVoteResult doProcessRequestVoteRpc(RequestVoteRpcMessage rpcMessage){
        RequestVoteRpc rpc = rpcMessage.getRequestVoteRpc();
        if (rpc.getTerm() < role.getTerm()){
            logger.debug("term from rpc < currnet term, don`t vote ({} < {})", rpc.getTerm(), role.getTerm());
            return new RequestVoteResult(role.getTerm(), false);
        }
        boolean voteForCandidate = true;

        if (rpc.getTerm() > role.getTerm()){
            becomeFollower(rpc.getTerm(), (voteForCandidate ? rpc.getCandidateId(): null), null, true);
            return new RequestVoteResult(rpc.getTerm(), voteForCandidate);
        }

        switch (role.getName()){
            case FOLLOWER:
                FollowerNodeRole followerNodeRole = (FollowerNodeRole) role;
                NodeId votedFor = followerNodeRole.getVotedFor();
                // 两种情况：
                // case 1: 自己尚未投过票，并且对方的日志比自己新
                // case 2: 自己已经给对方透过票1
                // 投票后需要切换为follower节点
                if ((votedFor == null && voteForCandidate) || Objects.equals(votedFor, rpc.getCandidateId())){
                    becomeFollower(role.getTerm(), rpc.getCandidateId(), null, true);
                    return new RequestVoteResult(rpc.getTerm(), true);
                }
                return new RequestVoteResult(role.getTerm(), false);
            case CANDIDATE:
            case LEADER:
                return new RequestVoteResult(role.getTerm(), false);
            default:
                throw new IllegalStateException("unexpected node role [" + role.getName() + "]");
        }
    }

    private void becomeFollower(int term, NodeId votedFor, NodeId leaderId, boolean scheduleElectionTimeout) {
        role.cancelTimeoutOrTask(); // 取消超时或者定时器
        if (leaderId != null && !leaderId.equals(((FollowerNodeRole) role).getLeaderId(context.selfId()))){
            logger.info("current leader is {}, term {}", leaderId, term);
        }

        // 重新创建选举超时定时器
        ElectionTimeout electionTimeout = scheduleElectionTimeout?scheduleElectionTimeout() :ElectionTimeout.None;
        changeToRole(new FollowerNodeRole(term, votedFor, leaderId, electionTimeout));
    }


    @Subscribe
    public void onReceiveAppendEntriesRpc(AppendEntriesRpcMessage rpcMessage){
        context.getTaskExecutor().submit()
    }

}
