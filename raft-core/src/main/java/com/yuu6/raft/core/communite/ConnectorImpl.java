package com.yuu6.raft.core.communite;

import com.google.common.eventbus.EventBus;
import com.yuu6.raft.core.communite.channels.*;
import com.yuu6.raft.core.mess.AppendEntriesReq;
import com.yuu6.raft.core.mess.AppendEntriesResult;
import com.yuu6.raft.core.mess.RequestVoteResult;
import com.yuu6.raft.core.mess.RequestVoteRpc;
import com.yuu6.raft.core.node.NodeEndpoint;
import com.yuu6.raft.core.node.NodeId;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @Author: yuu6
 * @Date: 2022/03/26/下午2:34
 */
public class ConnectorImpl implements Connector{
    private static final Logger logger = LoggerFactory.getLogger(ConnectorImpl.class);

    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);

    private final NioEventLoopGroup workGroup;
    // 对应客户端的channel
    private final OutBoundChannelGroup outBoundChannelGroup;
    //对应服务器的channel
    private final InBoundChannelGroup inBoundChannelGroup = new InBoundChannelGroup();
    private final Integer port;
    private final EventBus eventBus;
    private final boolean  workGroupShared;

    public ConnectorImpl(
            NioEventLoopGroup workGroup,
            Integer port,
            EventBus eventBus,
            NodeId selfNodeId,
            boolean workGroupShared) {
        this.workGroup = workGroup;
        // 初始化对外连接的channels
        outBoundChannelGroup = new OutBoundChannelGroup(workGroup, selfNodeId, eventBus);
        this.port = port;
        this.eventBus = eventBus;
        this.workGroupShared = workGroupShared;
    }


    @Override
    public void init() {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new Decoder());
                        pipeline.addLast(new Encoder());
                        pipeline.addLast(new FromRemoteHandler(eventBus, inBoundChannelGroup));
                    }
                });
        try {
            serverBootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendRequestVote(RequestVoteRpc req, Collection<NodeEndpoint> endpoints) {
        for (NodeEndpoint endpoint : endpoints) {
            System.out.println(String.format("向%s发送请求投票消息",  endpoint.getId()));
            try {
                NioChannel nioChannel = outBoundChannelGroup.getOrConnect(endpoint.getId(), endpoint.getAddress());
                nioChannel.writeRequestVoteReq(req);
            }catch (Exception e){
                logger.error("connect node {} fail!", endpoint.getAddress());
            }
        }
    }

    @Override
    public void replyRequestVote(RequestVoteResult result, NodeEndpoint endpoint) {
        logger.info("reply request vote to {}", endpoint.getAddress());
        NioChannel nioChannel = outBoundChannelGroup.getOrConnect(endpoint.getId(), endpoint.getAddress());
        nioChannel.writeRequsetVoteResult(result);
    }

    @Override
    public void sendAppendEntries(AppendEntriesReq req, NodeEndpoint endpoint) {
        System.out.println(String.format("Leader给%s发送心跳请求", endpoint.getId()));
        try {
            NioChannel nioChannel = outBoundChannelGroup.getOrConnect(endpoint.getId(), endpoint.getAddress());
            nioChannel.writeAppendEntriesReq(req);
        }catch (Exception e){
            System.out.println(String.format("Leader给%s发送心跳请求失败", endpoint.getId()));
        }
    }

    @Override
    public void replyAppendEntries(AppendEntriesResult result, NodeEndpoint endpoint) {
        System.out.println("响应心跳请求");
        NioChannel nioChannel = outBoundChannelGroup.getOrConnect(endpoint.getId(), endpoint.getAddress());
        nioChannel.writeAppendEntriesResult(result);
    }

    @Override
    public void resetChannels() {

    }

    @Override
    public void close() {
        inBoundChannelGroup.closeAll();
        outBoundChannelGroup.closeAll();
        bossGroup.shutdownGracefully();
        if (!workGroupShared){
            workGroup.shutdownGracefully();
        }

    }
}
