package com.example.chisu.newssalad.vod;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chisu.newssalad.R;

import java.util.ArrayList;

public class VodChatRecyclerAdapter extends RecyclerView.Adapter<VodChatRecyclerAdapter.itemViewHolder>{

    ArrayList<VodChatRecyclerItem> mItems;

    public VodChatRecyclerAdapter(ArrayList<VodChatRecyclerItem> items){
        mItems = items;
    }

    @NonNull
    @Override
    public VodChatRecyclerAdapter.itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vod_chat_recycler_item,parent,false);

        return new VodChatRecyclerAdapter.itemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VodChatRecyclerAdapter.itemViewHolder holder, int position) {
        holder.chatDescription.setText(mItems.get(position).getVodChatDescription());
        holder.chatAuthor.setText(mItems.get(position).getVodChatAuthor());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class itemViewHolder extends RecyclerView.ViewHolder{
        private TextView chatDescription;
        private TextView chatAuthor;
        public itemViewHolder(View itemView){
            super(itemView);
            chatDescription = itemView.findViewById(R.id.vodChatDescription);
            chatAuthor = itemView.findViewById(R.id.vodChatAuthor);
        }

    }

}
