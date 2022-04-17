package com.yuu6.raft.core.log;

import com.yuu6.raft.core.log.entry.Entry;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @Author: yuu6
 * @Date: 2022/04/09/下午5:42
 */
public class EntrySequenceView implements Iterable<Entry>{
    private final List<Entry> entries;
    private int firstLogIndex = -1;
    private int lastLogIndex = -1;

    EntrySequenceView(List<Entry> entries){
        this.entries = entries;
        if (!entries.isEmpty()){
            firstLogIndex = entries.get(0).getIndex();
            lastLogIndex = entries.get(entries.size() - 1).getIndex();
        }
    }

    Entry get(int index){
        if (entries.isEmpty() || index < firstLogIndex || index > lastLogIndex){
            return null;
        }
        return entries.get(index - firstLogIndex);
    }

    boolean isEmpty(){
        return entries.isEmpty();
    }

    int getFirstLogIndex(){
        return firstLogIndex;
    }

    int getLastLogIndex(){
        return lastLogIndex;
    }

    @Override
    public Iterator<Entry> iterator() {
        return entries.iterator();
    }

    @Override
    public void forEach(Consumer<? super Entry> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<Entry> spliterator() {
        return Iterable.super.spliterator();
    }

    EntrySequenceView subView(int fromIndex){
        if (entries.isEmpty() || fromIndex > lastLogIndex){
            return new EntrySequenceView(Collections.emptyList());
        }
        return new EntrySequenceView(entries.subList(fromIndex - firstLogIndex, entries.size()));
    }

}
