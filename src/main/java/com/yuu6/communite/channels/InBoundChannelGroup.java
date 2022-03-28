package com.yuu6.communite.channels;

import com.yuu6.node.NodeId;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.internal.PlatformDependent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: yuu6
 * @Date: 2022/03/26/下午2:43
 */
public class InBoundChannelGroup {
    private final List<NioChannel> channels = new ArrayList<NioChannel>();
    private final ConcurrentMap<NodeId, NioChannel> serverChannels = PlatformDependent.newConcurrentHashMap();

    public void add(NodeId remoteId, NioChannel channel){
        boolean added = serverChannels.putIfAbsent(remoteId, channel) == null;
        if (added) {
            channel.getDelegate().closeFuture().addListener((ChannelFutureListener) future -> {
                remove(channel);
            });
        }
    }

    private void remove(NioChannel channel){
        channels.remove(channel);
    }

    public void closeAll(){
        for (NioChannel channel: channels){
            channel.close();
        }
    }
}
