package com.yuu6.raft.core.node;

import java.util.*;
import java.util.stream.Collectors;

public class NodeGroup {
    private final NodeId selfId;

    private Map<NodeId, GroupMember> memberMap;

    public NodeGroup(NodeEndpoint endpoint){
        this(Collections.singleton(endpoint), endpoint.getId());
    }

    public NodeGroup(Collection<NodeEndpoint> endpoints, NodeId nodeId){
        this.memberMap = buildMemeberMap(endpoints);
        this.selfId = nodeId;
    }

    private Map<NodeId, GroupMember> buildMemeberMap(Collection<NodeEndpoint> endpoints){
        Map<NodeId, GroupMember> map = new HashMap<>();
        for (NodeEndpoint endpoint: endpoints) {
            map.put(endpoint.getId(), new GroupMember(endpoint));
        }
        if (map.isEmpty()){
            throw new IllegalArgumentException("endpoint is empty!!");
        }
        return map;
    }

    public GroupMember findMember(NodeId id){
        GroupMember member = getMember(id);
        if (member == null){
            throw new IllegalArgumentException("no such node" + id);
        }
        return member;
    }
    public GroupMember getMember(NodeId id){
        return memberMap.get(id);
    }

    public Collection<GroupMember> listReplicationTarget() {
        return memberMap.values().stream().filter(
                m -> !m.getEndpoint().getId().equals(selfId)
        ).collect(Collectors.toList());
    }

    public Set<NodeEndpoint> listEndpointExceptSelf(){
        Set<NodeEndpoint> endpoints = new HashSet<NodeEndpoint>();
        for (GroupMember member: memberMap.values()) {
            if (!member.getEndpoint().getId().equals(selfId)){
                endpoints.add(member.getEndpoint());
            }
        }
        return endpoints;
    }

    public int getCount() {
        return memberMap.size();
    }

    public int getMatchIndexOfMajor(){
        List<NodeMatchIndex> matchIndices = new ArrayList<>();
        for (GroupMember member:memberMap.values()) {
            if (!member.equals(selfId)){
                matchIndices.add(new NodeMatchIndex(member.getEndpoint().getId(), member.getMatchIndex()));
            }
        }

        int count = matchIndices.size();
        if (count == 0){
            throw new IllegalStateException("standalone or no major node");
        }
        Collections.sort(matchIndices);
        return matchIndices.get(count / 2).getMatchIndex();
    }

    public static class NodeMatchIndex implements Comparable<NodeMatchIndex>{
        // 节点ID
        private final NodeId nodeId;
        // 匹配的下标
        private final int matchIndex;

        @Override
        public String toString() {
            return "NodeMatchIndex{" +
                    "nodeId=" + nodeId +
                    ", matchIndex=" + matchIndex +
                    '}';
        }


        NodeMatchIndex(NodeId nodeId, int matchIndex){
            this.nodeId = nodeId;
            this.matchIndex = matchIndex;
        }

        @Override
        public int compareTo(NodeMatchIndex o) {
            return Integer.compare(this.matchIndex, o.matchIndex);
        }

        public NodeId getNodeId() {
            return nodeId;
        }

        public int getMatchIndex() {
            return matchIndex;
        }
    }
}
