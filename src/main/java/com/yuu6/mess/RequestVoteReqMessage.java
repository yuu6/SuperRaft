package com.yuu6.mess;

import com.yuu6.communite.channels.NioChannel;
import com.yuu6.node.NodeId;

public class RequestVoteReqMessage {
    // 请求
    private RequestVoteReq requestVoteReq;

    // 来源的nodeid
    private NodeId sourceNodeId;

    private NioChannel nioChannel;

    public RequestVoteReqMessage(RequestVoteReq req, NodeId remoteId, NioChannel channel){
        this.requestVoteReq = req;
        this.sourceNodeId = remoteId;
        this.nioChannel = channel;
    }

    public RequestVoteReq getRequestVoteRpc() {
        return requestVoteReq;
    }

    public void setRequestVoteRpc(RequestVoteReq requestVoteReq) {
        this.requestVoteReq = requestVoteReq;
    }

    public NodeId getNodeId() {
        return sourceNodeId;
    }

    public void setNodeId(NodeId sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
    }
}
