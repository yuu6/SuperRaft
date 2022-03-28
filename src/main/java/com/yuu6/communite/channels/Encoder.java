package com.yuu6.communite.channels;

import com.google.gson.Gson;
import com.yuu6.mess.AppendEntriesReq;
import com.yuu6.mess.AppendEntriesResult;
import com.yuu6.mess.RequestVoteReq;
import com.yuu6.mess.RequestVoteResult;
import com.yuu6.node.NodeId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @Author: yuu6
 * @Date: 2022/03/26/下午3:35
 */
public class Encoder extends MessageToByteEncoder<Object> {
    private final Gson gson = new Gson();

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof NodeId){
            this.writeMessage(out, MessageConstants.MSG_TYPE_NODE_ID, ((NodeId) msg).getValue().getBytes());
        }else if(msg instanceof RequestVoteReq){
            RequestVoteReq req = (RequestVoteReq) msg;
            String str = gson.toJson(req);
            this.writeMessage(out, MessageConstants.MSG_TYPE_REQUEST_VOTE_REQ, str);
        }else if(msg instanceof RequestVoteResult){
            RequestVoteResult result = (RequestVoteResult) msg;
            String str = gson.toJson(result);
            this.writeMessage(out, MessageConstants.MSG_TYPE_REQUEST_VOTE_RESULT, str);
        }else if(msg instanceof AppendEntriesReq){
            AppendEntriesReq req = (AppendEntriesReq) msg;
            String str = gson.toJson(req);
            this.writeMessage(out, MessageConstants.MSG_TYPE_APPEND_ENTRY_REQ, str);
        }else if(msg instanceof AppendEntriesResult){
            AppendEntriesResult result = (AppendEntriesResult) msg;
            String str = gson.toJson(result);
            this.writeMessage(out, MessageConstants.MSG_TYPE_APPEND_ENTRY_RESULT, str);
        }
    }

    private void writeMessage(ByteBuf out, int type, byte[] bytes) {
        out.writeInt(type);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

    private void writeMessage(ByteBuf out, int type, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        writeMessage(out,type,bytes);
    }
}
