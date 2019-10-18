package com.example.chisu.newssalad.vod;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chisu.newssalad.R;
import com.example.chisu.newssalad.token.T11;
import com.example.chisu.newssalad.utils.LogManager;
import com.example.chisu.newssalad.utils.SharedPreferenceManager;
import com.example.chisu.newssalad.utils.URLs;
import com.example.chisu.newssalad.utils.User;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceEventListener;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.chisu.newssalad.general.HomeActivity.CUSTOM_GAS_PRICE;
import static com.example.chisu.newssalad.utils.URLs.VOD_HOST;
import static org.web3j.tx.Contract.GAS_LIMIT;

//VOD 리스트 액티비티에서 선택한 VOD를 재생하는 액티비티이다.
//채팅 싱크도 진행한다. 전체화면으로 동영상을 볼 수도 있다.
//후원하기 버튼을 누르면 입력한 숫자에 해당하는 토큰이 글 작성자의 주소로 전송되게 했다.
public class VodActivity extends AppCompatActivity implements ExoPlayer.EventListener {
    //채팅 싱크를 실행할 스레드 및 핸들러
    Thread thread;
    //채팅 싱크를 사용할 때 스레드가 ui를 변경할 수 있도록 하는 핸들러.
    Handler handler;
    //후원 버튼
    Button supportBtn;
    //읽어주기 버튼
    Button ttsBtn;
    //음성 파일을 재생할 플레이어 객체.
    private MediaPlayer ttsPlayer = new MediaPlayer();

    //토큰 갯수를 적을 애딧텍스트
    EditText supportText;

    //텍스트 뉴스가 들어갈 텍스트뷰. 데이터 불러오는 건 발리로 불러와야 할듯.
    TextView vodNewsTextview;
    String vodNewsText;

    TextView vodTitleTextView;
    TextView vodAuthorTextView;
    TextView vodTimeTextView;

    //채팅 리사이클러뷰 어댑터
    public VodChatRecyclerAdapter adapter;
    //채팅 리스트 아이템
    private ArrayList<VodChatRecyclerItem> recyclerItemList = new ArrayList<>();

    //방 시작 시간을 담는 변수(채팅 싱크를 위해)
    private String startTimeString;
    //시간화
    private Date startTime;
    //들어온 채팅시간
    private Date chatTIme1;
    // ================================================================================================================================================================================================================================
    //동영상 재생 관련 변수 및 메소드
    private Dialog mFullScreenDialog;
    private SimpleExoPlayerView mExoPlayerView;
    private String hlsVideoUri;
    private SimpleExoPlayer player;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ImageView mFullScreenIcon;
    private boolean mExoPlayerFullscreen = false;
    private FrameLayout mFullScreenButton;

    //전체 화면 다이얼로그를 생성해 두기. 전체화면을 눌렀을 때 다이얼로그로 보여주는 것임.
    private void initFullscreenDialog() {

        // 테마를 이렇게 해야 화면을 다이얼로그가 꽉 채운다.
        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            //뒤로가기 버튼을 누르면 다이얼로그를 나간다.
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    //전체 화면을 여는 메소드
    private void openFullscreenDialog() {
        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(VodActivity.this, R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    //닫는 메소드
    private void closeFullscreenDialog() {
        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(mExoPlayerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(VodActivity.this, R.drawable.ic_fullscreen_expand));
    }

    //풀스크린 버튼 세팅해두는 메소드
    private void initFullscreenButton() {
        PlaybackControlView controlView = mExoPlayerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
        });
    }
    // ================================================================================================================================================================================================================================

    //이더리움 지갑 관련 변수
    org.web3j.crypto.Credentials credentials;
    Web3j web3j;
    User user;
    String walletPath;

    //인텐트로 넘어온 roomName을 담을 변수
    String gotText;

    // ================================================================================================================================================================================================================================
    //다이얼로그 관련 클래스, 변수, 메소드들.
    private ProgressDialog pd;

    //프로그래스바를 보여주는 메소드
    public void showProgress(String msg) {
        if (pd == null) {
            pd = new ProgressDialog(this);
            pd.setCancelable(false);
        }
        pd.setMessage(msg);
        // 원하는 메시지를 세팅한다.
        pd.show();
        // 화면에 띄워라
    }

    // 프로그레스 다이얼로그 숨기기
    public void hideProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    //이더리움 지갑에서 지갑으로 토큰을 전송할 때 사용하는 어싱크태스크 클래스.
    //네트워크를 사용할 때는 백그라운드 작업이 필요하기에 어싱크태스크를 사용했다.
    private class tokenSupportTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress("전송 중입니다.");
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            hideProgress();
            Toast.makeText(getApplicationContext(), "성공적으로 토큰이 전송되었습니다", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            String tokenAmount = supportText.getText().toString();
            long tokenLong = Long.valueOf(tokenAmount);
            BigInteger sendingTokenAmount = BigInteger.valueOf(tokenLong);

            web3j = Web3jFactory.build(new HttpService(URLs.INFURA_ADDRESS));
            user = SharedPreferenceManager.getInstance(getApplicationContext()).getUser();

            walletPath = user.getUserWalletFile();
            try {
                credentials = WalletUtils.loadCredentials("wltn7994!", walletPath);
            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                Log.e("newsSalad error : ", errors.toString());
            }

            T11 contract = T11.load(URLs.CONTRACT_ADDRESS, web3j, credentials, CUSTOM_GAS_PRICE, GAS_LIMIT);
            //녹스 지갑 0xd12b742c2af6cf076a3429178256279611e8980c
            try {
                //트랜잭션 영수증은 나중에 거래 기록을 조회하는 데 사용할 수 있다.
                //getBlockHash 메소드를 사용하면 됨.
                TransactionReceipt transactionReceipt = contract.transfer("0xd12b742c2af6cf076a3429178256279611e8980c", sendingTokenAmount).sendAsync().get();
                Log.e("newsSalad", "토큰이 기자의 계좌로 전송되었습니다");

            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                Log.e("newsSalad error : ", errors.toString());
            }
            return null;
        }
    }
    //다이얼로그 관련 클래스, 변수, 메소드들 끝.
    // ================================================================================================================================================================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod);

        recyclerView = findViewById(R.id.vodChatRecyclerView);
        adapter = new VodChatRecyclerAdapter(recyclerItemList);
        recyclerView.setAdapter(adapter);
        supportText = findViewById(R.id.supportEdit);
        supportBtn = findViewById(R.id.supportBtn);
        vodNewsTextview = findViewById(R.id.vodNewsText);
        ttsBtn = findViewById(R.id.TTSBtn);

        vodTitleTextView = findViewById(R.id.vodTitleTextView);
        vodAuthorTextView = findViewById(R.id.vodAuthorTextView);
        vodTimeTextView = findViewById(R.id.vodTimeTextView);

        supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tokenSupportTask tokenSupportTask = new tokenSupportTask();
                tokenSupportTask.execute();
            }
        });

        ttsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSoundNews();

            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setAutoMeasureEnabled(false);
        recyclerView.setLayoutManager(llm);

        Intent intent = getIntent();
        gotText = intent.getStringExtra("roomName");
        hlsVideoUri = VOD_HOST + gotText + ".m3u8";

        //기사 제목 및 기자 설정
        String vodTitle = intent.getStringExtra("roomName");
        String vodAuthor = intent.getStringExtra("author");

        vodTitleTextView.setText(vodTitle);
        vodAuthorTextView.setText(vodAuthor);
        //시간 설정은 첫 시간을가져오는 메소드에서 실행.

        progressBar = findViewById(R.id.progressBar);

        // 1. Create a default TrackSelector
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        mExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoplayer);
        initFullscreenDialog();
        initFullscreenButton();

        mExoPlayerView.setPlayer(player);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Exo2"), defaultBandwidthMeter);
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource representing the media to be played.
        HlsMediaSource hlsMediaSource = new HlsMediaSource(Uri.parse(hlsVideoUri), dataSourceFactory, mainHandler, new DefaultMediaSourceEventListener() {

            @Override
            public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                super.onLoadStarted(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData);
            }

            @Override
            public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                super.onLoadCompleted(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData);
            }

            @Override
            public void onLoadCanceled(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                super.onLoadCanceled(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData);
            }

            @Override
            public void onLoadError(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
                super.onLoadError(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData, error, wasCanceled);
            }

            @Override
            public void onReadingStarted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
                super.onReadingStarted(windowIndex, mediaPeriodId);
            }

            @Override
            public void onUpstreamDiscarded(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
                super.onUpstreamDiscarded(windowIndex, mediaPeriodId, mediaLoadData);
            }

            @Override
            public void onDownstreamFormatChanged(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
                super.onDownstreamFormatChanged(windowIndex, mediaPeriodId, mediaLoadData);
            }
        });

        player.addListener(this);
        player.prepare(hlsMediaSource);
        mExoPlayerView.requestFocus();
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getStartTime();

    }


    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    //플레이어 재생 상태에 따라 호출되는 콜백함수
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch (playbackState) {
            //버퍼링 중에는 프로그래스바를 띄움.
            case Player.STATE_BUFFERING:
                //You can use progress dialog to show user that video is preparing or buffering so please wait
                progressBar.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_IDLE:
                //idle state
                break;
            //버퍼링이 다 끝나고 영상을 재생할 준비가 완료되면 프로그래스바를 지움.
            case Player.STATE_READY:
                // dismiss your dialog here because our video is ready to play now
                progressBar.setVisibility(View.GONE);
                break;
            //영상 재생이 끝나면 프로그래스바를 지움.
            case Player.STATE_ENDED:
                // do your processing after ending of video
                progressBar.setVisibility(View.GONE);

                break;
        }
    }

    //비디오 재생 중 에러 발생 시
    @Override
    public void onPlayerError(ExoPlaybackException error) {

        AlertDialog.Builder adb = new AlertDialog.Builder(VodActivity.this);
        adb.setTitle("Could not able to stream video");
        adb.setMessage("It seems that something is going wrong.\nPlease try again.");
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                finish();
                // take out user from this activity. you can skip this
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false); //to pause a video because now our video player is not in focus
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler();
        playChat();
        getNewsText();

        //로그 매니저
        LogManager logManager;
        //최초로 생성된 로그4j 매니저를 가져오기.
        logManager = LogManager.getLogManagerInstance(VodActivity.this);
        //로그 작성. 각각 시간, 유저이름, 액티비티 이름을 나타낸다.
        String log = getClass().getSimpleName().trim();
        logManager.appendLog(log);

    }

    //채팅방 메시지를 서버에서 받은 뒤 똑같은 간격으로 재생해 주는 메소드
    public void playChat() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CHAT_SYNC_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //converting the string to json array object
                                    JSONArray array = new JSONArray(response);
                                    //traversing through all the object
                                    for (int i = 0; i < array.length(); i++) {

                                        //getting product object from json array
                                        JSONObject jsonObject = array.getJSONObject(i);
                                        String gotTime;
                                        //넘어오는 족족 하면 된다.

                                        VodChatRecyclerItem vodChatRecyclerItem = new VodChatRecyclerItem(
                                                jsonObject.getString("vodChatAuthor"),
                                                jsonObject.getString("vodChatDescription"),
                                                gotTime = jsonObject.getString("vodChatTime"));

                                        try {
                                            // String 타입을 java.util.Date 로 변환한다.
                                            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            java.util.Date chatTime = formatter.parse(gotTime);

                                            chatTIme1 = new Date(chatTime.getTime());
                                            Log.d("newsSalad", "chatTIme1 is : " + chatTIme1.toString());

                                            //채팅 시작 시간과 첫 번째 채팅 사이의 시간 차이 구하기
                                            long diff = chatTIme1.getTime() - startTime.getTime();

                                            //그 시간만큼 정지.
                                            thread.sleep(diff);
                                            //시간이 지나면 add
                                            recyclerItemList.add(vodChatRecyclerItem);

                                            //채팅을 칠 때마다 스크롤을 내려준다.
                                            int position = recyclerView.getAdapter().getItemCount() - 1;
                                            recyclerView.smoothScrollToPosition(position);

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //ui 변경
                                                    adapter.notifyDataSetChanged();
                                                    //채팅 시간 이동. 이제 다음 채팅 시간과 비교해야 함.
                                                    startTime = chatTIme1;
                                                    Log.d("newsSalad", "chat time changed..");
                                                }
                                            });
                                        } catch (Exception ex) {
                                            Log.d("newsSalad", "error is... : " + ex.getMessage());
                                        }
                                    }
                                    } catch (JSONException e) {
                                    Log.e("newsSalad", e.getMessage());
                                }
                            }
                        });
                        thread.start();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("newsSalad", error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //채팅방의 채팅들을 가져오기 위해 방 이름을 보낸다.
                params.put("roomName", gotText);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    //채팅방이 만들어진 시간을 구하는 메소드.
    public void getStartTime() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.VOD_START_TIME_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject jsonObject = array.getJSONObject(i);
                                startTimeString = jsonObject.getString("vodStartTime");
                                vodTimeTextView.setText("작성 시각 : " + startTimeString);
                                try {

                                    // String 타입을 java.util.Date 로 변환한다.
                                    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    java.util.Date chatStartTime = formatter.parse(startTimeString);

                                    // java.util.Date 를 java.sql.Timestamp 로 변환한다.
                                    startTime = new Date(chatStartTime.getTime());

                                } catch (Exception ex) {
                                    Log.d("newsSalad", "error is.. : " + ex.getMessage());
                                }
                                Log.d("newsSalad", "start time is : " + startTime);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("newsSalad", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("newsSalad", error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("roomName", gotText);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    //DB에 저장된 뉴스의 텍스트를 발리로 불러오는 메소드
    public void getNewsText() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.VOD_GET_NEWS_TEXT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("newsSalad  kxlvjzslk: ", response);

                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject jsonObject = array.getJSONObject(i);
                                vodNewsText = jsonObject.getString("newsText");
                                vodNewsTextview.setMovementMethod(new ScrollingMovementMethod());
                                vodNewsTextview.setText(vodNewsText);


                            }
                        } catch (JSONException e) {
                            StringWriter errors = new StringWriter();
                            e.printStackTrace(new PrintWriter(errors));
                            Log.e("newsSalad error : ", errors.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        StringWriter errors = new StringWriter();
                        error.printStackTrace(new PrintWriter(errors));
                        Log.e("newsSalad error : ", errors.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //룸네임을 보낸다. 룸네임을 중복돼선 안된다.
                params.put("roomName", gotText);
                Log.e("newsSalad  roomName: ", gotText);

                return params;
            }
        };
        queue.add(stringRequest);
    }

    //텍스트 뉴스를 소리로 읽어주는 메소드.
    //네트워크를 다루는 부분이 있어 스레드로 처리했다.
    public void getSoundNews(){
        ttsThread.start();
    }

    //네트워크로 음성 파일을 받는 스레드
    Thread ttsThread = new Thread(new Runnable() {
        @Override
        public void run() {
            //백엔드 처리
            String clientId = "jj2kj2t88b";//애플리케이션 클라이언트 아이디값";
            String clientSecret = "TPUkiL6WTC8g51hnCpn57kGY52PKbi8dieFOb0ra";//애플리케이션 클라이언트 시크릿값";
            try {
                //텍스트뉴스에서 텍스트를 가져옴
                String sttText = vodNewsTextview.getText().toString();
                //인코딩
                String text = URLEncoder.encode(sttText, "UTF-8"); // 13자
                String apiURL = "https://naveropenapi.apigw.ntruss.com/voice/v1/tts";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
                con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
                // post request
                String postParams = "speaker=jinho&speed=0&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) { // 정상 호출

                    //연결에서 들어오는 스트림을 생성한 인풋스트림에 넣어준다.
                    InputStream is = con.getInputStream();
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    //파일이 저장될 경로 지정.
                    String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LOG/";

                    //경로를 생성
                    File file = new File(dirPath);
                    if (!file.exists()) file.mkdirs();

                    //이름을 바탕으로 빈 파일을 생성한다.
                    File savefile = new File(dirPath + "ttsfile" + ".mp3");

                    //생성한 파일로 아웃풋스트림을 생성한다. 아웃풋스트림은 쓰기를 할 때 사용된다.
                    OutputStream outputStream = new FileOutputStream(savefile);

                    //is에서 받아온 파일의 데이터를 read에 넣어보고,
                    //그 값이 -1이 아니라면 아웃풋 스트림에 read의 데이터를 넣어준다.
                    //-1이 뜰 때까지, 즉 데이터가 더 없을 때까지 반복한다.
                    while ((read = is.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                    is.close();

                    //여기까지가 음성 파일 만드는 부분
                    //이 아래는 만든 mp3 파일을 가져와서 읽는 부분.

                    //플레이어를 리셋해둔다.
                    ttsPlayer.reset();
                    //경로의 파일을 가져온다.
                    ttsPlayer.setDataSource(savefile.getPath());
                    //준비하고
                    ttsPlayer.prepare();
                    //재생한다.
                    ttsPlayer.start();

                } else {  // 에러 발생 시
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    br.close();
                    Log.e("newsSalad error : ", response.toString());
                }
                Log.e("newsSalad", "음성화가 완료되었습니다.");

            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                Log.e("newsSalad error : ", errors.toString());
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    //ui 처리
                    Toast.makeText(getApplicationContext(), "음성 파일을 받았습니다.", Toast.LENGTH_LONG).show();
                }
            });
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

}
