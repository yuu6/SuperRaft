package com.yuu6.raft.start.config;

import com.yuu6.raft.start.domain.RaftNode;
import com.yuu6.raft.start.domain.ClusterNodes;
import com.yuu6.raft.core.NodeBuilder;
import com.yuu6.raft.core.election.Node;
import com.yuu6.raft.core.node.Address;
import com.yuu6.raft.core.node.NodeEndpoint;
import com.yuu6.raft.core.node.NodeId;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yuu6
 * @Date: 2022/04/16/下午6:59
 */
@Configuration
public class SuperRaftConfig {

    @ConfigurationProperties(prefix = "super-raft")
    @Bean(name = "clusterNodes")
    public ClusterNodes createClusterNode(){
        return new ClusterNodes();
    }

    @Bean(name = "raftNode")
    public Node start(ClusterNodes clusterNodes){
        Map<String, NodeEndpoint> nodeMap = new HashMap<>();
        for (RaftNode node: clusterNodes.getNodeList()) {
            String key = node.getName();
            NodeId nodeId = new NodeId(node.getName());
            Address address = new Address(node.getHost(), node.getPort());
            NodeEndpoint endpoint = new NodeEndpoint(nodeId, address);
            nodeMap.put(key, endpoint);
        }
        NodeBuilder nodeBuilder = NodeBuilder.getBuilder(clusterNodes.getSelfName(), clusterNodes.getSelfName(), nodeMap);
        Node node = nodeBuilder.build();
        node.start();
        return node;
    }
}
