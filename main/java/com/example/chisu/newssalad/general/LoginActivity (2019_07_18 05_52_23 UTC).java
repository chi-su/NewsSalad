package com.example.chisu.newssalad.general;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chisu.newssalad.R;
import com.example.chisu.newssalad.utils.SharedPreferenceManager;
import com.example.chisu.newssalad.utils.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText loginInputEmail;
    EditText loginInputPw;
    Button loginBtn;
    Button loginRegisterBtn;
    String gotEmail;
    String gotPw;
    //나중에 url들은 따로 모아놔야겠다...
    public static final String LOGIN_URL = "http://13.125.67.216/LoginApi.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //로그인되어있는 상태라면
        if (SharedPreferenceManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, HomeActivity.class));
        }

        loginInputEmail = findViewById(R.id.loginInputEmail);
        loginInputPw = findViewById(R.id.loginInputPw);
        loginBtn = findViewById(R.id.loginBtn);
        loginRegisterBtn = findViewById(R.id.loginRegisterBtn);

        loginRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), UserRegisterActivity.class);
//                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 gotEmail = loginInputEmail.getText().toString();
                 gotPw = loginInputPw.getText().toString();

                //따온 정보를 보내서 체크한다..
                checkUserWithDb();
            }
        });
    }

    public void checkUserWithDb(){

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            try {
                                //Json 객체 넘겨받기.
                                JSONObject jsonObject = new JSONObject(response);

                                //만약 서버에서 유저 객체 대신 fail이라는 메시지가 날아온다면
                                if (!jsonObject.getBoolean("error")) {

                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    JSONObject userJson = jsonObject.getJSONObject("user");

                                    //저장할 유저 객체 생성.
                                    User user = new User(
                                            userJson.getInt("userId"),
                                            userJson.getString("userName"),
                                            userJson.getString("userEmail"),
                                            userJson.getString("userImage"),
                                            userJson.getString("userWallet"),
                                            userJson.getString("userWalletFile")
                                    );

                                    //storing the user in shared preferences
                                    SharedPreferenceManager.getInstance(getApplicationContext()).userLogin(user);
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                                } else {
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }

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
                params.put("userEmail", gotEmail);
                params.put("userPw", gotPw);
                Log.e("newsSalad", "로그인 정보 전송 완료");
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
