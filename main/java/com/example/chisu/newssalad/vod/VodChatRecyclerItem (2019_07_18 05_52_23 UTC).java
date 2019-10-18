package com.example.chisu.newssalad.vod;

public class VodChatRecyclerItem {
    private String vodChatAuthor;
    private String vodChatDescription;
    private String vodChatTime;

    public VodChatRecyclerItem(String vodChatAuthor, String vodChatDescription, String vodChatTime){
        this.vodChatAuthor = vodChatAuthor;
        this.vodChatDescription = vodChatDescription;
        this.vodChatTime = vodChatTime;
    }

    public String getVodChatDescription(){
        return vodChatDescription;
    }

    public String getVodChatTime(){
        return vodChatTime;
    }

    public String getVodChatAuthor(){
        return vodChatAuthor;
    }

}
