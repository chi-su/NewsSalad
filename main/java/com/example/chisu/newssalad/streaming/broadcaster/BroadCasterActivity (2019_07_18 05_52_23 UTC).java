package com.example.chisu.newssalad.streaming.broadcaster;

import android.Manifest;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
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
import com.example.chisu.newssalad.utils.SharedPreferenceManager;
import com.example.chisu.newssalad.utils.User;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.nhancv.npermission.NPermission;
import com.nhancv.webrtcpeer.rtc_plugins.ProxyRenderer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
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

/**
 * 방송자가 실제로 채팅과 방송을 실시하며 서버로 전송하는 액티비티.
 */
@EActivity(R.layout.activity_broadcaster)
public class BroadCasterActivity extends MvpActivity<BroadCasterView, BroadCasterPresenter>
        implements BroadCasterView, NPermission.OnPermissionResult, ChatClientInitializer.MessageInterface2 {

    //로그용 태그
    private static final String TAG = BroadCasterActivity.class.getSimpleName();

    //채팅 리사이클러뷰 어댑터
    public ChatRecyclerAdapter adapter;
    //채팅 리스트 아이템
    private ArrayList<ChatRecyclerItem> recyclerItemList = new ArrayList<>();

    //채팅 서버 호스트 및 포트
    private static final String HOST = "13.125.67.216";
    private static final int PORT = 5001;

    private long lastTimeBackPressed; //뒤로가기 버튼이 클릭된 시간
    @Override
    public void onBackPressed() {
        //2초 이내에 뒤로가기 버튼을 재 클릭 시 앱 종료
        if (System.currentTimeMillis() - lastTimeBackPressed < 2000) {
            finish();
            return;
        }
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }

    @ViewById(R.id.vGLSurfaceViewCall)
    protected SurfaceViewRenderer vGLSurfaceViewCall;

    //서버에 보낼 방송의 제목을 넘겨받은 것. 인텐트로 넘겨받은 것을 서버로 보내야 함.
    @Extra("gotText")
    String gotText;
    //위와 같이 넘겨받은 방송의 장르.
    String gotGenre;
    //서버에서 전달받은 메시지. 즉 리사이클러뷰에 표시할 채팅 내용
    String gotChat;

    EditText editText;
    Button chatBtn;
    //    Button bExitBtn;
    RecyclerView recyclerView;

    //서버와 연결할 채널
    Channel channel;

    //ChatClientHandler에서 넘겨받은 채팅 데이터를 이 액티비티까지 끌고오기 위해
    //인터페이스를 사용. 중간에 initalizer 때문에 총 2개의 인터페이스를 사용했다.
    ChatClientInitializer.MessageInterface2 messageInterface2 = this::sendData;

    //채팅 데이터가 이 액티비티로 넘어오면 그걸 채팅 리사이클러뷰에 표시해주는 메소드
    @Override
    public void sendData(String txt) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                gotChat = txt;
                setData();
                Log.d("newsSalad", "sendData OK..");
            }
        });
    }

    //채팅 리사이클러뷰에 아이템을 추가하고 갱신하는 메소드
    private void setData() {
        recyclerItemList.add(new ChatRecyclerItem(gotChat));
        adapter.notifyDataSetChanged();
    }

    private NPermission nPermission;
    private EglBase rootEglBase;
    private ProxyRenderer localProxyRenderer;
    private Toast logToast;
    private boolean isGranted;

    //클라 녹화 관련 코드 : 실패
//    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 1;
//    private static final String VIDEO_TRACK_ID = "video_stream";
//    PeerConnectionFactory peerConnectionFactory;
//    SurfaceViewRenderer localVideoView;
//    VideoSource videoSource;
//    VideoTrack localVideoTrack;
//    @Nullable
//    private VideoFileRenderer videoFileRenderer;
//    int timeoutUs = 1000;
//    MediaMuxer mediaMuxer;
//    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE =
//            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE";
//    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH =
//            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_WIDTH";
//    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT =
//            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT";

    @AfterViews
    protected void init() {
        //상태 바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        gotText = intent.getStringExtra("roomName");
        gotGenre = intent.getStringExtra("genre");

        editText = findViewById(R.id.bEditText);
        chatBtn = findViewById(R.id.bInputBtn);
        recyclerView = findViewById(R.id.bRecyclerView);

        adapter = new ChatRecyclerAdapter(recyclerItemList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        nPermission = new NPermission(true);

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        //config peer
        localProxyRenderer = new ProxyRenderer();
        rootEglBase = EglBase.create();

        vGLSurfaceViewCall.init(rootEglBase.getEglBaseContext(), null);
        vGLSurfaceViewCall.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        vGLSurfaceViewCall.setEnableHardwareScaler(true);
        vGLSurfaceViewCall.setMirror(true);
        localProxyRenderer.setTarget(vGLSurfaceViewCall);

        presenter.initPeerConfig(gotText);

        //채팅 메소드 시작.
        startChat();
        //클라이언트 녹화 코드(중단)
//        try {
//            mediaMuxer = new MediaMuxer("/storage/emulated/0/777/temp.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
//            remoteSinks.add((ProxyVideoSink) remoteProxyRenderer);
//            String saveRemoteVideoToFile = intent.getStringExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);
//            if (saveRemoteVideoToFile != null) {
//                int videoOutWidth = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
//                int videoOutHeight = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
//                try {
//                    videoFileRenderer = new VideoFileRenderer(
//                            saveRemoteVideoToFile, videoOutWidth, videoOutHeight, rootEglBase.getEglBaseContext());
//                    remoteSinks.add((VideoSink) videoFileRenderer);
//
//                    MediaCodec codec = MediaCodec.createDecoderByType("video/avc");//H.264/AVC video
//
//                    ByteBuffer[] inputBuffers = codec.getInputBuffers();
//                    ByteBuffer[] outputBuffers = codec.getOutputBuffers();
//
//                    int inputBufferIndex = codec.dequeueInputBuffer(timeoutUs);
//                    if (inputBufferIndex >= 0) {
//                        // fill inputBuffers[inputBufferIndex] with valid data
//
//                        codec.queueInputBuffer(inputBufferIndex, 0, 100, 1000, 0);
//                    }
//
//                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//                    int outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, timeoutUs);
//                    if (outputBufferIndex >= 0) { // 0 이상일 경우에 인코딩/디코딩 데이터가 출력됩니다.
//                        ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
//// muxer에 Sample data를 입력합니다.(파일로 저장)
//                        mediaMuxer.writeSampleData(outputBufferIndex, outputBuffer, bufferInfo);
//// outputBuffer is ready to be processed or rendered.
//// release OutputBuffer의 2번째 boolean 값은 Surface를
//// 통한 디코딩시에 true, 그렇지 않으면 false 처리합니다.
////                        codec.releaseOutputBuffer(outputBufferIndex, (boolean) true/false);
//                        codec.releaseOutputBuffer(outputBufferIndex, true);
//
//                    } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//// Output buffer changed가 호출되면 outputBuffers 를 다시 호출합니다.(Temp buffer의 수가 변경될 수 있음)
//                        outputBuffers = codec.getOutputBuffers();
//
//                    } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                        // More often, the MediaFormat will be retrieved from MediaCodec.getOutputFormat()
//// or MediaExtractor.getTrackFormat().
//
//// Muxer를 초기화 필요할 경우 아래와 같은 코드를 추가해주면 됩니다.
//// Audio/Video를 모두 Muxer에 추가할 경우 2개를 아래와 같이 따로 따로 추가해야 하며,
//// Codec 역시 2개를 별도로 생성해주어야 합니다.
//                        MediaFormat audioFormat = codec.getOutputFormat();
//                        MediaFormat videoFormat = codec.getOutputFormat();
//// MediaFormat에 따라서 Track을 추가하게되면, TrackIndex가 생성됩니다.
//                        int audioTrackIndex = mediaMuxer.addTrack(audioFormat);
//                        int videoTrackIndex = mediaMuxer.addTrack(videoFormat);
//// 트랙 추가가 완료되면 start를 호출하여 muxer를 시작합니다.
//                        mediaMuxer.start();
//
//                        codec.stop();
//                        codec.release();
//                        codec = null;
//
//                    }
//                } catch (IOException e) {
//                    throw new RuntimeException(
//                            "Failed to open video file for output: " + saveRemoteVideoToFile, e);
//                }
//            }
//        } catch (Exception e) {
//
//        }
    }

    //방송 연결이 끊겼을 때.
    @Override
    public void disconnect() {
        localProxyRenderer.setTarget(null);
        if (vGLSurfaceViewCall != null) {
            vGLSurfaceViewCall.release();
            vGLSurfaceViewCall = null;
        }
        Toast.makeText(this, "연결이 끊겼습니다...", Toast.LENGTH_SHORT).show();
        //액티비티를 종료한다.
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

                    //부트스트랩 생성 및 설정.
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new ChatClientInitializer(sslCtx, messageInterface2));
                    //attr 값은 어떤 방을 선택하느냐에 따라 달라져야 한다.


                    User user = SharedPreferenceManager.getInstance(getApplicationContext()).getUser();
                    String userName = user.getUsername();


                    // 채널을 열자마자 바로 방 제목과 명령어를 키-값으로 보내줘야 한다.
                    // 보내기 위해 json 객체에 데이터를 넣는다.
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("command", "make a room");
                    jsonObject.put("room name", gotText);
                    jsonObject.put("genre", gotGenre);
                    jsonObject.put("user name", userName);
                    //네티의 채널리드는 스트링에만 반응하므로 스트링 오브젝트로 넘겨준다.
                    //서버에서는 넘겨받은 스트링을 다시 제이슨으로 변환해 사용할 것이다.
                    String stringObject = jsonObject.toString();

                    //shoot~!
                    channel.writeAndFlush(stringObject);
                    //이러면 방 제목이 곁들여진 방을 생성한 것이 된다.
                    Log.d("newsSalad", "방 생성 완료!!" + gotText);

                    //스트리밍 및 채팅방에 접속해 있는 상태에서 메시지를 보낼 때
                    //글이 들어 있는 상태에서 버튼을 누를 때마다 글이 보내진다.
                    chatBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //에딧택스트 안에 있는 글을 따온다.
                            String gotText1 = editText.getText().toString();

                            JSONObject jsonObject = new JSONObject();
                            //메시지를 보내는 것이므로 메시지의 키-값도 추가.
                            try {
                                jsonObject.put("command", "send a message");
                                jsonObject.put("room name", gotText);
                                jsonObject.put("message", gotText1 + "\r\n");
                                jsonObject.put("user name", userName);

                                String stringObject = jsonObject.toString();

                                //shoot~!
                                channel.writeAndFlush(stringObject);

                                Log.d("newsSalad", "채팅 메시지가 넘어갔습니다.");

                                //메시지를 보내고 난 후에는 에딧텍스트에서 글을 제거한다.
                                editText.setText(" ");

                            } catch (Exception e) {
                                Log.d("newsSalad", e.getMessage());
                            }
                        }
                    });
                } catch (Exception e) {

                    Log.d("newsSalad", e.getMessage());

                }
                //ui 작업
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                }
//            });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT < 23 || isGranted) {
            presenter.startCall();
        } else {
            nPermission.requestPermission(BroadCasterActivity.this, Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        nPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(String permission, boolean isGranted) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                this.isGranted = isGranted;
                if (!isGranted) {
//                    nPermission.requestPermission(this, Manifest.permission.CAMERA);
                } else {
                    nPermission.requestPermission(this, Manifest.permission.RECORD_AUDIO);
                    presenter.startCall();
                }
                break;
            default:
                break;
        }
    }

    @NonNull
    @Override
    public BroadCasterPresenter createPresenter() {
        return new BroadCasterPresenter(getApplication());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        JSONObject jsonObject = new JSONObject();
        try {
            //방송자가 나간다는 표시를 보낸다.
            //방송자가 나가면 방이 폭파되며 그 방의 사용자의 연결을 전부 끊어야 한다.
            jsonObject.put("command", "broadcaster exit the room");
            jsonObject.put("room name", gotText);

            String stringObject = jsonObject.toString();

            channel.writeAndFlush(stringObject);
            //보낸 후에 클라이언트 상의 채팅 채널을 닫는다.
            channel.close();

            Log.d("newsSalad", "채팅 내용이 넘어갔습니다.");
            //방송 채널을 끊는다.
            presenter.disconnect();
            //그리고 액티비티를 종료한다.
            finish();
            Toast.makeText(getApplicationContext(), "방송을 종료했습니다.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d("newsSalad", e.getMessage());
        }
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
    public VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer;
        if (useCamera2()) {
            if (!captureToTexture()) {
                return null;
            }
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
        }
        if (videoCapturer == null) {
            return null;
        }
        return videoCapturer;
    }

    @Override
    public EglBase.Context getEglBaseContext() {
        return rootEglBase.getEglBaseContext();
    }

    @Override
    public VideoRenderer.Callbacks getLocalProxyRenderer() {
        return localProxyRenderer;
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this) && presenter.getDefaultConfig().isUseCamera2();
    }

    private boolean captureToTexture() {
        return presenter.getDefaultConfig().isCaptureToTexture();
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();
        // First, try to find front facing camera
        // 수정을 해서 백 카메라를 우선적으로 찾게 만들었다.
        for (String deviceName : deviceNames) {

            if (enumerator.isBackFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        // Front facing camera not found, try something else
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }
}