package com.yuu6.election;


import com.yuu6.mess.*;
import com.yuu6.role.*;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yuu6.node.NodeId;
import com.yuu6.node.GroupMember;

import java.util.Objects;

public class NodeImpl implements Node{

    private static final Logger logger = LoggerFactory.getLogger(NodeImpl.class);
    // 上下文信息，一些关联的组件
    private final NodeContext context;
    private boolean started;
    // 角色
    private AbstractNodeRole role;

    public NodeImpl(NodeContext context){
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
        // 通信组件
        context.getConnector().init();
        NodeStore store = context.getStore();
        // 启动的时候是follower角色
        changeToRole(new FollowerNodeRole(
                store.getTerm(),
                store.getVotedFor(),
                null,
                scheduleElectionTimeout()
        ));
        started = true;
    }

    private ElectionTimeout scheduleElectionTimeout(){
        return context.getScheduler().scheduleElectionTimeout(this::electionTimeout);
    }

    /**
     * 选举超时
     */
    private void electionTimeout() {
        context.getTaskExecutor().submit(this::doProcessElectionTimeout);
    }

    private void doProcessElectionTimeout() {
        //leader 角色不会有选举超时
        if (role.getName() == RoleName.LEADER){
            logger.warn("leader 不应该选举超时！！");
            return;
        }

        // 对于follower节点来说是发起选举
        // 对于candidate 节点来说是再次发起选举
        // 选举term + 1
        int newTerm = role.getTerm() + 1;
        role.cancelTimeoutOrTask();
        logger.info("start election!!");
        System.out.println("======开始选举！！！！！！=======");

        changeToRole(new CandidateNodeRole(newTerm, scheduleElectionTimeout()));

        RequestVoteReq req = new RequestVoteReq();
        req.setTerm(newTerm);
        req.setCandidateId(context.selfId());
        req.setLastLogIndex(0);
        req.setLastLogTerm(0);
        System.out.println(String.format("目前是第%s轮次", newTerm));
        // 给所有节点发送投票的消息
        context.getConnector().sendRequestVote(req, context.getNodeGroup().listEndpointExceptSelf());
    }

    private void changeToRole(AbstractNodeRole newRole){
        System.out.println(String.format(" %s ----> %s",role == null ? null : role.getName() ,newRole.getName()));
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
    public void onReceiveRequestVoteReq(RequestVoteReqMessage rpcMessage){
        System.out.println(String.format("节点%s处理%s的投票请求！", context.selfId(), rpcMessage.getNodeId()));
        context.getTaskExecutor().submit(
                // 发送投票信息
                () -> context.getConnector().replyRequestVote(
                        doProcessRequestVoteReq(rpcMessage),
                        context.findMember(rpcMessage.getNodeId()).getEndpoint()
                )
        );
    }

    @Subscribe
    public void onReceiveRequestVoteResult(RequestVoteResult result){
        System.out.println(String.format("节点%s处理投票结果%s！", context.selfId(), result.isVoteGranted()));

        // 处理投票的结果信息
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

        // 如果对方的term较小，或者没有给自己投票，则忽略
        if (result.getTerm() < role.getTerm()|| !result.isVoteGranted()){
            return;
        }

        // 当前票数
        int currentVotesCount = ((CandidateNodeRole)role).getVotesCount() + 1;

        System.out.println(String.format("%s拥有%s票！", context.selfId(), currentVotesCount));
        // 总的节点数
        int countOfMajor = context.getNodeGroup().getCount();

        logger.debug("votes count {}, node count {}", currentVotesCount, countOfMajor);
        // 取消超时选举定时任务
        role.cancelTimeoutOrTask();
        // 票数过半
        if (currentVotesCount > countOfMajor / 2){
            // 变成了leader节点
            changeToRole(new LeaderNodeRole(role.getTerm(), scheduleLogReplicationTask()));
        }else{
            changeToRole(new CandidateNodeRole(role.getTerm(), currentVotesCount, scheduleElectionTimeout()));
        }
    }

    private LogReplicationTask scheduleLogReplicationTask() {
        return context.getScheduler().scheduleLogReplicationTask(this::replicateLog);
    }

    private void replicateLog(){
        context.getTaskExecutor().submit(this::doReplicateLog);
    }

    private void doReplicateLog(){
        System.out.println("发送心跳时的节点数目"+ context.getNodeGroup().listReplicationTarget().size());
        for (GroupMember member : context.getNodeGroup().listReplicationTarget()) {
            doReplicateLog0(member);
        }
    }

    private void doReplicateLog0(GroupMember member){
        AppendEntriesReq rpc = new AppendEntriesReq();
        rpc.setTerm(role.getTerm());
        rpc.setLeaderId(context.selfId());
        rpc.setPrevLogIndex(0);
        rpc.setPrevLogTerm(0);
        rpc.setLeaderCommit(0);
        context.getConnector().sendAppendEntries(rpc, member.getEndpoint());
    }

    private RequestVoteResult doProcessRequestVoteReq(RequestVoteReqMessage rpcMessage){
        RequestVoteReq rpc = rpcMessage.getRequestVoteRpc();
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
                // candidate 只为自己投票
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
        ElectionTimeout electionTimeout = scheduleElectionTimeout ? scheduleElectionTimeout() :ElectionTimeout.None;
        changeToRole(new FollowerNodeRole(term, votedFor, leaderId, electionTimeout));
    }


    /**
     * 收到心跳消息
     *
     * @param reqMessage
     */
    @Subscribe
    public void onReceiveAppendEntriesReq(AppendEntriesReqMessage reqMessage){
        context.getTaskExecutor().submit(() ->
                context.getConnector().replyAppendEntries(
                        doProcessAppendEntriesReq(reqMessage),
                        context.findMember(reqMessage.getSourceNodeId()).getEndpoint()
                )
        );
    }

    private AppendEntriesResult doProcessAppendEntriesReq(AppendEntriesReqMessage reqMessage) {
        AppendEntriesReq req = reqMessage.getAppendEntriesReq();
        // 如果自己的比对象的term大，则回复自己的term
        if (req.getTerm() < role.getTerm()){
            return new AppendEntriesResult(role.getTerm(), false);
        }

        // 如果对象的term比自己大，则退化为Follower角色
        if (req.getTerm() > role.getTerm()){
            becomeFollower(req.getTerm(), null, req.getLeaderId(), true);
            return new AppendEntriesResult(req.getTerm(), appendEntries(req));
        }

        assert req.getTerm() == role.getTerm();

        switch (role.getName()){
            case FOLLOWER:
                becomeFollower(req.getTerm(), ((FollowerNodeRole)role).getVotedFor(), req.getLeaderId(), true);
                return new AppendEntriesResult(req.getTerm(), appendEntries(req));
            case CANDIDATE:
                becomeFollower(req.getTerm(), null, req.getLeaderId(), true);
                return new AppendEntriesResult(req.getTerm(), appendEntries(req));
            case LEADER:
                System.out.println("错误，出现了两个leader");
                return new AppendEntriesResult(req.getTerm(), false);
            default:
                throw new IllegalStateException("unexpected node role");
        }
    }

    private boolean appendEntries(AppendEntriesReq req) {
        return true;
    }

    @Subscribe
    public void onReceiveAppendEntriesResult(AppendEntriesResultMessage resultMessage){
        context.getTaskExecutor().submit(() -> doProcessAppendEntriesResult(resultMessage));
    }

    private void doProcessAppendEntriesResult(AppendEntriesResultMessage resultMessage) {
        AppendEntriesResult result = resultMessage.getAppendEntriesResult();

        if (result.getTerm() > role.getTerm()){
            becomeFollower(result.getTerm(), null, null, true);
            return;
        }

        if (role.getName() != RoleName.LEADER){
            System.out.println("从其他的节点收到了心跳消息");
        }
    }
}
