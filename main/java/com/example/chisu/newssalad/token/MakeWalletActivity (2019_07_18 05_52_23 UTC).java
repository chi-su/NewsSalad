package com.example.chisu.newssalad.token;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chisu.newssalad.R;
import com.example.chisu.newssalad.utils.SharedPreferenceManager;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

//지갑이 없을 경우 지갑을 만드는 액티비티.
//지갑을 만들면 그 지갑을 유저의 sharedpreference에 넣어준다.
public class MakeWalletActivity extends AppCompatActivity {

    String password;
    EditText gotText;
    Button createBtn;
    Credentials credentials;

    private String fileName;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_wallet);

        //외부 저장소 쓰기 권한 여부 확인하기. 권한 없으면 못 씀
        isStoragePermissionGranted();

        gotText = findViewById(R.id.walletPw);
        createBtn = findViewById(R.id.createWalletButton);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = gotText.getText().toString();
                createWallet(password);
            }
        });
    }

    //지갑을 만드는 함수
    public void createWallet(final String password) {
        // 생성한 지갑파일의 경로를 담을 문자열 변수.
        String result;
        try {
            //다운로드 path 가져오기
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!path.exists()) {
                path.mkdir();
            }
            //지갑생성
            this.fileName = WalletUtils.generateLightNewWalletFile(password, file = new File(String.valueOf(path)));

            result = path+"/"+fileName;

            //파일 이름에서 주소를 추출하기 위해 인증서를 로드한다.
            credentials = WalletUtils.loadCredentials(password, result);

            //지갑 생성 후에는 지갑 주소와 지갑 파일의 경로를 sharedPreference에 넣어준다.
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SharedPreferenceManager.SHARED_PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SharedPreferenceManager.KEY_USER_WALLET_ADDRESS, credentials.getAddress());
            editor.putString(SharedPreferenceManager.KEY_USER_WALLET_FILE_PATH, result);
            editor.apply();
            Log.e("newsSalad ", "지갑이 생성되었습니다.");

        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Log.e("newsSalad error : ",errors.toString());
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("newsSalad","Permission is granted");
                return true;
            } else {
                Log.v("newsSalad","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("newsSalad","Permission is granted");
            return true;
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("newsSalad","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }}
