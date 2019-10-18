package com.example.chisu.newssalad.chatting;
import android.util.Log;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

//네티 핸들러를 여기서도 만들었다. 사실상 작은 채팅 서버를 만드는 셈.
public class ChatClientHandler extends ChannelInboundHandlerAdapter {

    //넘어온 채팅 데이터를 담을 스트링 객체.
    String gotText;

    //인터페이스의 객체 선언. 이 객체에는 sendData 메소드가 담겨 있다.
    private MessageInterface messageInterface;
    //클래스의 생성자.
    ChatClientHandler(MessageInterface messageInterface){
        this.messageInterface = messageInterface;
    }

    //인터페이스 선언. 액티비티에 채팅 텍스트 정보를 보내기 위해 인터페이스를 사용했다.
    public interface MessageInterface{
        public void sendData(String txt);
    }


    //서버에서 넘어오는 채팅 정보를 받는 메소드.
    @Override
    public void channelRead(ChannelHandlerContext arg0, Object arg1) throws Exception {
        gotText = (String)arg1;

        Log.d("newsSalad", (String)arg1);
        //정보를 받긴하는데.. 어떻게 보내지?
        messageInterface.sendData((String)arg1);

    }

    //에러 발생 시 발동하는 메소드.
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        Log.d("newsSalad", "error : " + cause.getMessage());

    }
}

