package com.yuu6.log;

import com.yuu6.log.entry.Entry;
import com.yuu6.log.entry.GeneralEntry;
import com.yuu6.log.entry.NoOpEntry;

/**
 * @Author: yuu6
 * @Date: 2022/03/31/下午10:56
 */
public class EntryFactory {

    public Entry create(int kind, int index, int term, byte[] commandBytes){
        switch (kind){
            case Entry.KIND_NO_OP:
                return new NoOpEntry(index, term);
            case Entry.KIND_GENERAL:
                return new GeneralEntry(index, term, commandBytes);
            default:
                throw new IllegalArgumentException("unexpected entry kind " + kind);
        }
    }
}
