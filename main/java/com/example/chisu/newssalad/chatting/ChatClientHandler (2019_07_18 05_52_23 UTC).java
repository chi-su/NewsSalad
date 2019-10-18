package com.example.chisu.newssalad.chatting;
import android.util.Log;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ChatClientHandler extends ChannelInboundHandlerAdapter {

    //인터페이스의 객체 선언. 이 객체에는 sendData 메소드가 담겨 있다.
    private MessageInterface messageInterface;
    //클래스의 생성자.
    ChatClientHandler(MessageInterface messageInterface){
        this.messageInterface = messageInterface;
    }

    //인터페이스 선언
    public interface MessageInterface{
        public void sendData(String txt);
    }

    String gotText;

    @Override
    public void channelRead(ChannelHandlerContext arg0, Object arg1) throws Exception {
        gotText = (String)arg1;

        Log.d("newsSalad", (String)arg1);
        //정보를 받긴하는데.. 어떻게 보내지?
        messageInterface.sendData((String)arg1);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        Log.d("newsSalad", "error : " + cause.getMessage());

    }
}

