package com.example.chisu.newssalad.general;

//홈 화면의 리사이클러뷰 아이템 정의.
//제목, 저자, 그림이 필요함.

public class LiveListItem {

    private String homeDescription;

    public LiveListItem(String homeDescription){
        this.homeDescription = homeDescription;
    }

    public String getHomeDescription(){
        return homeDescription;
    }

}
