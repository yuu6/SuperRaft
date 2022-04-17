package com.yuu6.raft.core.election;

import com.yuu6.raft.core.node.NodeId;

public interface NodeStore {
    int getTerm();

    void setTerm(int term);

    NodeId getVotedFor();

    void setVotedFor(NodeId nodeId);

    void close();
}
