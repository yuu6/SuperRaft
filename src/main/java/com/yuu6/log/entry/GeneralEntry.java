package com.yuu6.log.entry;

import java.util.Arrays;

/**
 * @Author: yuu6
 * @Date: 2022/03/30/上午8:15
 */
public class GeneralEntry extends AbstractEntry {
    private final byte[] commandBytes;

    public GeneralEntry(int index, int term, byte[] commandBytes){
        super(KIND_GENERAL, index, term);
        this.commandBytes = commandBytes;
    }
    @Override
    public byte[] getCommandBytes() {
        return this.commandBytes;
    }

    @Override
    public String toString() {
        return "GeneralEntry{" +
                "index=" + index +
                ", term=" + term +
                ", commandBytes=" + Arrays.toString(commandBytes) +
                '}';
    }
}
