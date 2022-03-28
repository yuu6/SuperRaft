package com.yuu6.election;

import com.yuu6.mess.AppendEntriesReq;
import com.yuu6.node.NodeId;

/**
 * @Author: yuu6
 * @Date: 2022/03/27/下午2:03
 */
public class AppendEntriesReqMessage {
    private AppendEntriesReq appendEntriesReq;

    private NodeId sourceNodeId;

    public AppendEntriesReqMessage(AppendEntriesReq appendEntriesReq, NodeId sourceNodeId) {
        this.appendEntriesReq = appendEntriesReq;
        this.sourceNodeId = sourceNodeId;
    }

    public NodeId getSourceNodeId() {
        return sourceNodeId;
    }

    public void setSourceNodeId(NodeId sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
    }

    public AppendEntriesReq getAppendEntriesReq() {
        return appendEntriesReq;
    }

    public void setAppendEntriesReq(AppendEntriesReq appendEntriesReq) {
        this.appendEntriesReq = appendEntriesReq;
    }
}