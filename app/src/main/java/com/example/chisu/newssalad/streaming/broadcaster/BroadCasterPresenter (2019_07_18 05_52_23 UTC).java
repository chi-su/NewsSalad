package com.example.chisu.newssalad.streaming.broadcaster;

import android.app.Application;
import android.util.Log;

import com.example.chisu.newssalad.utils.URLs;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import com.example.chisu.newssalad.streaming.rtc_peer.kurento.KurentoPresenterRTCClient;
import com.example.chisu.newssalad.streaming.rtc_peer.kurento.models.CandidateModel;
import com.example.chisu.newssalad.streaming.rtc_peer.kurento.models.response.ServerResponse;
import com.example.chisu.newssalad.streaming.rtc_peer.kurento.models.response.TypeResponse;
import com.example.chisu.newssalad.streaming.util.RxScheduler;


import com.nhancv.webrtcpeer.rtc_comm.ws.BaseSocketCallback;
import com.nhancv.webrtcpeer.rtc_comm.ws.DefaultSocketService;
import com.nhancv.webrtcpeer.rtc_comm.ws.SocketService;
import com.nhancv.webrtcpeer.rtc_peer.PeerConnectionClient;
import com.nhancv.webrtcpeer.rtc_peer.PeerConnectionParameters;
import com.nhancv.webrtcpeer.rtc_peer.SignalingEvents;
import com.nhancv.webrtcpeer.rtc_peer.SignalingParameters;
import com.nhancv.webrtcpeer.rtc_peer.StreamMode;
import com.nhancv.webrtcpeer.rtc_peer.config.DefaultConfig;
import com.nhancv.webrtcpeer.rtc_plugins.RTCAudioManager;

import org.java_websocket.handshake.ServerHandshake;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.VideoCapturer;

import java.util.ArrayList;
import java.util.LinkedList;

import static com.example.chisu.newssalad.utils.URLs.STREAM_HOST;

/**
 * Created by nhancao on 7/20/17.
 */

public class BroadCasterPresenter extends MvpBasePresenter<BroadCasterView>
        implements SignalingEvents, PeerConnectionClient.PeerConnectionEvents {

    private static final String TAG = BroadCasterPresenter.class.getSimpleName();

    private Application application;
    private SocketService socketService;
    private Gson gson;

    private PeerConnectionClient peerConnectionClient;
    private KurentoPresenterRTCClient rtcClient;
    private PeerConnectionParameters peerConnectionParameters;
    private DefaultConfig defaultConfig;
    private RTCAudioManager audioManager;
    private SignalingParameters signalingParameters;
    private boolean iceConnected;

    //서버로 전송될 방 제목
    String gotText;

    //생성자
    public BroadCasterPresenter(Application application) {
        this.application = application;
        this.socketService = new DefaultSocketService(application);
        this.gson = new Gson();
    }


    //이밑에 스트링 인자
    //broadcasteractivity에서 넘어온 인자를 받아서 전역 변수에 넣는다.
    public void initPeerConfig(String gotText1) {
        rtcClient = new KurentoPresenterRTCClient(socketService);
        defaultConfig = new DefaultConfig();
        peerConnectionParameters = defaultConfig.createPeerConnectionParams(StreamMode.SEND_ONLY);
        peerConnectionClient = PeerConnectionClient.getInstance();
        peerConnectionClient.createPeerConnectionFactory(
                application.getApplicationContext(), peerConnectionParameters, this);
        gotText = gotText1;
    }

    //방송을 끊는 메소드. 피어커넥션을 끊고 소켓을 닫고 오디오매니저도 비운다. rtc클라이언트도 비운다.
    public void disconnect() {
        if (rtcClient != null) {
            rtcClient = null;
        }
        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }

        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }

        if (socketService != null) {
            socketService.close();
        }

        if (isViewAttached()) {
            getView().disconnect();
        }
    }

    //서버와 연결하기 위해 콜 시작.
    public void startCall() {
        if (rtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.");
            return;
        }

        //서버와 연결하는 메소드
        rtcClient.connectToRoom(URLs.STREAM_HOST, new BaseSocketCallback() {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                super.onOpen(serverHandshake);
                RxScheduler.runOnUi(o -> {
                    if (isViewAttached()) {
                        getView().logAndToast("Socket connected");
                    }
                });
                SignalingParameters parameters = new SignalingParameters(
                        new LinkedList<PeerConnection.IceServer>() {
                            {
                                add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
                            }
                        }, true, null, null, null, null, null);
                onSignalConnected(parameters);
            }

            //스트리밍 통신 시 서버에서 넘어온 메시지를 처리하는 콜백 메소드
            @Override
            public void onMessage(String serverResponse_) {
                super.onMessage(serverResponse_);
                try {
                    ServerResponse serverResponse = gson.fromJson(serverResponse_, ServerResponse.class);

                    switch (serverResponse.getIdRes()) {
                        case PRESENTER_RESPONSE:
                            if (serverResponse.getTypeRes() == TypeResponse.REJECTED) {
                                RxScheduler.runOnUi(o -> {
                                    if (isViewAttached()) {
                                        getView().logAndToast(serverResponse.getMessage());
                                    }
                                });
                            } else {
                                SessionDescription sdp = new SessionDescription(SessionDescription.Type.ANSWER,
                                                                                serverResponse.getSdpAnswer());
                                onRemoteDescription(sdp);
                            }

                            break;

                        case ICE_CANDIDATE:
                            CandidateModel candidateModel = serverResponse.getCandidate();
                            onRemoteIceCandidate(
                                    new IceCandidate(candidateModel.getSdpMid(), candidateModel.getSdpMLineIndex(),
                                                     candidateModel.getSdp()));
                            break;

                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                super.onClose(i, s, b);
                RxScheduler.runOnUi(o -> {
                    if (isViewAttached()) {
                        getView().logAndToast("Socket closed");
                    }
                    disconnect();
                });
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
                RxScheduler.runOnUi(o -> {
                    if (isViewAttached()) {
                        getView().logAndToast(e.getMessage());
                    }
                    disconnect();
                });
            }
        });

        // Create and audio manager that will take care of audio routing,
        // audio modes, audio device enumeration etc.
        audioManager = RTCAudioManager.create(application.getApplicationContext());
        // Store existing audio settings and change audio mode to
        // MODE_IN_COMMUNICATION for best possible VoIP performance.
        Log.d(TAG, "Starting the audio manager...");
        audioManager.start((audioDevice, availableAudioDevices) ->
                                   Log.d(TAG, "onAudioManagerDevicesChanged: " + availableAudioDevices + ", "
                                              + "selected: " + audioDevice));
    }

    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    private void callConnected() {
        if (peerConnectionClient == null) {
            Log.w(TAG, "Call is connected in closed or error state");
            return;
        }
        // Enable statistics callback.
        peerConnectionClient.enableStatsEvents(true, 1000);
    }

    //연결이 성공할 경우에 스트림을 보내는 메소드.

    @Override
    public void onSignalConnected(SignalingParameters params) {
        RxScheduler.runOnUi(o -> {
            if (isViewAttached()) {
                signalingParameters = params;
                VideoCapturer videoCapturer = null;
                if (peerConnectionParameters.videoCallEnabled) {
                    videoCapturer = getView().createVideoCapturer();
                }
                //연결하고 스트림을 보내기 시작한다.
                //RTCPeerConnection 이 생성되면, Navigator.mediaDevices.getUserMedia함수를 통해 유저의 카메라와 마이크에 권한을 요청한다.
                //한번 연결이 되면, 그 연결을 통해 지속적으로 스트림을 보낸다.
                peerConnectionClient
                        .createPeerConnection(getView().getEglBaseContext(), getView().getLocalProxyRenderer(),
                                              new ArrayList<>(), videoCapturer,
                                              signalingParameters);

                if (signalingParameters.initiator) {
                    if (isViewAttached()) getView().logAndToast("Creating OFFER...");
                    // Create offer. Offer SDP will be sent to answering client in
                    // PeerConnectionEvents.onLocalDescription event.
                    peerConnectionClient.createOffer();
                } else {
                    if (params.offerSdp != null) {
                        peerConnectionClient.setRemoteDescription(params.offerSdp);
                        if (isViewAttached()) getView().logAndToast("Creating ANSWER...");
                        // Create answer. Answer SDP will be sent to offering client in
                        // PeerConnectionEvents.onLocalDescription event.
                        peerConnectionClient.createAnswer();
                    }
                    if (params.iceCandidates != null) {
                        // Add remote ICE candidates from room.
                        for (IceCandidate iceCandidate : params.iceCandidates) {
                            peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onRemoteDescription(SessionDescription sdp) {
        RxScheduler.runOnUi(o -> {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
                return;
            }
            peerConnectionClient.setRemoteDescription(sdp);
            if (!signalingParameters.initiator) {
                if (isViewAttached()) getView().logAndToast("Creating ANSWER...");
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
        });
    }

    @Override
    public void onRemoteIceCandidate(IceCandidate candidate) {
        RxScheduler.runOnUi(o -> {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.");
                return;
            }
            peerConnectionClient.addRemoteIceCandidate(candidate);
        });
    }

    @Override
    public void onRemoteIceCandidatesRemoved(IceCandidate[] candidates) {
        RxScheduler.runOnUi(o -> {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.");
                return;
            }
            peerConnectionClient.removeRemoteIceCandidates(candidates);
        });
    }

    @Override
    public void onChannelClose() {
        RxScheduler.runOnUi(o -> {
            if (isViewAttached()) getView().logAndToast("Remote end hung up; dropping PeerConnection");
            disconnect();
        });
    }

    @Override
    public void onChannelError(String description) {
        Log.e(TAG, "onChannelError: " + description);
    }

    @Override
    public void onLocalDescription(SessionDescription sdp) {
        RxScheduler.runOnUi(o -> {
            if (rtcClient != null) {
                if (signalingParameters.initiator) {
                    //이 밑에 인자 하나 더 넣기
                    //서버와 연결하고자할 때 sendOfferSdp 메소드를 오버라이딩하여 presenter 로 연결한다.
                    rtcClient.sendOfferSdp(sdp, gotText);
                } else {
                    rtcClient.sendAnswerSdp(sdp);
                }
            }
            if (peerConnectionParameters.videoMaxBitrate > 0) {
                Log.d(TAG, "Set video maximum bitrate: " + peerConnectionParameters.videoMaxBitrate);
                peerConnectionClient.setVideoMaxBitrate(peerConnectionParameters.videoMaxBitrate);
            }
        });
    }

    @Override
    public void onIceCandidate(IceCandidate candidate) {
        RxScheduler.runOnUi(o -> {
            if (rtcClient != null) {
                rtcClient.sendLocalIceCandidate(candidate, gotText);
            }
        });
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] candidates) {
        RxScheduler.runOnUi(o -> {
            if (rtcClient != null) {
                rtcClient.sendLocalIceCandidateRemovals(candidates);
            }
        });
    }

    @Override
    public void onIceConnected() {
        RxScheduler.runOnUi(o -> {
            iceConnected = true;
            callConnected();
        });
    }

    @Override
    public void onIceDisconnected() {
        RxScheduler.runOnUi(o -> {
            if (isViewAttached()) getView().logAndToast("ICE disconnected");
            iceConnected = false;
            disconnect();
        });
    }

    @Override
    public void onPeerConnectionClosed() {

    }

    @Override
    public void onPeerConnectionStatsReady(StatsReport[] reports) {
        RxScheduler.runOnUi(o -> {
            if (iceConnected) {
                Log.e(TAG, "run: " + reports);
            }
        });
    }

    @Override
    public void onPeerConnectionError(String description) {
        Log.e(TAG, "onPeerConnectionError: " + description);
    }
}
