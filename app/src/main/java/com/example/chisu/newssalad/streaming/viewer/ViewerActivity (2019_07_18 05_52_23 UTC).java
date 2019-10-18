package com.example.chisu.newssalad.streaming.viewer;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chisu.newssalad.R;
import com.example.chisu.newssalad.chatting.ChatClientInitializer;
import com.example.chisu.newssalad.chatting.ChatRecyclerAdapter;
import com.example.chisu.newssalad.chatting.ChatRecyclerItem;
import com.example.chisu.newssalad.streaming.broadcaster.BroadCasterActivity;
import com.example.chisu.newssalad.utils.LogManager;
import com.example.chisu.newssalad.utils.URLs;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.nhancv.webrtcpeer.rtc_plugins.ProxyRenderer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;
import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;

import java.util.ArrayList;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

@EActivity(R.layout.activity_viewer)
public class ViewerActivity extends MvpActivity<ViewerView, ViewerPresenter> implements ViewerView, ChatClientInitializer.MessageInterface2 {
    private static final String TAG = ViewerActivity.class.getSimpleName();

    public ChatRecyclerAdapter adapter;

    private ArrayList<ChatRecyclerItem> recyclerItemList = new ArrayList<>();

    Channel channel;

    EditText watchChatText;
    Button watchChatButton;
    RecyclerView recyclerView;

    //서버에서 넘어오는 채팅 내용
    String gotText1;

    private void setData() {
        recyclerItemList.add(new ChatRecyclerItem(gotText1));
        adapter.notifyDataSetChanged();
    }

    ChatClientInitializer.MessageInterface2 messageInterface2 = this::sendData;

    @Override
    public void sendData(String txt) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                gotText1 = txt;

                if (gotText1.equals("closed by presenter")) {
                    channel.close();
                    Log.d("newsSalad", "채팅 내용이 넘어갔습니다.");
                    //방송 채널을 끊는다.
                    presenter.disconnect();
                    //그리고 액티비티를 종료한다.
                    Toast.makeText(getApplicationContext(), "송출자에 의해 방송이 종료되었습니다.", Toast.LENGTH_LONG).show();
                    finish();
                }

                setData();
                Log.d("newsSalad", "setText..");
            }
        });
    }

    @ViewById(R.id.vGLSurfaceViewCall)
    protected SurfaceViewRenderer vGLSurfaceViewCall;

    //방 제목
    @Extra("gotText")
    String gotText;

    private EglBase rootEglBase;
    private ProxyRenderer remoteProxyRenderer;
    private Toast logToast;

    @AfterViews
    protected void init() {
        //상태바 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //로그 매니저
        LogManager logManager;
        //최초로 생성된 로그 매니저를 가져오기.
        logManager = LogManager.getLogManagerInstance(ViewerActivity.this);
        String log = getClass().getSimpleName().trim();
        logManager.appendLog(log);


        Intent intent = getIntent();
        gotText = intent.getStringExtra("roomName");

        watchChatText = findViewById(R.id.vEditText);
        watchChatButton = findViewById(R.id.vInputBtn);
        recyclerView = findViewById(R.id.vRecyclerView);

        adapter = new ChatRecyclerAdapter(recyclerItemList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //config peer
        remoteProxyRenderer = new ProxyRenderer();
        rootEglBase = EglBase.create();

        vGLSurfaceViewCall.init(rootEglBase.getEglBaseContext(), null);
        vGLSurfaceViewCall.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        vGLSurfaceViewCall.setEnableHardwareScaler(true);
        vGLSurfaceViewCall.setMirror(true);
        remoteProxyRenderer.setTarget((VideoRenderer.Callbacks) vGLSurfaceViewCall);

        presenter.initPeerConfig(gotText);
        presenter.startCall();

        startChat();
    }

    @Override
    public void disconnect() {
        remoteProxyRenderer.setTarget(null);
        if (vGLSurfaceViewCall != null) {
            vGLSurfaceViewCall.release();
            vGLSurfaceViewCall = null;
        }

        finish();
    }

    public void startChat() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //클라이언트이므로 그룹은 하나면 됨.
                EventLoopGroup group = new NioEventLoopGroup();
                Log.d("newsSalad", "이벤트 그룹 생성 완료");

                try {
                    final SslContext sslCtx = SslContextBuilder.forClient()
                            .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

                    //설정을 위한 부트스트랩 생성 및 설정.
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new ChatClientInitializer(sslCtx, messageInterface2));
                    //attr 값은 어떤 방을 선택하느냐에 따라 달라져야 한다.

                    //채널 객체를 생성해서 부트스트랩과 연결해준다. 채널은 소켓과 같다.
                    channel = bootstrap.connect(URLs.CHAT_HOST, URLs.CHAT_PORT).sync().channel();

                    // 채널을 열자마자 바로 방 제목을 보내준다. 명령어도 보내줘야 한다.
                    // 워치이므로 방 입장 명령어도 보내야 한다.
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("command", "enter the room");
                    jsonObject.put("room name", gotText);

                    //네티의 채널리드는 스트링에만 반응하므로 스트링 오브젝트로 넘겨준다.
                    //서버에서는 넘겨받은 스트링을 다시 제이슨으로 변환해 사용한다.
                    String stringObject = jsonObject.toString();

                    channel.writeAndFlush(stringObject);
                    //이러면 방 제목이 곁들여진 방을 생성한 것이 된다.
                    Log.d("newsSalad", "방 입장 완료!! 입장한 방 이름은 : " + gotText);

                    //채팅방에 접속해 있는 상태에서 메시지를 보낼 때
                    //글이 들어 있는 상태에서 버튼을 누를 때마다 글이 보내진다.
                    watchChatButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //에딧택스트 안에 있는 글을 따온다.
                            String gotText1 = watchChatText.getText().toString();

                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("command", "send a message");
                                jsonObject.put("room name", gotText);
                                jsonObject.put("message", gotText1 + "\r\n");

                                String stringObject = jsonObject.toString();

                                channel.writeAndFlush(stringObject);

                                Log.d("newsSalad", "채팅 내용이 넘어갔습니다.");

                            } catch (Exception e) {
                                Log.d("newsSalad", e.getMessage());
                            }
                        }
                    });
                    Log.d("newsSalad", "connected");
                } catch (Exception e) {
                    Log.d("newsSalad", e.getMessage());
                }
            }
        }).start();
    }

    @NonNull
    @Override
    public ViewerPresenter createPresenter() {
        return new ViewerPresenter(getApplication());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        presenter.disconnect();
    }

    @Override
    public void stopCommunication() {
        onBackPressed();
    }

    @Override
    public void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (logToast != null) {
            logToast.cancel();
        }
        logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        logToast.show();
    }

    @Override
    public EglBase.Context getEglBaseContext() {
        return rootEglBase.getEglBaseContext();
    }

    @Override
    public VideoRenderer.Callbacks getRemoteProxyRenderer() {
        return remoteProxyRenderer;
    }

}
