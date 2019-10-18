package com.example.chisu.newssalad.utils;

import android.content.Context;
import android.content.SharedPreferences;

//유저 정보를 저장할 sp를 쉽게 관리하기 위해 만든 클래스.
public class SharedPreferenceManager {

    //저장할 데이터들의 형태 정의(보통 스트링이 편함)
    //sharedpreference의 이름도 아예 정의. 하나만 쓰기 때문에 일일이 입력하는 것보다 이게 낫다.
    public static final String SHARED_PREF_NAME = "sharedpref";
    public static final String KEY_USERNAME = "keyUsername";
    private static final String KEY_USER_EMAIL = "keyUserEmail";
    private static final String KEY_USER_IMAGE = "keyUserImage";
    private static final String KEY_USER_ID = "keyUserId";
    public static final String KEY_USER_WALLET_ADDRESS = "keyUserWalletAddress";
    public static final String KEY_USER_WALLET_FILE_PATH = "keyUserWalletFilePath";

    // 클래스, Context 객체 생성
    private static SharedPreferenceManager mInstance;
    static Context mCtx;

    //생성자
    private SharedPreferenceManager(Context context) {
        mCtx = context;
    }

    // 예외처리 메소드(클래스가 없을 경우) 정의
    public static synchronized SharedPreferenceManager getInstance(Context context) {
        if (mInstance == null) { //sharedpreferencemanager 객체가 null이라면 새로 생성한다.
            mInstance = new SharedPreferenceManager(context);
        }
        return mInstance;
    }

    //유저 로그인을 위한 메소드.
    //이 메소드는 sharedpreference에 유저의 데이터를 저장한다.
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_IMAGE, user.getUserImage());
        editor.putString(KEY_USER_WALLET_ADDRESS, user.getUserWallet());
        editor.putString(KEY_USER_WALLET_FILE_PATH, user.getUserWalletFile());
        editor.apply();
    }

    //이 메소드는 유저가 이미 로그인했는지 아닌지 체크한다.
    //sp에 들어있는 username이 null인지 아닌지 체크해서 true 혹은 false를 리턴한다.
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //sp의 username이 null이면(비로그인) false를 리턴하고, null이 아니면(로그인) true를 리턴한다.
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    //이 메소드는 로그인한 유저에게 실행된다.
    //sp에 저장되어 있던 정보를 꺼내는 메소드.
    //this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(KEY_USER_ID, -1),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_USER_EMAIL, null),
                sharedPreferences.getString(KEY_USER_IMAGE, null),
                sharedPreferences.getString(KEY_USER_WALLET_ADDRESS, null),
                sharedPreferences.getString(KEY_USER_WALLET_FILE_PATH, null)
        );
    }

    //이 메소드는 유저가 로그아웃할 때 사용하는 메소드다.
    //로그아웃을 하므로 sp에 있던 정보를 싹 지우고 저장한다.
    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
