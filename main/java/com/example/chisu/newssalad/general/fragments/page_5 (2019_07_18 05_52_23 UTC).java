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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chisu.newssalad.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.chisu.newssalad.utils.URLs.VOD_LIST_URL;
import static org.webrtc.ContextUtils.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class page_5 extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    Context context;
    Activity activity;

    VodListAdapter adapter;
    private ArrayList<VodListItem> recyclerItemList = new ArrayList<>();
    RecyclerView recyclerView;

    SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    public void onRefresh() {
        loadRoomList();
        //이걸 안하면 새로고침 후에도 화살표가 사라지지 않는다.
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = getView().findViewById(R.id.page_5_swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
//        imageView = getView().findViewById(R.id.imageView10);
//        Picasso.get().load("http://developer.android.com/assets/images/android_logo.png").into(imageView);

        recyclerView = getView().findViewById(R.id.page_5_recyclerView);
        adapter = new VodListAdapter(recyclerItemList, getApplicationContext());
        recyclerView.setAdapter(adapter);

        //일반적인 리니어레이아웃 매니저(아래의 경우)는 자동으로 크기를 조절하는 경우가 있다.
        //그래서 크기가 원하는 대로 안나오는 경우가 있는데, 아래처럼 하면 개선될 수 있다.
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setAutoMeasureEnabled(false);
        recyclerView.setLayoutManager(llm);

        loadRoomList();

        adapter.notifyDataSetChanged();
    }


    public static page_5 newInsance(){
        Bundle args = new Bundle();
        page_5 fragment = new page_5();
        fragment.setArguments(args);
        return fragment;

    }

    public page_5() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        activity = getActivity();
        return inflater.inflate(R.layout.fragment_page_5, container, false);
    }

    public void loadRoomList(){

        recyclerItemList.clear();

        RequestQueue queue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, VOD_LIST_URL,

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

                                recyclerItemList.add(new VodListItem(
                                        jsonObject.getString("newsTitle"),
                                        jsonObject.getString("newsAuthor"),
                                        jsonObject.getString("newsThumbnailPath")
                                ));
                            }
                            adapter.notifyDataSetChanged();

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
                //받아오기만 하면 되기 때문에 아무것도 보내지 않았다.
                Map<String, String> params = new HashMap<>();

                //탭마다 다른 내용을 가져오기 위해 장르를 보내야 한다.
                params.put("genre", "비즈니스");

//                params.put("itemId", itemId);
                return params;
            }
        };
        queue.add(stringRequest);
    }


}
