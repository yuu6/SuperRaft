package com.yuu6.mess;

import com.yuu6.log.entry.Entry;
import com.yuu6.node.NodeId;

import java.util.Collections;
import java.util.List;

public class AppendEntriesReq {
    private int term;
    private NodeId leaderId;
    private int prevLogIndex = 0; // 前一条日志的索引
    private int prevLogTerm; // 前一条日志的term
    private List<Entry> entries = Collections.emptyList();
    private int leaderCommit;
//    private int lastAppendEntryIndex;

    @Override
    public String toString() {
        return "AppendEntriesRpc{" +
                "term=" + term +
                ", leaderId=" + leaderId +
                ", prevLogIndex=" + prevLogIndex +
                ", prevLogTerm=" + prevLogTerm +
                ", entries=" + entries +
                ", leaderCommit=" + leaderCommit +
                '}';
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public NodeId getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(NodeId leaderId) {
        this.leaderId = leaderId;
    }

    public int getPrevLogIndex() {
        return prevLogIndex;
    }

    public void setPrevLogIndex(int prevLogIndex) {
        this.prevLogIndex = prevLogIndex;
    }

    public int getPrevLogTerm() {
        return prevLogTerm;
    }

    public void setPrevLogTerm(int prevLogTerm) {
        this.prevLogTerm = prevLogTerm;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public int getLeaderCommit() {
        return leaderCommit;
    }

    public void setLeaderCommit(int leaderCommit) {
        this.leaderCommit = leaderCommit;
    }

    public int getLastAppendEntryIndex() {
        return prevLogIndex;
    }
}
