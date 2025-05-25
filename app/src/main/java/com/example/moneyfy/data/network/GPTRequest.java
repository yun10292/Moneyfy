package com.example.moneyfy.data.network;

import java.util.ArrayList;
import java.util.List;

// GPT 요청 보낼 때 필요한 데이터 형식 정의
public class GPTRequest {
    private final String model = "gpt-3.5-turbo";
    private final List<Message> messages;
    private final double temperature = 0.8;

    public GPTRequest(String prompt) {
        messages = new ArrayList<>();
        messages.add(new Message("user", prompt));
    }

    public String getModel() { return model; }
    public List<Message> getMessages() { return messages; }
    public double getTemperature() { return temperature; }

    public static class Message {
        private final String role;
        private final String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public String getContent() { return content; }
    }
}
