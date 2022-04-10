package com.yuu6.mess;

// 日志复制的结果
public class AppendEntriesResult {
    // 选举轮次
    public final int term;
    // 是否成功
    private final boolean success;

    public int getTerm() {
        return term;
    }

    @Override
    public String toString() {
        return "AppendEntriesResult{" +
                "term=" + term +
                ", success=" + success +
                '}';
    }

    public boolean isSuccess() {
        return success;
    }



    public AppendEntriesResult(int term, boolean success) {
        this.term = term;
        this.success = success;
    }


}
