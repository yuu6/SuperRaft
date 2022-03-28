package com.yuu6.role;

/**
 * 抽象角色类
 */
public abstract class AbstractNodeRole {
    /**
     * 角色名称
     */
    private final RoleName name;
    /**
     * 当前的轮次
     */
    protected final int term;

    AbstractNodeRole(RoleName name, int term){
        this.name = name;
        this.term = term;
    }
    public RoleName getName(){
        return name;
    }

    public abstract void cancelTimeoutOrTask();

    public int getTerm(){
        return term;
    }
}
