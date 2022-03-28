package com.yuu6.communite.channels;

import com.google.gson.Gson;
import com.yuu6.mess.AppendEntriesReq;
import com.yuu6.mess.AppendEntriesResult;
import com.yuu6.mess.RequestVoteReq;
import com.yuu6.mess.RequestVoteResult;
import com.yuu6.node.NodeId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Author: yuu6
 * @Date: 2022/03/26/下午3:34
 */
public class Decoder extends ByteToMessageDecoder {
    private static final Gson gson = new Gson();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int availableBytes = in.readableBytes();
        if (availableBytes < 8){
            return;
        }

        in.markReaderIndex();
        int messageType = in.readInt();
        int len = in.readInt();
        if (in.readableBytes() < len){
            in.resetReaderIndex();
            return;
        }
        byte[] payload = new byte[len];
        in.readBytes(payload);

        switch (messageType){
            case MessageConstants.MSG_TYPE_NODE_ID:
                out.add(new NodeId(new String(payload)));
                break;
            case MessageConstants.MSG_TYPE_REQUEST_VOTE_REQ:
                RequestVoteReq requestVoteReq = gson.fromJson(new String(payload), RequestVoteReq.class);
                out.add(requestVoteReq);
                break;
            case MessageConstants.MSG_TYPE_REQUEST_VOTE_RESULT:
                RequestVoteResult result = gson.fromJson(new String(payload), RequestVoteResult.class);
                out.add(result);
                break;
            case MessageConstants.MSG_TYPE_APPEND_ENTRY_REQ:
                AppendEntriesReq req = gson.fromJson(new String(payload), AppendEntriesReq.class);
                out.add(req);
                break;
            case MessageConstants.MSG_TYPE_APPEND_ENTRY_RESULT:
                AppendEntriesResult result1 = gson.fromJson(new String(payload), AppendEntriesResult.class);
                out.add(result1);
                break;
        }
    }
}
