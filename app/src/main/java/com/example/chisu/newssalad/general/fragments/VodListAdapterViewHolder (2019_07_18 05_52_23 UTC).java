package com.example.chisu.newssalad.general.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chisu.newssalad.R;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.io.InputStream;

public class VodListAdapterViewHolder extends RecyclerView.ViewHolder {

    public ImageView newsThumbnail;
    public TextView newsTitle;
    public ConstraintLayout newsBox;
    public TextView newsAuthor;

    public VodListAdapterViewHolder(View itemView) {
        super(itemView);

        newsThumbnail = itemView.findViewById(R.id.newsThumbnail);
        newsTitle = itemView.findViewById(R.id.newsTitle);
        newsBox = itemView.findViewById(R.id.newsBox);
        newsAuthor = itemView.findViewById(R.id.newsAuthor);
    }
}