package com.example.chisu.newssalad.general.fragments;

public class VodListItem {

    private String newsTitle;
    //표시해야할 것이 이미지이더라도 이미지 경로를 서버에서 받아온다.
    private String newsThumbnailPath;
    private String newsAuthor;

    public VodListItem(String title, String author, String path){
        this.newsTitle = title;
        this.newsThumbnailPath = path;
        this.newsAuthor = author;
    }

    public String getNewsTitle(){
        return newsTitle;
    }

    public String getNewsThumbnailPath(){
        return newsThumbnailPath;
    }

    public String getNewsAuthor(){
        return newsAuthor;
    }

}
