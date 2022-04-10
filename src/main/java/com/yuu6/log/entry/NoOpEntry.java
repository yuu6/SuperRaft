package com.yuu6.log.entry;

/**
 * @Author: yuu6
 * @Date: 2022/03/30/上午8:18
 */
public class NoOpEntry extends AbstractEntry {

    public NoOpEntry(int index, int term){
        super(KIND_NO_OP, index, term);
    }

    @Override
    public byte[] getCommandBytes() {
        return new byte[0];
    }

    @Override
    public String toString() {
        return "NoOpEntry{" +
                "index=" + index +
                ", term=" + term +
                '}';
    }
}
