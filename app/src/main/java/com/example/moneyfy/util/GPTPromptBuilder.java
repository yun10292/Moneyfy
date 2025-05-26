package com.example.moneyfy.util;

import androidx.annotation.Nullable;

import com.example.moneyfy.data.room.CategorySum;
import java.util.List;

public class GPTPromptBuilder {

    public static String buildPromptWithType(
            @Nullable String userInput,
            @Nullable String promptType,
            List<CategorySum> expenseSummary,
            List<CategorySum> incomeSummary
    ) {
        StringBuilder builder = new StringBuilder();

        // 1. 사용자 입력 또는 추천 키워드 출력
        if (userInput != null && !userInput.trim().isEmpty()) {
            builder.append("사용자 질문: ").append(userInput).append("\n\n");
        } else {
            builder.append("추천 키워드 요청: ").append(promptType).append("\n\n");
        }

        // 2. 수입 요약
        builder.append("이번 달 수입 내역:\n");
        int incomeTotal = 0;
        for (CategorySum item : incomeSummary) {
            builder.append("- ").append(item.category).append(": ").append(item.total).append("원\n");
            incomeTotal += item.total;
        }
        builder.append("총 수입: ").append(incomeTotal).append("원\n\n");

        // 3. 지출 요약
        builder.append("이번 달 지출 내역:\n");
        int expenseTotal = 0;
        for (CategorySum item : expenseSummary) {
            builder.append("- ").append(item.category).append(": ").append(item.total).append("원\n");
            expenseTotal += item.total;
        }
        builder.append("총 지출: ").append(expenseTotal).append("원\n\n");

        // 4. 프롬프트 헤더 공통 문구
        builder.append("너는 스마트 가계부 앱에서 소비 분석 피드백을 제공하는 재정 코치야.\n");

        // 5. 버튼 종류에 따른 분석 요청 문구 분기
        if (promptType == null && userInput != null) {
            builder.append("위 데이터를 참고해서 사용자 질문에 맞는 맞춤형 피드백을 제공해줘.\n");
        } else if ("소비 분석".equals(promptType)) {
            builder.append("다음 소비 데이터를 바탕으로 소비 습관의 전반적인 특징과 지출 경향만 간단히 요약해줘.\n");
            builder.append("절약 방법이나 저축 전략은 생략하고, 분석에만 집중해줘.\n");
        } else if ("절약 팁 알려줘".equals(promptType)) {
            builder.append("교통, 식비, 쇼핑 등의 항목에서 과소비를 줄일 수 있는 **즉시 실행 가능한 행동 팁**만 알려줘.\n");
            builder.append("저축, 목표 설정, 자동이체, 금융 상품 등 금전 관리 전략에 관한 단어는 절대 사용하지 마.\n");
        } else if ("예산 설정 도움".equals(promptType)) {
            builder.append("이번 달 소비 데이터를 바탕으로 다음 달 예산을 설계해줘.\n");
            builder.append("항목별로 적절한 예산 수치를 제안하되, 아래의 5개 소비 영역을 기준으로 묶어서 판단해줘:\n");
            builder.append("- 생존 필수 항목 (식비, 주거/통신, 건강)\n");
            builder.append("- 생활 유지 항목 (마트/편의점, 생활용품, 교육)\n");
            builder.append("- 교통 관련 항목 (교통/차량)\n");
            builder.append("- 유동 소비 항목 (문화생활, 패션/미용, 경조사/회비)\n");
            builder.append("- 가족 지원 항목 (부모님)\n\n");
            builder.append("각 그룹별 지출 성향을 분석하고, 다음 달 적정 예산 금액을 수치로 제시해줘.\n");
            builder.append("절약 팁이나 소비 습관 조언 없이, 오직 예산 수치 중심으로 구성해줘.\n");
            builder.append("남은 예산은 저축이나 비상금으로 배분해도 좋아.\n");
        }
        else {
            builder.append("소비 습관 분석과 조언을 자유롭게 구성해서 알려줘.\n");
        }

        // 6. 공통 요청 조건
        builder.append("\n단, 생존에 필수적인 지출(예: 식비 등)은 무리하게 줄이라고 하지 말고,\n");
        builder.append("과도하게 지출된 항목 위주로 현실적인 조언을 제시해줘.\n");

        return builder.toString();
    }

}
