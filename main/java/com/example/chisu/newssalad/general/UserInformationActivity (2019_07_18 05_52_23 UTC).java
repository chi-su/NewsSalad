package com.example.chisu.newssalad.general;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.chisu.newssalad.R;

public class UserInformationActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        toolbar = findViewById(R.id.user_toolbar);
        setSupportActionBar(toolbar);
        //추가된 소스코드, Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setb
        //왜 툴바 적용이안되징
        toolbar.setNavigationIcon(R.drawable.md_ic_arrow_drop_down);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //툴바 배경색 검은색으로.
        ColorDrawable cd = new ColorDrawable(0xFF000000);
        getSupportActionBar().setBackgroundDrawable(cd);

    }
}
