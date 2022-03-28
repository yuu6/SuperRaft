package com.yuu6.role;

import com.yuu6.election.ElectionTimeout;
import com.yuu6.node.NodeId;

/**
 * 跟随的节点
 */
public class FollowerNodeRole extends AbstractNodeRole{
    private final NodeId votedFor;

    private final NodeId leaderId;

    /**
     * 选举超时
     */
    private final ElectionTimeout electionTimeout;

    // 从节点的构造函数
    public FollowerNodeRole(int term, NodeId votedFor, NodeId leaderId, ElectionTimeout electionTimeout){
        super(RoleName.FOLLOWER, term);
        this.votedFor = votedFor;
        this.leaderId = leaderId;
        this.electionTimeout = electionTimeout;
    }
    @Override
    public String toString() {
        return "FollowerNodeRole{" +
                "term="+term+
                "votedFor=" + votedFor +
                ", leaderId=" + leaderId +
                ", electionTimeout=" + electionTimeout +
                '}';
    }

    public NodeId getVotedFor() {
        return votedFor;
    }

    public NodeId getLeaderId(NodeId nodeId) {
        return leaderId;
    }

    @Override
    public void cancelTimeoutOrTask(){
        electionTimeout.cancel();
    }
}
