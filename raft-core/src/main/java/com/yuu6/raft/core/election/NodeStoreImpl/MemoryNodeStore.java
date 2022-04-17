package com.yuu6.raft.core.election.NodeStoreImpl;

import com.yuu6.raft.core.election.NodeStore;
import com.yuu6.raft.core.node.NodeId;

public class MemoryNodeStore implements NodeStore {
    private int term;
    private NodeId votedFor;

    public MemoryNodeStore(){
        this(0, null);
    }

    public MemoryNodeStore(int term, NodeId votedFor){
        this.term = term;
        this.votedFor = votedFor;
    }

    @Override
    public int getTerm() {
        return term;
    }

    @Override
    public void setTerm(int term) {
        this.term = term;
    }

    @Override
    public NodeId getVotedFor() {
        return votedFor;
    }

    @Override
    public void setVotedFor(NodeId nodeId) {
        this.votedFor = votedFor;
    }

    @Override
    public void close() {
    }
}
