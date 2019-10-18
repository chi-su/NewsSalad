package com.example.chisu.newssalad.chatting;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chisu.newssalad.R;

import java.util.ArrayList;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.itemViewHolder> {

    ArrayList<ChatRecyclerItem> mItems;

    public ChatRecyclerAdapter(ArrayList<ChatRecyclerItem> items){
        mItems = items;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_recycler_item,parent,false);

        return new itemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(itemViewHolder holder, int position) {
        holder.chatDescription.setText(mItems.get(position).getChatDescription());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class itemViewHolder extends RecyclerView.ViewHolder{
        private TextView chatDescription;
        public itemViewHolder(View itemView){
            super(itemView);
            chatDescription = itemView.findViewById(R.id.chatDescription);

        }

    }
}
