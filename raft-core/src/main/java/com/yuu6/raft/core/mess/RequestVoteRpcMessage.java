package com.yuu6.raft.core.mess;

import com.yuu6.raft.core.communite.channels.NioChannel;
import com.yuu6.raft.core.node.NodeId;

public class RequestVoteRpcMessage {
    // 请求
    private RequestVoteRpc requestVoteRpc;

    // 来源的nodeid
    private NodeId sourceNodeId;

    private NioChannel nioChannel;

    public RequestVoteRpcMessage(RequestVoteRpc req, NodeId remoteId, NioChannel channel){
        this.requestVoteRpc = req;
        this.sourceNodeId = remoteId;
        this.nioChannel = channel;
    }

    public RequestVoteRpc getRequestVoteRpc() {
        return requestVoteRpc;
    }

    public void setRequestVoteRpc(RequestVoteRpc requestVoteRpc) {
        this.requestVoteRpc = requestVoteRpc;
    }

    public NodeId getNodeId() {
        return sourceNodeId;
    }

    public void setNodeId(NodeId sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
    }
}
