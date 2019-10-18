package com.example.chisu.newssalad.general.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chisu.newssalad.R;
import com.example.chisu.newssalad.general.LiveListAdapter;
import com.example.chisu.newssalad.general.LiveListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.chisu.newssalad.utils.URLs.MAIN_URL;
import static org.webrtc.ContextUtils.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
//라이브 페이지
public class page_1 extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    Context context;
    Activity activity;

    //http로 db에 저장된 방 리스트들을 가져오는 URL
    //홈 리사이클러뷰 어댑터
    public LiveListAdapter adapter;

    //홈 리사이클러뷰 아이템 리스트
    private ArrayList<LiveListItem> recyclerItemList = new ArrayList<>();
    RecyclerView recyclerView;

    //당겨서 새로고침을 위한 레이아웃
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    public void onRefresh() {
        loadRoomList();
        //이걸 안하면 새로고침 후에도 화살표가 사라지지 않는다.
        mSwipeRefreshLayout.setRefreshing(false);
    }


    public static page_1 newInsance(){
        Bundle args = new Bundle();
        page_1 fragment = new page_1();
        fragment.setArguments(args);
        return fragment;

    }

    public page_1() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //프래그먼트에서는 findviewbyid가 안되므로 앞에 getview를 붙여준다.
        //이 방법은 onVIewCreated에서만 먹힘
        mSwipeRefreshLayout = getView().findViewById(R.id.page_1_swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        recyclerView = getView().findViewById(R.id.page_1_recyclerView);
        adapter = new LiveListAdapter(recyclerItemList, getApplicationContext());
        recyclerView.setAdapter(adapter);

        //일반적인 리니어레이아웃 매니저(아래의 경우)는 자동으로 크기를 조절하는 경우가 있다.
        //그래서 크기가 원하는 대로 안나오는 경우가 있는데, 아래처럼 하면 개선될 수 있다.
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setAutoMeasureEnabled(false);
        recyclerView.setLayoutManager(llm);

        loadRoomList();
        adapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context =getContext();
        activity = getActivity();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page_1, container, false);

    }

    //DB 에서 방 목록을 불러와 리사이클러뷰에 표시해주는 메소드
    public void loadRoomList(){

        recyclerItemList.clear();
        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MAIN_URL,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i=0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject jsonObject = array.getJSONObject(i);

                                recyclerItemList.add(new LiveListItem(
                                        jsonObject.getString("newsTitle")
                                ));
                            }
                            adapter.notifyDataSetChanged();

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
                //받아오기만 하면 되기 때문에 아무것도 보내지 않았다.
                Map<String, String> params = new HashMap<>();
//                params.put("itemId", itemId);
                return params;
            }
        };
        queue.add(stringRequest);
    }

}
