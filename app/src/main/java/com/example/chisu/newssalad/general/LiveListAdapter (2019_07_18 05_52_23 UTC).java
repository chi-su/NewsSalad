package com.example.chisu.newssalad.general;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chisu.newssalad.R;
import com.example.chisu.newssalad.streaming.viewer.ViewerActivity_;
import java.util.ArrayList;

//라이브 방송 목록을 나타내는  리사이클러뷰.
public class LiveListAdapter extends RecyclerView.Adapter<LiveListAdapter.itemViewHolder> {

    ArrayList<LiveListItem> mItems;
    private Context context;

    //이런 식으로 해야 해.
    public LiveListAdapter(ArrayList<LiveListItem> items, Context context){
        mItems = items;
        this.context = context;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycler_item,parent,false);

        return new itemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(itemViewHolder holder, int position) {
        holder.homeDescription.setText(mItems.get(position).getHomeDescription());

        final LiveListItem item = mItems.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String roomName = item.getHomeDescription();

                //인텐트로 보내고 액티비티를 시작시킨다.
                Intent intent = new Intent(context, ViewerActivity_.class);
                intent.putExtra("roomName", roomName);

                //액티비티가 아닌 곳에서 액티비티를 시작시키기 위한 플래그 세우기.
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                return ;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class itemViewHolder extends RecyclerView.ViewHolder{
        private TextView homeDescription;
        public itemViewHolder(View itemView){
            super(itemView);
            homeDescription = itemView.findViewById(R.id.homeDescription);

        }
    }
}
