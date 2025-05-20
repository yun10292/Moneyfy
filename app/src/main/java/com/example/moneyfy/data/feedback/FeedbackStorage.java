package com.example.moneyfy.data.feedback;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FeedbackStorage {

    private static final String FILE_NAME = "user_feedback.json";

    public static void saveFeedback(Context context, String store, String correctCategory) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        JSONArray feedbackArray = new JSONArray();

        // 기존 데이터 불러오기
        if (file.exists()) {
            try {
                StringBuilder jsonBuilder = new StringBuilder();
                Scanner scanner = new Scanner(file, StandardCharsets.UTF_8.name());
                while (scanner.hasNextLine()) {
                    jsonBuilder.append(scanner.nextLine());
                }
                scanner.close();
                feedbackArray = new JSONArray(jsonBuilder.toString());
            } catch (Exception e) {
                e.printStackTrace(); // 파일 읽기 에러 처리
            }
        }

        // 새로운 피드백 추가
        JSONObject feedbackObject = new JSONObject();
        try {
            feedbackObject.put("store", store);
            feedbackObject.put("correct_category", correctCategory);
            feedbackObject.put("timestamp", System.currentTimeMillis());
            feedbackArray.put(feedbackObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 파일에 저장
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(feedbackArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}