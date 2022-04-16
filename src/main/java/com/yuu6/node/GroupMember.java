package com.yuu6.node;

/**
 * 组成员
 */
public class GroupMember {

    private final NodeEndpoint endpoint;

    // 复制状态
    private ReplicatingState replicatingState;

    public GroupMember(NodeEndpoint endpoint) {
//        this.endpoint = endpoint;
        this(endpoint, new ReplicatingState(0, 0));
    }
    // 带日志的构造器
    public GroupMember(NodeEndpoint endpoint, ReplicatingState replicatingState){
        this.endpoint = endpoint;
        this.replicatingState = replicatingState;
    }

    public int getNextIndex(){
        return ensureReplicatingState().getNextIndex();
    }

    public int getMatchIndex(){
        return ensureReplicatingState().getMatchIndex();
    }

    private ReplicatingState ensureReplicatingState(){
        if (replicatingState == null){
            throw new IllegalStateException("replication state not set");
        }
        return replicatingState;
    }

    public boolean advanceReplicatingState(int index){
        ReplicatingState replicatingState = ensureReplicatingState();
        replicatingState.setMatchIndex(index);
        replicatingState.setNextIndex(index + 1);
        return true;
    }


    public NodeEndpoint getEndpoint() {
        return endpoint;
    }

    // 复制进度
    public static class ReplicatingState {
        // 匹配的下标
        private int matchIndex;
        // 下一个下标
        private int nextIndex;

        public ReplicatingState(int matchIndex, int nextIndex) {
            this.matchIndex = matchIndex;
            this.nextIndex = nextIndex;
        }

        public int getMatchIndex() {
            return matchIndex;
        }

        public int getNextIndex() {
            return nextIndex;
        }

        public void setMatchIndex(int matchIndex) {
            this.matchIndex = matchIndex;
        }

        public void setNextIndex(int nextIndex) {
            this.nextIndex = nextIndex;
        }
    }
}