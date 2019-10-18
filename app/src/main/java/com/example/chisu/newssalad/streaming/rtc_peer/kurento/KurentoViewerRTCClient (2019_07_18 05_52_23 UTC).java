package com.example.chisu.newssalad.streaming.rtc_peer.kurento;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.nhancv.webrtcpeer.rtc_comm.ws.BaseSocketCallback;
import com.nhancv.webrtcpeer.rtc_comm.ws.SocketService;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * Created by nhancao on 7/18/17.
 */

public class KurentoViewerRTCClient {
    private static final String TAG = KurentoViewerRTCClient.class.getSimpleName();

    private SocketService socketService;
    private final Handler handler;


    public KurentoViewerRTCClient(SocketService socketService) {
        this.socketService = socketService;
        final HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public void connectToRoom(String host, BaseSocketCallback socketCallback) {
        socketService.connect(host, socketCallback);
    }

    public void disconnectFromRoom() {
        handler.post(new Runnable() {
            @Override
            public void run() {
               // disconnectFromRoomInternal();
                handler.getLooper().quit();
            }
        });
    }


    public void sendOfferSdp(SessionDescription sdp, String gotText1) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", "viewer");
            obj.put("sdpOffer", sdp.description);
            obj.put("roomId", gotText1);

            socketService.sendMessage(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendAnswerSdp(SessionDescription sdp) {
        Log.e(TAG, "sendAnswerSdp: ");
    }

    public void sendLocalIceCandidate(IceCandidate iceCandidate, String gotText1) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", "onIceCandidate");
            JSONObject candidate = new JSONObject();
            candidate.put("candidate", iceCandidate.sdp);
            candidate.put("sdpMid", iceCandidate.sdpMid);
            candidate.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
            obj.put("candidate", candidate);
            obj.put("roomId", gotText1);
            socketService.sendMessage(obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendLocalIceCandidateRemovals(IceCandidate[] candidates) {
        Log.e(TAG, "sendLocalIceCandidateRemovals: ");
    }

}
