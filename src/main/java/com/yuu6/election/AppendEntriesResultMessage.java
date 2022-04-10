package com.yuu6.election;

import com.yuu6.mess.AppendEntriesReq;
import com.yuu6.mess.AppendEntriesResult;
import com.yuu6.node.NodeId;

/**
 * @Author: yuu6
 * @Date: 2022/03/27/下午2:23
 */
public class AppendEntriesResultMessage {
    // 请求
    private AppendEntriesResult appendEntriesResult;

    // 来源的nodeid
    private NodeId sourceNodeId;

    private AppendEntriesReq lastAppendEntriesReq;
    public AppendEntriesReq getLastAppendEntriesReq() {
        return lastAppendEntriesReq;
    }

    public void setLastAppendEntriesReq(AppendEntriesReq lastAppendEntriesReq) {
        this.lastAppendEntriesReq = lastAppendEntriesReq;
    }


    public AppendEntriesResultMessage(AppendEntriesResult result, NodeId remoteId, AppendEntriesReq lastAppendEntriesReq){
        this.appendEntriesResult= result;
        this.sourceNodeId = remoteId;
        this.lastAppendEntriesReq = lastAppendEntriesReq;
    }

    public AppendEntriesResult getAppendEntriesResult() {
        return appendEntriesResult;
    }

    public void setAppendEntriesResult(AppendEntriesResult appendEntriesResult) {
        this.appendEntriesResult = appendEntriesResult;
    }

    public NodeId getSourceNodeId() {
        return sourceNodeId;
    }

    public void setSourceNodeId(NodeId sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
    }
}
