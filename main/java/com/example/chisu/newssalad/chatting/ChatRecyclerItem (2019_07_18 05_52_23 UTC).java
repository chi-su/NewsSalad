package com.example.chisu.newssalad.chatting;

public class ChatRecyclerItem {
    private String chatAuthor;
    private String chatDescription;

    public ChatRecyclerItem(String chatDescription){
        this.chatDescription = chatDescription;
    }

    public ChatRecyclerItem(String chatDescription, String chatAuthor){
        this.chatDescription = chatDescription;
        this.chatAuthor = chatAuthor;
    }

    public String getChatDescription(){
        return chatDescription;
    }

    public String getChatAuthor(){
        return chatAuthor;
    }

}
