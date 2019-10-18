package com.example.chisu.newssalad.chatting;

import android.util.Log;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;

public class ChatClientInitializer extends ChannelInitializer<SocketChannel> implements ChatClientHandler.MessageInterface {

    private MessageInterface2 messageInterface2;

    private final SslContext sslCtx;

    // 이 센드데이터를 새롭게 정의해야 한다.
    @Override
    public void sendData(String txt) {

        messageInterface2.sendData(txt);
        Log.d("newsSalad", "chatinit :"  + txt);

    }

    public interface MessageInterface2{
        public void sendData(String txt);
    }

    public ChatClientInitializer(SslContext sslCtx, MessageInterface2 messageInterface2) {
        this.sslCtx = sslCtx;
        this.messageInterface2 = messageInterface2;
    }

    @Override
    protected void initChannel(SocketChannel arg0) throws Exception {
        ChannelPipeline pipeline = arg0.pipeline();

        //pipeline.addLast(sslCtx.newHandler(arg0.alloc(), ChatClient.HOST, ChatClient.PORT));
        pipeline.addLast(new ByteToMessageDecoder() {
            @Override
            public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                out.add(in.readBytes(in.readableBytes()));
            }
        });
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
        pipeline.addLast(new ChatClientHandler(this::sendData));
    }
}
