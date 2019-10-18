package com.example.chisu.newssalad.utils;

//sharedpreference에 저장할 유저 정보를 정의하는 클래스.
public class User {

    //db의 user 테이블과 호환될 정보들
    private int id;
    private String username, email, userImage, userWallet, userWalletFile;

    //생성자
    public User(int id, String username, String email, String userImage, String userWallet, String userWalletFile) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userImage = userImage;
        this.userWallet = userWallet;
        this.userWalletFile = userWalletFile;
    }

    //getter들
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getUserWallet(){
        return userWallet;
    }

    public String getUserWalletFile(){
        return userWalletFile;
    }
}
