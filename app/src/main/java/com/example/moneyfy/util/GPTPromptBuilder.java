package com.example.moneyfy.util;

import com.example.moneyfy.data.room.CategorySum;
import java.util.List;

public class GPTPromptBuilder {

    public static String buildMonthlyFeedbackPrompt(List<CategorySum> summary) {
        StringBuilder sb = new StringBuilder("이번 달 소비 내역:\n\n");
        int total = 0;
        for (CategorySum item : summary) {
            sb.append(item.category).append(": ").append(item.total).append("원\n");
            total += item.total;
        }
        sb.append("\n총합: ").append(total).append("원\n\n");
        sb.append("이 내용을 바탕으로 따뜻한 말투로 피드백을 주고, 절약 팁도 하나 알려줘.");
        return sb.toString();
    }
}
