package com.yuu6.communite;

import com.yuu6.mess.AppendEntriesResult;
import com.yuu6.mess.AppendEntriesReq;
import com.yuu6.mess.RequestVoteResult;
import com.yuu6.mess.RequestVoteRpc;
import com.yuu6.node.NodeEndpoint;

import java.util.Collection;

/**
 * @Author: yuu6
 * @Date: 2022/03/26/下午2:26
 */
public interface Connector {
    /**
     * 通信组建的初始化方法
     */
    void init();

    /**
     * 发送投票信息
     * @param rpc
     * @param endpoints
     */
    void sendRequestVote(RequestVoteRpc rpc, Collection<NodeEndpoint> endpoints);

    /**
     * 回复投票信息
     * @param result
     * @param endpoint
     */
    void replyRequestVote(RequestVoteResult result, NodeEndpoint endpoint);

    /**
     * 发送增加条目信息
     * @param rpc
     * @param endpoint
     */
    void sendAppendEntries(AppendEntriesReq rpc, NodeEndpoint endpoint);

    /**
     * 响应条目消息
     * @param result
     * @param endpoint
     */
    void replyAppendEntries(AppendEntriesResult result, NodeEndpoint endpoint);


    void resetChannels();


    void close();
}
