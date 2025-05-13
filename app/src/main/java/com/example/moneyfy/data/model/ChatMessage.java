package com.example.moneyfy.data.model;

public class ChatMessage {
    public static final int TYPE_USER = 0;
    public static final int TYPE_GPT = 1;

    public final String message;
    public final int type;

    public ChatMessage(String message, int type) {
        this.message = message;
        this.type = type;
    }
}
