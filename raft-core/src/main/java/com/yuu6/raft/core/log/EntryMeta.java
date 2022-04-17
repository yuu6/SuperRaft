package com.yuu6.raft.core.log;

/**
 * @Author: yuu6
 * @Date: 2022/03/30/上午8:14
 */
public class EntryMeta {
    private final int kind;
    private final int index;
    private final int term;
    public EntryMeta(int kind, int index, int term) {
        this.kind = kind;
        this.term = term;
        this.index = index;
    }

    public int getKind() {
        return kind;
    }

    public int getIndex() {
        return index;
    }

    public int getTerm() {
        return term;
    }
}
