package com.example.chisu.newssalad.streaming.broadcaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.chisu.newssalad.R;
import com.example.chisu.newssalad.utils.LogManager;
import com.example.chisu.newssalad.utils.SharedPreferenceManager;
import com.example.chisu.newssalad.utils.User;
import com.jaredrummler.materialspinner.MaterialSpinner;


/**
 * 방송을 시작하기 전에 방송 정보를 입력하는 액티비티
 */
public class ReadyBroadcastActivity extends AppCompatActivity {

    EditText bEditText;
    Button bButton;
    String genre;

    @Override
    protected void onResume() {
        super.onResume();
        User user = SharedPreferenceManager.getInstance(getApplicationContext()).getUser();

        //로그 매니저
        LogManager logManager;
        //최초로 생성된 로그4j 매니저를 가져오기.
        logManager = LogManager.getLogManagerInstance(ReadyBroadcastActivity.this);
        //로그 작성. 각각 시간, 유저이름, 액티비티 이름을 나타낸다.
//        String log = "{\"time\": \""+ logManager.getTime() + "\", \"username\" : \""+ user.getUsername() + "\" ,\"activity\" : \""+ getClass().getSimpleName().trim() + "\"}";
        String log = getClass().getSimpleName().trim();

        logManager.appendLog(log);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_broadcast);
        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems("장르를 선택하세요.", "스포츠", "과학", "엔터테인먼트", "비즈니스");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                //장르 정해주기
                genre = item;
            }
        });
        bEditText = findViewById(R.id.bReadyEditText);
        bButton = findViewById(R.id.bReadyBtn);

        bButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomName = bEditText.getText().toString();
                Intent intent = new Intent(getApplicationContext(), BroadCasterActivity_.class);
                intent.putExtra("genre", genre);
                intent.putExtra("roomName", roomName);
                startActivity(intent);
            }
        });
        }
}
