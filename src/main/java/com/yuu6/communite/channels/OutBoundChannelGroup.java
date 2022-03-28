package com.yuu6.communite.channels;

import com.google.common.eventbus.EventBus;
import com.yuu6.communite.ToRemoteHandler;
import com.yuu6.node.Address;
import com.yuu6.node.NodeId;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端的所有Channel
 *
 * @Author: yuu6
 * @Date: 2022/03/26/下午2:43
 */
public class OutBoundChannelGroup {
    private final EventLoopGroup workGroup;
    private final NodeId selfNodeId;
    private final EventBus eventBus;

    // 保存所有客户端channel的map
    private static final ConcurrentHashMap<NodeId, NioChannel> channelMap = new ConcurrentHashMap<>();


    public OutBoundChannelGroup(EventLoopGroup eventLoopGroup,
                                NodeId selfNodeId,
                                EventBus eventBus) {
        this.workGroup = eventLoopGroup;
        this.selfNodeId = selfNodeId;
        this.eventBus = eventBus;
    }

    /**
     * 首先从map中获取channel, 如果获取不到，就连接
     *
     * @param nodeId
     * @param address
     * @return
     */
    public NioChannel getOrConnect(NodeId nodeId, Address address) {
        if (!channelMap.containsKey(nodeId)) {
            System.out.println("创建对于" + nodeId + "的连接！");
            try {
                channelMap.put(nodeId, connect(nodeId, address));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return channelMap.get(nodeId);
//        Future<NioChannel> future = channelMap.get(nodeId);
//        if (future == null) {
//            FutureTask<NioChannel> newFuture = new FutureTask<NioChannel>(() -> connect(nodeId, address));
//            future = channelMap.putIfAbsent(nodeId, newFuture);
//            if (future == null) {
//                future = newFuture;
//                newFuture.run();
//            }
//        }
//        try {
//            // 阻塞的获取连接
//            return future.get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    private NioChannel connect(NodeId nodeId, Address address) throws InterruptedException {
        // 所有的客户端共有workGroup
        Bootstrap bootstrap = new Bootstrap()
                .group(workGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new Decoder())
                                .addLast(new Encoder())
                                .addLast(new ToRemoteHandler(eventBus, nodeId, selfNodeId));
                    }
                });
        ChannelFuture future = bootstrap.connect(address.getHost(), address.getPort()).sync();
        if (!future.isSuccess()) {
            throw new ChannelException("fail to connect", future.cause());
        }
        future.channel().closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                channelMap.remove(nodeId);
                System.out.println("channel 被关闭！");
            }
        });

        return new NioChannel(future.channel());
    }


    public void closeAll() {
            for (NioChannel task : channelMap.values()) {
                task.close();
            }
    }
}
