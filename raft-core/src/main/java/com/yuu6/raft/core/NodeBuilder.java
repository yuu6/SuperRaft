package com.yuu6.raft.core;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.yuu6.raft.core.communite.Connector;
import com.yuu6.raft.core.communite.ConnectorImpl;
import com.yuu6.raft.core.election.Node;
import com.yuu6.raft.core.election.NodeContext;
import com.yuu6.raft.core.election.NodeImpl;
import com.yuu6.raft.core.election.NodeStoreImpl.MemoryNodeStore;
import com.yuu6.raft.core.election.SingleThreadTaskExecutor;
import com.yuu6.raft.core.election.TaskExecutor;
import com.yuu6.raft.core.election.scheduler.DefaultScheduler;
import com.yuu6.raft.core.election.scheduler.Scheduler;
import com.yuu6.raft.core.log.FileLog;
import com.yuu6.raft.core.node.Address;
import com.yuu6.raft.core.node.NodeEndpoint;
import com.yuu6.raft.core.node.NodeGroup;
import com.yuu6.raft.core.node.NodeId;
import io.netty.channel.nio.NioEventLoopGroup;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yuu6
 * @Date: 2022/03/27/下午2:44
 */
public class NodeBuilder {
    // 集群成员
    private final NodeGroup nodeGroup;
    //节点ID
    private final NodeId nodeId;
    private final EventBus eventBus;
    private Scheduler scheduler = null;
    private Connector connector = null;
    private TaskExecutor taskExecutor = null;
    private String logSuffix = "";

    public NodeBuilder(NodeEndpoint endpoint, EventBus eventBus, String logSuffix){
        this(Collections.singletonList(endpoint), endpoint.getId(), eventBus, logSuffix);
    }

    public NodeBuilder(List<NodeEndpoint> endpoints, NodeId nodeId, EventBus eventBus, String logSuffix){
        this.nodeGroup = new NodeGroup(endpoints, nodeId);
        this.nodeId = nodeId;
        this.eventBus = eventBus;
        this.logSuffix = logSuffix;
    }

    NodeBuilder setConnector(Connector connector){
        this.connector = connector;
        return this;
    }

    NodeBuilder setScheduler(Scheduler scheduler){
        this.scheduler = scheduler;
        return this;
    }

    NodeBuilder setTaskExecutor(TaskExecutor taskExecutor){
        this.taskExecutor = taskExecutor;
        return this;
    }

    public Node build(){
        return new NodeImpl(buildContext());
    }

    private NodeContext buildContext() {
        NodeContext context = new NodeContext();
        context.setStore(new MemoryNodeStore());
        context.setNodeGroup(nodeGroup);
        context.setSelfId(nodeId);
        context.setEventBus(eventBus);
        context.setScheduler(scheduler != null ? scheduler : new DefaultScheduler(8000, 120000, 1000, 1000));
        context.setConnector(connector);
        context.setTaskExecutor(
                taskExecutor != null ? taskExecutor : new SingleThreadTaskExecutor("node")
        );
        // 设置日志状态及
        context.setLog(new FileLog(new File("./log-" + logSuffix)));
        return context;
    }


    private static Map<String, NodeEndpoint> nodeMap(){
        NodeEndpoint a = new NodeEndpoint(new NodeId("a"), new Address("127.0.0.1", 7001));
        NodeEndpoint b = new NodeEndpoint(new NodeId("b"), new Address("127.0.0.1", 7002));
        NodeEndpoint c = new NodeEndpoint(new NodeId("c"), new Address("127.0.0.1", 7003));
        return new HashMap<String, NodeEndpoint>(){{
            put("a", a);
            put("b", b);
            put("c", c);
        }};
    }

    public static NodeBuilder getBuilder(String nodeId, String suffix, Map<String, NodeEndpoint> nodeMap){
//        Map<String, NodeEndpoint> nodeMap = nodeMap();
        NodeEndpoint selfNode = nodeMap.get(nodeId);
        EventBus eventBus = new EventBus(nodeId);

        NodeBuilder nodeBuilder = new NodeBuilder(Lists.newArrayList(nodeMap.values()), selfNode.getId(), eventBus, suffix);
        // 创建工作者线程
        NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
        // 创建通信模块
        ConnectorImpl connector = new ConnectorImpl(workGroup, selfNode.getAddress().getPort(), eventBus, selfNode.getId(), false);
        nodeBuilder.setConnector(connector);
        nodeBuilder.setTaskExecutor(new SingleThreadTaskExecutor());
        return nodeBuilder;
    }

//    public static void main(String[] args) {
//
//        NodeBuilder aBuilder = getBuilder(args[0], args[1]);
//        Node a = aBuilder.build();
//        a.start();
//    }
}
