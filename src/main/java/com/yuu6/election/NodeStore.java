package com.yuu6.election;

import com.yuu6.node.NodeId;

public interface NodeStore {
    int getTerm();

    void setTerm(int term);

    NodeId getVotedFor();

    void setVotedFor(NodeId nodeId);

    void close();
}
