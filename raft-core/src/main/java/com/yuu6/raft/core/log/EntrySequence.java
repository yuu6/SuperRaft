package com.yuu6.raft.core.log;

import com.yuu6.raft.core.log.entry.Entry;

import java.util.List;

/**
 * @Description: entry的操作类
 * @Author: yuu6
 * @Date: 2022/03/30/上午8:33
 */
public interface EntrySequence {
    /**
     * entry 是不是为空
     * @return
     */
    boolean isEmpty();

    /**
     * 获得第一个日志的下标
     * @return
     */
    int getFirstLogIndex();

    /**
     * 获得上一个日志的下标
     * @return
     */
    int getLastLogIndex();

    /**
     * 获得下一个日志的下标
     * @return
     */
    int getNextLogIndex();

    /**
     * 获得部分日志条目
     * @param fromIndex
     * @return
     */
    List<Entry> subList(int fromIndex);

    /**
     * 获得部分日志条目
     * @param fromIndex
     * @param toIndex
     * @return
     */
    List<Entry> subList(int fromIndex, int toIndex);

    /**
     * 某个下标的日志是否存在
     * @param index
     * @return
     */
    boolean isEntryPresent(int index);

    /**
     * 获得某个下标日志的元信息
     * @param index
     * @return
     */
    EntryMeta getEntryMeta(int index);

    /**
     * 获得某个下标的日志条目
     * @param index
     * @return
     */
    Entry getEntry(int index);

    /**
     * 获得上一个日志条目
     * @return
     */
    Entry getLastEntry();

    /**
     * 添加日志条目
     * @param entry
     */
    void append(Entry entry);

    /**
     * 添加日志条目列表
     * @param entries
     */
    void append(List<Entry> entries);

    /**
     * 提交某一个日志条目
     * @param index
     */
    void commit(int index);

    /**
     * 获得提交的日志下标
     * @return
     */
    int getCommitIndex();

    /**
     * 将某个下标之后的日志移除
     * @param index
     */
    void removeAfter(int index);

    /**
     * 关闭
     */
    void close();
}
