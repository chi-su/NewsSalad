package com.example.chisu.newssalad.streaming.rtc_peer.kurento;

import android.util.Log;

import com.nhancv.webrtcpeer.rtc_comm.ws.BaseSocketCallback;
import com.nhancv.webrtcpeer.rtc_comm.ws.SocketService;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

public class KurentoPresenterRTCClient {
    private static final String TAG = KurentoPresenterRTCClient.class.getSimpleName();

    private SocketService socketService;

    public KurentoPresenterRTCClient(SocketService socketService) {
        this.socketService = socketService;
    }

    public void connectToRoom(String host, BaseSocketCallback socketCallback) {
        socketService.connect(host, socketCallback);
    }

    //여기에다 넣어주기 .
    public void sendOfferSdp(SessionDescription sdp, String roomId) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", "presenter");
            obj.put("sdpOffer", sdp.description);
            obj.put("roomId", roomId);

            socketService.sendMessage(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void sendAnswerSdp(SessionDescription sdp) {
        Log.e(TAG, "sendAnswerSdp: ");
    }


    public void sendLocalIceCandidate(IceCandidate iceCandidate, String roomId) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", "onIceCandidate");
            JSONObject candidate = new JSONObject();
            candidate.put("candidate", iceCandidate.sdp);
            candidate.put("sdpMid", iceCandidate.sdpMid);
            candidate.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
            obj.put("candidate", candidate);
            obj.put("roomId", roomId);

            socketService.sendMessage(obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void sendLocalIceCandidateRemovals(IceCandidate[] candidates) {
        Log.e(TAG, "sendLocalIceCandidateRemovals: ");
    }

}
