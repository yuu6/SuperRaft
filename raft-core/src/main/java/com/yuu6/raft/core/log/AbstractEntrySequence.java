package com.yuu6.raft.core.log;

import com.yuu6.raft.core.log.entry.Entry;

import java.util.Collections;
import java.util.EmptyStackException;
import java.util.List;

/**
 * @Author: yuu6
 * @Date: 2022/03/30/上午8:44
 */
public abstract class AbstractEntrySequence implements EntrySequence{
    // 日志索引偏移
    int logIndexOffset;
    // 下一条日志下标
    int nextLogIndex;

    public AbstractEntrySequence(int logIndexOffset) {
        this.logIndexOffset = logIndexOffset;
        this.nextLogIndex = logIndexOffset;
    }

    @Override
    public boolean isEmpty() {
        return logIndexOffset == nextLogIndex;
    }

    @Override
    public int getFirstLogIndex() {
        if (isEmpty()){
            System.out.println(("日志为空！！"));
            return -1;
        }
        return doGetFirstLogIndex();
    }

    int doGetFirstLogIndex(){
        return logIndexOffset;
    }

    @Override
    public int getLastLogIndex() {
        if (isEmpty()){
            System.out.println("日志为空！！");
        }
        return doGetLastLogIndex();
    }

    private int doGetLastLogIndex() {
        return nextLogIndex - 1;
    }

    @Override
    public int getNextLogIndex() {
        return nextLogIndex;
    }

    @Override
    public List<Entry> subList(int fromIndex) {
        if (isEmpty()  || fromIndex > getLastLogIndex()){
            return Collections.EMPTY_LIST;
        }
        return subList(Math.max(fromIndex, getFirstLogIndex()), nextLogIndex);
    }

    @Override
    public List<Entry> subList(int fromIndex, int toIndex) {
        if (isEmpty()){
            throw new EmptyStackException();
        }
        if (fromIndex < getFirstLogIndex() ||
            toIndex > nextLogIndex ||
            fromIndex > toIndex){
            throw new IllegalArgumentException("要查询的index 不合理！");
        }
        return doSubList(fromIndex, toIndex);
    }

    @Override
    public boolean isEntryPresent(int index) {
        return !isEmpty() && index >= getFirstLogIndex() && index <= getLastLogIndex();
    }

    @Override
    public EntryMeta getEntryMeta(int index) {
        Entry entry = getEntry(index);
        return entry != null ? entry.getMeta() : null;
    }

    /**
     * 通过索引获取条目
     * @param index
     * @return
     */
    @Override
    public Entry getEntry(int index) {
        if (!isEntryPresent(index)){
            return null;
        }
        return doGetEntry(index);
    }


    @Override
    public Entry getLastEntry() {
        return isEmpty() ? null : doGetEntry(doGetLastLogIndex());
    }

    @Override
    public void append(Entry entry) {
        if (entry.getIndex() != nextLogIndex){
            throw new IllegalArgumentException("entry index must be " + nextLogIndex);
        }

        doAppend(entry);
        nextLogIndex ++;
    }

    @Override
    public void append(List<Entry> entries) {
        for (Entry e : entries) {
            append(e);
        }
    }

    @Override
    public void removeAfter(int index) {
        if (isEmpty() || index >= doGetLastLogIndex()){
            return;
        }
        doRemoveAfter(index);
    }

    protected abstract void doRemoveAfter(int index);

    @Override
    public abstract void close();

    protected abstract Entry doGetEntry(int index);

    @Override
    public abstract void commit(int index);

    @Override
    public abstract int getCommitIndex();

    protected abstract List<Entry> doSubList(int fromIndex, int toIndex);

    protected abstract void doAppend(Entry e);

}
