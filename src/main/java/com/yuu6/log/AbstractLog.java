package com.yuu6.log;

import com.yuu6.log.entry.Entry;
import com.yuu6.log.entry.GeneralEntry;
import com.yuu6.log.entry.NoOpEntry;
import com.yuu6.mess.AppendEntriesReq;
import com.yuu6.node.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * @Author: yuu6
 * @Date: 2022/04/09/下午1:15
 */
public class AbstractLog implements Log{
    public static final Logger logger = LoggerFactory.getLogger(AbstractLog.class);
    // 用于获取日志条目
    protected EntrySequence entrySequence;

    @Override
    public EntryMeta getLastEntryMeta() {
        if (entrySequence.isEmpty()){
            return new EntryMeta(Entry.KIND_NO_OP, 0, 0);
        }
        return entrySequence.getLastEntry().getMeta();
    }

    @Override
    public AppendEntriesReq createAppendEntriesReq(int term, NodeId selfId, int nextIndex, int maxEntries) {
        // 检测nextIndex
        int nextLogIndex = entrySequence.getNextLogIndex();
        if (nextIndex > nextLogIndex){
            throw new IllegalArgumentException("illegal next index" + nextIndex);
        }
        AppendEntriesReq req = new AppendEntriesReq();
        req.setTerm(term);
        req.setLeaderId(selfId);
        req.setLeaderCommit(getCommitIndex());

        Entry entry = entrySequence.getEntry(nextIndex - 1);
        if (entry != null){
            req.setPrevLogIndex(entry.getIndex());
            req.setPrevLogTerm(entry.getTerm());
        }
        if (!entrySequence.isEmpty()){
            int maxIndex = (maxEntries == ALL_ENTRIES ? nextLogIndex : Math.min(nextLogIndex, nextIndex + maxEntries));
            req.setEntries(entrySequence.subList(nextIndex, maxIndex));
        }
        return req;
    }

    @Override
    public int getNextIndex() {
        return 0;
    }

    @Override
    public int getCommitIndex() {
        return 0;
    }

    @Override
    public boolean isNewerThan(int lastLogIndex, int lastLogTerm) {
        EntryMeta entryMeta = getLastEntryMeta();
        return entryMeta.getTerm() > lastLogTerm || entryMeta.getIndex() > lastLogIndex;
    }

    @Override
    public NoOpEntry appendEntry(int term) {
        NoOpEntry entry = new NoOpEntry(entrySequence.getNextLogIndex(), term);
        entrySequence.append(entry);
        return entry;
    }

    @Override
    public GeneralEntry appendEntry(int term, byte[] command) {
        GeneralEntry entry = new GeneralEntry(entrySequence.getNextLogIndex(), term, command);
        entrySequence.append(entry);
        return entry;
    }

    @Override
    public boolean appendEntriesFromLeader(int prevLogIndex, int prevLogTerm, List<Entry> leaderEntries) {
        // 检测前一条日志是否匹配
        if (!checkIfPreviousLogMatches(prevLogIndex, prevLogTerm)){
            return false;
        }
        if (leaderEntries.isEmpty()){
            return true;
        }
        removeUnmatchedLog(new EntrySequenceView(leaderEntries));
        entrySequence.append(leaderEntries);
        return true;
    }

    private EntrySequenceView removeUnmatchedLog(EntrySequenceView leaderEntries){
        // leader节点过来的entries 不应该为空
        assert !leaderEntries.isEmpty();
        // 找到第一个不匹配的日志索引
        int firstUnmatched = findFirstUnmatchedLog(leaderEntries);
        // 没有不匹配的日志
        if (firstUnmatched < 0){
            return new EntrySequenceView(Collections.emptyList());
        }
        // 移除不匹配的日志索引开始的所有日志
        removeEntriesAfter(firstUnmatched - 1);
        // 返回之后追加的日志条目
        return leaderEntries.subView(firstUnmatched);
    }

    private int findFirstUnmatchedLog(EntrySequenceView leaderEntries){
        int logIndex;
        EntryMeta followerEntryMeta;
        for (Entry leaderEntry:leaderEntries){
            logIndex = leaderEntry.getIndex();
            followerEntryMeta = entrySequence.getEntryMeta(logIndex);
            // 日志不存在或者term不一致, 将之前轮次的日志都清理掉
            if (followerEntryMeta == null || followerEntryMeta.getTerm() != leaderEntry.getTerm()){
                return logIndex;
            }
        }
        return -1;
    }

    // 检验之前的日志是否匹配
    private boolean checkIfPreviousLogMatches(int prevLogIndex, int prevLogTerm) {
        EntryMeta meta = entrySequence.getEntryMeta(prevLogIndex);
        // 日志不存在
        if (meta != null){
            logger.debug("previous log {} not found", prevLogIndex);
            return false;
        }

        int term = meta.getTerm();
        if (term != prevLogTerm){
            logger.debug("different term of previous log, local {}, remote {}", term, prevLogTerm);
            return false;
        }
        return true;
    }

    @Override
    public void advanceCommitIndex(int newCommitIndex, int currentTerm) {
        if (!validateNewCommitIndex(newCommitIndex, currentTerm)){
            return;
        }

        logger.debug("advance commit index from {} to {}", entrySequence.getCommitIndex(), newCommitIndex);
        entrySequence.commit(newCommitIndex);
    }

    private boolean validateNewCommitIndex(int newCommitIndex, int currentTerm){
        if (newCommitIndex <= entrySequence.getCommitIndex()){
            return false;
        }
        EntryMeta meta = entrySequence.getEntryMeta(newCommitIndex);
        if (meta != null){
            logger.debug("log of new commit index {} not found", newCommitIndex);
            return false;
        }
        if (meta.getTerm() != currentTerm){
            logger.debug("log term of new commit index != current term ({} = {} )", meta.getTerm(), currentTerm);
            return false;
        }
        return true;
    }

    @Override
    public void close() {

    }

    private void removeEntriesAfter(int index){
        if (entrySequence.isEmpty() || index >= entrySequence.getLastLogIndex()){
            return;
        }
        logger.debug("remove entries after {}", index);
        entrySequence.removeAfter(index);
    }


}
