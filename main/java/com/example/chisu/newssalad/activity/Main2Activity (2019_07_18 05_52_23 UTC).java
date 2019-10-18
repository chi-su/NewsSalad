package com.example.chisu.newssalad.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.chisu.newssalad.R;
import com.example.chisu.newssalad.general.fragments.TestActivity;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import jnr.ffi.annotations.In;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = new Intent(getApplicationContext(), TestActivity.class);
        startActivity(intent);

        Toolbar toolbar = findViewById(R.id.toolbar0);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.contentRecyclerview);
        //get부분 수정
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getApplicationContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP);

        //가로로 쌓임
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new CatAdapter(this);
        recyclerView.setAdapter(adapter);
    }
}
