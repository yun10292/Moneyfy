package com.example.moneyfy.data.network;

import java.util.List;

public class GPTResponse {
    private List<Choice> choices;

    public String getFeedback() {
        return choices.get(0).message.content;
    }

    public static class Choice {
        public Message message;
    }

    public static class Message {
        public String role;
        public String content;
    }
}
