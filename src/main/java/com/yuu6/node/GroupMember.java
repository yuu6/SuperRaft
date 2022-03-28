package com.yuu6.node;

public class GroupMember {

    private final NodeEndpoint endpoint;

    //    private ReplicatingState replicatingState;
    GroupMember(NodeEndpoint endpoint) {
//        this(endpoint, null);
        this.endpoint = endpoint;
    }
//    GroupMember(NodeEndpoint endpoint, ReplicatingState replicatingState){
//        this.endpoint = endpoint;
//        this.replicatingState = replicatingState;
//    }

//    int getNextIndex(){
//        return ensureReplicatingState().getNextIndex();
//    }
//
//    int getMatchIndex(){
//        return ensureReplicatingState().getMatchIndex();
//    }
//
//    private ReplicatingState ensureReplicatingState(){
//        if (replicatingState == null){
//            throw new IllegalStateException("replication state not set");
//        }
//        return replicatingState;
//    }


    public NodeEndpoint getEndpoint() {
        return endpoint;
    }

//    public ReplicatingState getReplicatingState() {
//        return replicatingState;
//    }
//
//    public void setReplicatingState(ReplicatingState replicatingState) {
//        this.replicatingState = replicatingState;
//    }
//}
}