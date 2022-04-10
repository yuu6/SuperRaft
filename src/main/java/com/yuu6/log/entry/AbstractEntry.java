package com.yuu6.log.entry;

import com.yuu6.log.EntryMeta;

/**
 * @Author: yuu6
 * @Date: 2022/03/30/上午8:11
 */
public abstract class AbstractEntry implements Entry {
    private final int kind;
    protected final int index;
    protected final int term;

    public AbstractEntry(int kind, int index, int term) {
        this.kind = kind;
        this.index = index;
        this.term = term;
    }

    public int getKind(){
        return this.kind;
    }

    public int getIndex(){
        return index;
    }

    public int getTerm(){
        return term;
    }

    public EntryMeta getMeta(){
        return new EntryMeta(kind, index, term);
    }


}
