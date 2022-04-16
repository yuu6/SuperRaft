package com.yuu6.communite.channels;

import com.yuu6.mess.AppendEntriesReq;
import com.yuu6.mess.AppendEntriesResult;
import com.yuu6.mess.RequestVoteRpc;
import com.yuu6.mess.RequestVoteResult;
import io.netty.channel.Channel;

/**
 * 限制了只能写入四种消息
 * @Author: yuu6
 * @Date: 2022/03/26/下午2:59
 */
public class NioChannel {
    private final Channel nettyChannel;

    public NioChannel(Channel nettyChannel) {
        this.nettyChannel = nettyChannel;
    }

    public void writeRequestVoteReq(RequestVoteRpc requestVoteRpc){
        nettyChannel.writeAndFlush(requestVoteRpc);
    }

    public void writeRequsetVoteResult(RequestVoteResult result){
        nettyChannel.writeAndFlush(result);
    }

    public void writeAppendEntriesReq(AppendEntriesReq req){
        nettyChannel.writeAndFlush(req);
    }

    public void writeAppendEntriesResult(AppendEntriesResult result) {
        nettyChannel.writeAndFlush(result);
    }

    Channel getDelegate(){
        return nettyChannel;
    }

    public void close(){
        try {
            nettyChannel.close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
