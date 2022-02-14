package Node;

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

    GroupMember findMember(NodeId id){
        GroupMember member = getMember(id);
        if (member == null){
            throw new IllegalArgumentException("no such node" + id);
        }
        return member;
    }
    GroupMember getMember(NodeId id){
        return memberMap.get(id);
    }
    Collection<GroupMember> listReplicationTarget() {
        return memberMap.values().stream().filter(
                m -> !m.getEndpoint().getId().equals(selfId)).collect(Collectors.toList());
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
}
