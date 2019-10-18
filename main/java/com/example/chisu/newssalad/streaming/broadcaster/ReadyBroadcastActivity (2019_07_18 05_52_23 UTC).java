package com.example.chisu.newssalad.streaming.broadcaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.chisu.newssalad.R;


/**
 * 방송을 시작하기 전에 방송 정보를 입력하는 액티비티
 */
public class ReadyBroadcastActivity extends AppCompatActivity {

    EditText bEditText;
    Button bButton;
    String genre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_broadcast);

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

        final Spinner spinner_field = (Spinner) findViewById(R.id.spinner_field);
        String[] str = getResources().getStringArray(R.array.spinnerArray);
        final ArrayAdapter<String> adapter= new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,str);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner_field.setAdapter(adapter);

        //spinner 이벤트 리스너

        spinner_field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (spinner_field.getSelectedItemPosition() > 0) {

                    //선택된 항목
                    genre = spinner_field.getSelectedItem().toString();
                    Log.v("알림", spinner_field.getSelectedItem().toString() + "is selected");

                }

            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        }
}
