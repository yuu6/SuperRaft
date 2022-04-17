package com.yuu6.raft.core.communite;

import com.google.common.eventbus.EventBus;
import com.yuu6.raft.core.node.NodeId;
import com.yuu6.raft.core.communite.channels.NioChannel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: yuu6
 * @Date: 2022/03/26/下午5:22
 */
public class ToRemoteHandler extends AbstractHandler {
    private final NodeId selfNodeId;

    public ToRemoteHandler(EventBus eventBus, NodeId remoteId, NodeId selfNodeId) {
        super(eventBus);
        this.selfNodeId = selfNodeId;
        this.remoteId = remoteId;
    }

    /**
     * 连接的时候
     *
     * @param ctx
     */
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channel active, 发送自己的NodeId:" + selfNodeId);
        ctx.write(selfNodeId);
        channel = new NioChannel(ctx.channel());
    }
}
