package com.yuu6.raft.core.log.storage;

/**
 * @Description: 日志索引条目
 * @Author: yuu6
 * @Date: 2022/03/31/下午11:07
 */
public class EntryIndexItem {
    private int index;
    private long offset;
    private int kind;
    private int term;

    public EntryIndexItem(int index, long offset, int kind, int term) {
        this.index = index;
        this.offset = offset;
        this.kind = kind;
        this.term = term;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
