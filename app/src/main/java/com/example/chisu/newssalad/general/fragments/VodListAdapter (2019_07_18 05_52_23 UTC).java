package com.example.chisu.newssalad.general.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chisu.newssalad.R;
import com.example.chisu.newssalad.vod.VodActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//import kotlin.jvm.internal.Intrinsics;

//VOD 파일들의 리스트를 나타내는 리사이클러뷰 어댑터
//홈 화면의 프래그먼트들 안에서 사용됨.
public class VodListAdapter extends RecyclerView.Adapter<VodListAdapterViewHolder> {
    ArrayList<VodListItem> mItems;

    private Context context;

    public VodListAdapter(ArrayList<VodListItem> items, Context context1){
        mItems = items;
        this.context = context1;
    }

    @NonNull
    @Override
    public VodListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vods_recycler_item, parent, false);
        return new VodListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VodListAdapterViewHolder holder, int position) {
        holder.newsTitle.setText(mItems.get(position).getNewsTitle());

        Picasso.get().load("http://52.79.75.232/vodthumbnails/"+mItems.get(position).getNewsThumbnailPath()).placeholder(R.drawable.cat_1).into(holder.newsThumbnail);
        holder.newsAuthor.setText(mItems.get(position).getNewsAuthor());
        final VodListItem item = mItems.get(position);

        Log.e("newsSalad", "onBindViewHolder");


        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Vod 제목
                String roomName = item.getNewsTitle();
                //vod 저자
                String author = item.getNewsAuthor();

                //인텐트로 보내고 액티비티를 시작시킨다.
                Intent intent = new Intent(context, VodActivity.class);
                intent.putExtra("roomName", roomName);
                intent.putExtra("author", author);

                //액티비티가 아닌 곳에서 액티비티를 시작시키기 위한 플래그 세우기.
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                return;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
