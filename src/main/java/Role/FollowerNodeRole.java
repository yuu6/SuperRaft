package Role;

import Election.ElectionTimeout;
import Node.NodeId;

public class FollowerNodeRole extends AbstractNodeRole{
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

    private final NodeId votedFor;

    private final NodeId leaderId;

    private final ElectionTimeout electionTimeout;

    // 从节点的构造函数
    public FollowerNodeRole(int term, NodeId votedFor, NodeId leaderId, ElectionTimeout electionTimeout){
        super(RoleName.FOLLOWER, term);
        this.votedFor = votedFor;
        this.leaderId = leaderId;
        this.electionTimeout = electionTimeout;
    }

    @Override
    public void cancelTimeoutOrTask(){
        electionTimeout.cancel();
    }
}
