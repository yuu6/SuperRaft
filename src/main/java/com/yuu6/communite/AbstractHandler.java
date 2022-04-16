package com.yuu6.communite;

import com.google.common.eventbus.EventBus;
import com.yuu6.communite.channels.NioChannel;
import com.yuu6.election.AppendEntriesRpcMessage;
import com.yuu6.election.AppendEntriesResultMessage;
import com.yuu6.mess.*;
import com.yuu6.node.NodeId;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: yuu6
 * @Date: 2022/03/26/下午4:33
 */
public class AbstractHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractHandler.class);
    /**
     * 远端的ID是多少
     */
    NodeId remoteId;
    private AppendEntriesReq lastAppendEntriesReq;

    NioChannel channel;
    protected final EventBus eventBus;

    AbstractHandler(EventBus eventBus){
        this.eventBus = eventBus;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RequestVoteRpc){
            RequestVoteRpc req = (RequestVoteRpc) msg;
            System.out.println(String.format("接收到了 %s 请求投票的消息",  remoteId));
            // 投票的请求
            eventBus.post(new RequestVoteRpcMessage(req, remoteId, channel));
        }else if (msg instanceof RequestVoteResult){
            RequestVoteResult result = (RequestVoteResult) msg;
            System.out.println("处理投票结果："+  result.isVoteGranted());
            eventBus.post(result);
            logger.debug("get RequestVoteResult {}", msg);
        }else if (msg instanceof AppendEntriesResult){
            AppendEntriesResult result = (AppendEntriesResult) msg;
            if (lastAppendEntriesReq != null){
                logger.debug("get AppendEntriesResult {}", msg);
                eventBus.post(new AppendEntriesResultMessage(result, remoteId, lastAppendEntriesReq));
                lastAppendEntriesReq = null;
            }
        }else if (msg instanceof AppendEntriesReq){
            AppendEntriesReq result = (AppendEntriesReq) msg;
            System.out.println("收到了心跳消息！！");
            logger.debug("get AppendEntriesReq {}", msg);
            eventBus.post(new AppendEntriesRpcMessage(result, remoteId));
        }
    }


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof AppendEntriesReq){
            lastAppendEntriesReq = (AppendEntriesReq) msg;
        }
        super.write(ctx, msg, promise);
    }

}
