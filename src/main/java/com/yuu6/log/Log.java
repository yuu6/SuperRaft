package com.yuu6.log;

import com.yuu6.log.entry.Entry;
import com.yuu6.log.entry.GeneralEntry;
import com.yuu6.log.entry.NoOpEntry;
import com.yuu6.mess.AppendEntriesReq;
import com.yuu6.node.NodeId;

import java.util.List;

/**
 * @Author: yuu6
 * @Date: 2022/03/30/上午8:23
 */
public interface Log {

    int ALL_ENTRIES = -1;
    // 获得上一条日志记录
    EntryMeta getLastEntryMeta();
    // 创建复制日志请求
    AppendEntriesReq createAppendEntriesReq(int term, NodeId nodeId, int nextIndex, int maxEntries);
    // 获取下一条日志的索引
    int getNextIndex();
    // 获取已经提交的索引
    int getCommitIndex();

    boolean isNewerThan(int lastLogIndex, int lastLogTerm);
    // 增加一个No_op日志
    NoOpEntry appendEntry(int term);
    // 增加一条普通日志
    GeneralEntry appendEntry(int term, byte[] command);
    // 追加来自leader的日志条目
    boolean appendEntriesFromLeader(int prevLogIndex, int prevLogTerm, List<Entry> entries);
    // 推进commit index
    void advanceCommitIndex(int newCommitIndex, int currentTerm);

    void close();
}
