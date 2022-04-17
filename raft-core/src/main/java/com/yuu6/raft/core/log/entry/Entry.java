package com.yuu6.raft.core.log.entry;

import com.yuu6.raft.core.log.EntryMeta;

/**
 * @Author: yuu6
 * @Date: 2022/03/30/上午8:08
 */
public interface Entry {
    // 日志条目类型 空日志
    int KIND_NO_OP = 0;
    // 日志条目类型 普通日志
    int KIND_GENERAL = 1;
    // 类型
    int getKind();
    // 日志序号
    int getIndex();
    // 日志轮次
    int getTerm();
    // 元数据，排除了命令的日志元信息
    EntryMeta getMeta();
    // 命令内容
    byte[] getCommandBytes();
}
