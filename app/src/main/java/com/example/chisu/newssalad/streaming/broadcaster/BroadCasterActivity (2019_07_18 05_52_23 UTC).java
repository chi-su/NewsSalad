package com.example.chisu.newssalad.streaming.broadcaster;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chisu.newssalad.R;
import com.example.chisu.newssalad.chatting.ChatClientInitializer;
import com.example.chisu.newssalad.chatting.ChatRecyclerAdapter;
import com.example.chisu.newssalad.chatting.ChatRecyclerItem;
import com.example.chisu.newssalad.soundRecognize.MessageDialogFragment;
import com.example.chisu.newssalad.soundRecognize.SpeechService;
import com.example.chisu.newssalad.soundRecognize.VoiceRecorder;
import com.example.chisu.newssalad.utils.LogManager;
import com.example.chisu.newssalad.utils.SharedPreferenceManager;
import com.example.chisu.newssalad.utils.User;
import com.example.chisu.newssalad.vod.VodActivity;
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import static com.example.chisu.newssalad.utils.URLs.CHAT_HOST;
import static com.example.chisu.newssalad.utils.URLs.CHAT_PORT;

/**
 * 방송자가 실제로 채팅과 방송을 실시하며 서버로 전송하는 액티비티.
 * 방송자의 음성을 인식해서 화면에 띄워주고 채팅창에도 넣어준다.
 * 인식한 음성은 따로 저장해서 나중에 텍스트 뉴스로 띄워준다.
 * 음성인식은 스트리밍에서는 하지 않는다.
 */
@EActivity(R.layout.activity_broadcaster)
public class BroadCasterActivity extends MvpActivity<BroadCasterView, BroadCasterPresenter>
        implements BroadCasterView, NPermission.OnPermissionResult, ChatClientInitializer.MessageInterface2 {

    /**음성인식 관련 변수 및 메소드들**/

//  방송자가 말한 것들을 모아두는 스트링 객체.
    String newsText = "//텍스트 뉴스입니다//";
    //음성인식한 텍스트가 들어갈 텍스트뷰.
    //수정했다. 인식한 텍스트는 그냥 채팅창의 에딧텍스트로 들어가게 했다.
    //그리고 채팅되게. 이건 없애야되지만 그냥 귀찮아서 남겨둔다.
    //xml에서는 gone 처리.
    private TextView recognizedTextview;

    private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";

    //권한 구분용. 이걸 보내면 돌아올 때도 똑같은 값으로 돌아오기 때문에 어떤 권한인지 알 수 있다.
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;

    private SpeechService mSpeechService;

    private VoiceRecorder mVoiceRecorder;

    //스피치서비스의 리스너 인터페이스를 여기서 재정의해서 사용.
    private SpeechService.Listener  mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    //인식되는 소리가 없으면 더이상 듣지 않는다.
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                    }
                    //소리가 있으면 계속 듣는다.
                    if (recognizedTextview != null && !TextUtils.isEmpty(text)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    //말하는 게 다 끝나면 텍스트를 지운다.
                                    //그리고 채팅으로 전송한다.
                                    //+텍스트 뉴스에 문장 추가
                                    newsText = newsText + "\n" + text;
                                    Log.e("newsSalad!!!", newsText);
                                    //클릭이벤트 발생시키기
                                    chatBtn.performClick();
                                    //채팅입력창 비우기.
                                    editText.setText(" ");
                                } else {
                                    //말이 끝나지 않을경우 지속적으로 채팅창에 텍스트 업데이트.
                                    editText.setText(text);
                                }
                            }
                        });
                    }
                }
            };

    //보이스레코더에 소리가 들려야 스피치서비스가 작동하기 시작한다.
    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        //보이스가 들리기 시작할 때 인식을 시작함
        @Override
        public void onVoiceStart() {
            Log.e("newsSalad", "onVoiceStart");

            if (mSpeechService != null) {
                mSpeechService.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }
        //보이스가 들리는 중에 계속 인식함
        @Override
        public void onVoice(byte[] data, int size) {
            Log.e("newsSalad", "onVoice");

            if (mSpeechService != null) {
                mSpeechService.recognize(data, size);
            }
        }
        //보이스가 끝나면 소리 인식도 끝남.
        @Override
        public void onVoiceEnd() {
            Log.e("newsSalad", "onVoiceEnd");
            if (mSpeechService != null) {
                mSpeechService.finishRecognizing();
            }
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            Log.e("newsSalad", "onServiceConnected");
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("newsSalad", "onServiceDisconnected");

            mSpeechService = null;
        }
    };

    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        //위에서 설정한 콜백을 기반으로 보이스레코더 시작
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }

    private void stopVoiceRecorder() {
        Log.e("newsSalad", "stopVoiceRecorder");

        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }

    private void showPermissionMessageDialog() {
        Log.e("newsSalad", "showPermissionMessageDialog");

        MessageDialogFragment
                .newInstance(getString(R.string.permission_message))
                .show(getSupportFragmentManager(), FRAGMENT_MESSAGE_DIALOG);
    }

    /**
     * 음성인식 관련 변수 및 메소드들 끝
     */

    //로그용 태그
    private static final String TAG = BroadCasterActivity.class.getSimpleName();

    //채팅 리사이클러뷰 어댑터
    public ChatRecyclerAdapter adapter;
    //채팅 리스트 아이템
    private ArrayList<ChatRecyclerItem> recyclerItemList = new ArrayList<>();

    private long lastTimeBackPressed; //뒤로가기 버튼이 클릭된 시간

    @Override
    public void onBackPressed() {
        //2초 이내에 뒤로가기 버튼을 재 클릭 시 앱 종료
        if (System.currentTimeMillis() - lastTimeBackPressed < 2000) {
            finish();
            return;
        }
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 방송이 종료됩니다.", Toast.LENGTH_SHORT).show();
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

    //채팅 내용이 들어갈 edittext
    EditText editText;
    Button chatBtn;
    //채팅 리사이클러뷰
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
                Log.e("newsSalad", "sendData OK..");
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

    @AfterViews
    protected void init() {
        //상태 바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recognizedTextview = findViewById(R.id.recognizedTextview);

        //로그 매니저
        LogManager logManager;
        //최초로 생성된 로그 매니저를 가져오기.
        logManager = LogManager.getLogManagerInstance(BroadCasterActivity.this);
        String log = getClass().getSimpleName().trim();
        logManager.appendLog(log);

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

    //netty 채팅 스레드 시작 메소드
    public void startChat() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //클라이언트이므로 그룹은 하나면 됨.
                EventLoopGroup group = new NioEventLoopGroup();
                Log.e("newsSalad", "이벤트 그룹 생성 완료");

                try {
                    final SslContext sslCtx = SslContextBuilder.forClient()
                            .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

                    //부트스트랩 생성 및 설정.
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new ChatClientInitializer(sslCtx, messageInterface2));
                    //attr 값은 어떤 방을 선택하느냐에 따라 달라져야 한다.
                    channel = bootstrap.connect(CHAT_HOST, CHAT_PORT).sync().channel();

                    User user = SharedPreferenceManager.getInstance(getApplicationContext()).getUser();
                    String userName = user.getUsername();

                    // 채널을 열자마자 바로 방 제목과 명령어를 키-값으로 보내줘야 한다.
                    // 보내기 위해 json 객체에 데이터를 넣는다.
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("command", "make a room");
                    jsonObject.put("room name", gotText);
                    jsonObject.put("genre", gotGenre);
                    jsonObject.put("user name", userName);

                    Log.e("newsSalad", userName);
                    //네티의 채널리드는 스트링에만 반응하므로 스트링 오브젝트로 넘겨준다.
                    //서버에서는 넘겨받은 스트링을 다시 제이슨으로 변환해 사용할 것이다.
                    String stringObject = jsonObject.toString();
                    Log.e("newsSalad", stringObject);

                    //shoot~!
                    channel.writeAndFlush(stringObject);
                    //이러면 방 제목이 곁들여진 방을 생성한 것이 된다.
                    Log.e("newsSalad", "방 생성 완료!!" + gotText);

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

                                Log.e("newsSalad", "채팅 메시지가 넘어갔습니다.");

                                //메시지를 보내고 난 후에는 에딧텍스트에서 글을 제거한다.
                                editText.setText(" ");

                            } catch (Exception e) {
                                StringWriter errors = new StringWriter();
                                e.printStackTrace(new PrintWriter(errors));
                                Log.e("newsSalad error : ", errors.toString());
                            }
                        }
                    });
                } catch (Exception e) {

                    StringWriter errors = new StringWriter();
                    e.printStackTrace(new PrintWriter(errors));
                    Log.e("newsSalad error : ", errors.toString());

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
    protected void onStart() {
        super.onStart();

        //음성인식 관련 코드
        bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);
        Log.e("newsSalad", "bindService");

        // Start listening to voices
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            Log.e("newsSalad", "startVoiceRecorder");
            startVoiceRecorder();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            showPermissionMessageDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //SDK 버전에 따른 퍼미션 요청
        if (Build.VERSION.SDK_INT < 23 || isGranted) {
            presenter.startCall();
        } else {
            nPermission.requestPermission(BroadCasterActivity.this, Manifest.permission.CAMERA);
            nPermission.requestPermission(this, Manifest.permission.RECORD_AUDIO);
        }

//        mSpeechService.recognizeInputStream(getResources().openRawResource(R.raw.audio));

    }


    @Override
    protected void onStop() {
        // Stop listening to voice
        stopVoiceRecorder();

        // Stop Cloud Speech API
        mSpeechService.removeListener(mSpeechServiceListener);
        unbindService(mServiceConnection);
        mSpeechService = null;

        super.onStop();
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
            jsonObject.put("news text", newsText);
            String stringObject = jsonObject.toString();

            channel.writeAndFlush(stringObject);
            //보낸 후에 클라이언트 상의 채팅 채널을 닫는다.
            channel.close();

            Log.e("newsSalad", "방송을 종료합니다..");
            //방송 채널을 끊는다.
            presenter.disconnect();
            //그리고 액티비티를 종료한다.
            finish();
            Toast.makeText(getApplicationContext(), "방송을 종료했습니다.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("newsSalad", e.getMessage());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        nPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (permissions.length == 1 && grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecorder();
            } else {
                showPermissionMessageDialog();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onPermissionResult(String permission, boolean isGranted) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                this.isGranted = isGranted;
                if (!isGranted) {
                    nPermission.requestPermission(this, Manifest.permission.CAMERA);
                } else {
//                    nPermission.requestPermission(this, Manifest.permission.RECORD_AUDIO);
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
        Log.e("newsSalad, ", "createPresenter");
        return new BroadCasterPresenter(getApplication());
    }


    @Override
    public void logAndToast(String msg) {
        Log.e("newsSalad, ", "logAndToast");

        Log.e(TAG, msg);
        if (logToast != null) {
            logToast.cancel();
        }
        logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        logToast.show();
    }

    @Override
    public VideoCapturer createVideoCapturer() {
        Log.e("newsSalad, ", "createVideoCapturer");

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
        Log.e("newsSalad, ", "getEglBaseContext");
        return rootEglBase.getEglBaseContext();
    }

    @Override
    public VideoRenderer.Callbacks getLocalProxyRenderer() {
        Log.e("newsSalad, ", "getLocalProxyRenderer");
        return localProxyRenderer;
    }
    //카메라2를 사용하는지 불린을 리턴하는 함수. 사용가능하면 씀.
    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this) && presenter.getDefaultConfig().isUseCamera2();
    }

    private boolean captureToTexture() {
        Log.e("newsSalad, ", "captureToTexture");
        return presenter.getDefaultConfig().isCaptureToTexture();
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();
        // First, try to find front facing camera
        // 수정을 해서 백 카메라를 우선적으로 찾게 만들었다.
        // 다시 재수정해서 프론트 카메라를 사용함.
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