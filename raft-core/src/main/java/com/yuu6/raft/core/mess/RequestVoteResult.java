package com.yuu6.raft.core.mess;

public class RequestVoteResult {
    // 自己的轮次
    private final int term;
    // 是否投票
    private final boolean voteGranted;

    public RequestVoteResult(int term, boolean voteGranted){
        this.term = term;
        this.voteGranted = voteGranted;
    }

    public int getTerm(){
        return term;
    }

    public boolean isVoteGranted(){
        return voteGranted;
    }

    @Override
    public String toString(){
        return "RequestVoteResult{" + "term=" + term +
                ", voteGrantet=" + voteGranted +
                '}';
    }

}
