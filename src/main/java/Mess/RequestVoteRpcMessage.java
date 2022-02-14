package Mess;

import Node.NodeEndpoint;
import Node.NodeId;

import javax.xml.stream.events.EndDocument;

public class RequestVoteRpcMessage {
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

    private RequestVoteRpc requestVoteRpc;

    private NodeId sourceNodeId;

}
