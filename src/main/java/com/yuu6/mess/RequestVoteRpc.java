package com.yuu6.mess;

import com.yuu6.node.NodeId;

/**
 * 请求投票的消息
 */
public class RequestVoteRpc {
    private int term;
    private NodeId candidateId; // 候选者的节点Id，一般都是发送者自己
    // 上一条日志的信息
    private int lastLogIndex = 0; // 候选者最后一条日志的索引
    private int lastLogTerm = 0; // 候选者最后一条日志的term

    @Override
    public String toString() {
        return "RequestVoteRpc{" +
                "term=" + term +
                ", candidateId=" + candidateId +
                ", lastLogIndex=" + lastLogIndex +
                ", lastLogTerm=" + lastLogTerm +
                '}';
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public NodeId getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(NodeId candidateId) {
        this.candidateId = candidateId;
    }

    public int getLastLogIndex() {
        return lastLogIndex;
    }

    public void setLastLogIndex(int lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }

    public int getLastLogTerm() {
        return lastLogTerm;
    }

    public void setLastLogTerm(int lastLogTerm) {
        this.lastLogTerm = lastLogTerm;
    }
}
