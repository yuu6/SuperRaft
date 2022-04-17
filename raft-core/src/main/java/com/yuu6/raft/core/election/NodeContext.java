package com.yuu6.raft.core.election;

import com.google.common.eventbus.EventBus;
import com.yuu6.raft.core.communite.Connector;
import com.yuu6.raft.core.election.scheduler.Scheduler;
import com.yuu6.raft.core.log.Log;
import com.yuu6.raft.core.node.GroupMember;
import com.yuu6.raft.core.node.NodeGroup;
import com.yuu6.raft.core.node.NodeId;

/**
 * 间接层类
 */
public class NodeContext {
    private NodeId selfId;
    private NodeGroup nodeGroup;
    // 通信组件
    private Connector connector;
    // 调度组件
    private Scheduler scheduler;

    private EventBus eventBus;
    // 任务执行期
    private TaskExecutor taskExecutor;
    private NodeStore store;
    private Log log;

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public NodeId selfId(){
        return selfId;
    }

    public void setSelfId(NodeId nodeId){
        this.selfId = nodeId;
    }

    public NodeGroup getNodeGroup() {
        return nodeGroup;
    }

    public void setNodeGroup(NodeGroup nodeGroup) {
        this.nodeGroup = nodeGroup;
    }

    public Connector getConnector() {
        return connector;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public NodeStore getStore() {
        return store;
    }

    public void setStore(NodeStore store) {
        this.store = store;
    }

    public GroupMember findMember(NodeId nodeId) {
        return nodeGroup.findMember(nodeId);
    }
}
