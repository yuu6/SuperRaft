package com.yuu6.raft.core.communite;

import com.google.common.eventbus.EventBus;
import com.yuu6.raft.core.node.NodeId;
import com.yuu6.raft.core.communite.channels.InBoundChannelGroup;
import com.yuu6.raft.core.communite.channels.NioChannel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: yuu6
 * @Date: 2022/03/26/下午4:30
 */
public class FromRemoteHandler extends AbstractHandler {

    // 服务端的channel
    private final InBoundChannelGroup inBoundChannelGroup;

    FromRemoteHandler(EventBus eventBus,
                      InBoundChannelGroup inBoundChannelGroup) {
        super(eventBus);
        this.inBoundChannelGroup = inBoundChannelGroup;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof NodeId){
            System.out.println("收到了 nodeId:" + msg);
            remoteId = (NodeId) msg;
            NioChannel nioChannel = new NioChannel(ctx.channel());
            inBoundChannelGroup.add(remoteId, nioChannel);
            return;
        }
        super.channelRead(ctx, msg);
    }
}
