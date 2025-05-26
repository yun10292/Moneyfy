
package com.example.moneyfy.ui.advice;
import com.example.moneyfy.BuildConfig;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyfy.data.model.ChatMessage;
import com.example.moneyfy.data.network.GPTRequest;
import com.example.moneyfy.data.network.GPTResponse;
import com.example.moneyfy.data.network.GPTService;
import com.example.moneyfy.data.room.AppDatabase;
import com.example.moneyfy.data.room.CategorySum;
import com.example.moneyfy.data.room.TransactionDao;
import com.example.moneyfy.util.GPTPromptBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdviceViewModel extends AndroidViewModel {
    private boolean hasFetchedAdvice = false;

    private final MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private final TransactionDao transactionDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AdviceViewModel(@NonNull Application application) {
        super(application);
        transactionDao = AppDatabase.getInstance(application).transactionDao();
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    // 채팅 메시지 추가 메서드
    public void appendMessage(ChatMessage message) {
        List<ChatMessage> current = messages.getValue();
        current.add(message);
        messages.postValue(current);
    }

    // 공통 처리 메서드: userInput이 null이면 추천 버튼, 아니면 사용자 입력 기반
    private void fetchAdviceFromGpt(@Nullable String userInput, @Nullable String promptType) {
        executor.execute(() -> {
            try {
                // 1. 현재 연월 구하기 (예: 202405)
                String yearMonth = new SimpleDateFormat("yyyyMM", Locale.getDefault()).format(new Date());

                // 2. 월별 수입/지출 데이터 불러오기
                List<CategorySum> expenseSummary = transactionDao.getMonthlyAmountByCategory("expense", yearMonth);
                List<CategorySum> incomeSummary = transactionDao.getMonthlyAmountByCategory("income", yearMonth);

                // 3. GPT 프롬프트 생성 (추천 버튼이든, 사용자 입력이든 모두 이 메서드로 통일)
                String prompt = GPTPromptBuilder.buildPromptWithType(userInput, promptType, expenseSummary, incomeSummary);

                // 4. 프롬프트 자체를 사용자 메시지로 보여주고 GPT 요청
                // appendMessage(new ChatMessage(prompt, ChatMessage.TYPE_USER));
                sendPromptToGPT(prompt);

            } catch (Exception e) {
                appendMessage(new ChatMessage("오류 발생: " + e.getMessage(), ChatMessage.TYPE_GPT));
            }
        });
    }


    // 추천 버튼 클릭 시 실행
    // 각각의 해당 버튼 텍스트를 사용자 메시지처럼 추가
    // 버튼/입력 구분 없이 프롬프트 처리
    public void handleUserPrompt(String input, boolean isFromButton) {
        appendMessage(new ChatMessage(input, ChatMessage.TYPE_USER));
        fetchAdviceFromGpt(isFromButton ? null : input, isFromButton ? input : null);

    }

    // 추천 버튼 클릭 시 호출
    /* public void fetchGptAdvice() {
        fetchAdviceFromGpt(null);
    } */

    // 완성된 promt를 GPT에 보내고 응답 받는 비동기 처리 함수
    private void sendPromptToGPT(String prompt) {
        String token = "Bearer " + BuildConfig.OPENAI_API_KEY;

        executor.execute(() -> {
            try {
                GPTRequest request = new GPTRequest(prompt);
                GPTService service = new Retrofit.Builder()
                        .baseUrl("https://api.openai.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(GPTService.class);

                Call<GPTResponse> call = service.getFeedback(token, request);
                Response<GPTResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    String reply = response.body().getFeedback();
                    appendMessage(new ChatMessage(reply, ChatMessage.TYPE_GPT));
                } else {
                    appendMessage(new ChatMessage("GPT 응답 실패: " + response.code(), ChatMessage.TYPE_GPT));
                }

            } catch (Exception e) {
                appendMessage(new ChatMessage("오류 발생: " + e.getMessage(), ChatMessage.TYPE_GPT));
            }
        });
    }

//    // 월별 소비 요약을 기반으로 GPT에 조언 요청
//    public void fetchGptAdvice() {
//        String token = "Bearer " + BuildConfig.OPENAI_API_KEY;
//
//        executor.execute(() -> {
//            try {
//                // 1. 월별 소비 요약
//                String yearMonth = new SimpleDateFormat("yyyyMM", Locale.getDefault()).format(new Date());
//                List<CategorySum> summary = transactionDao.getMonthlyAmountByCategory("지출", yearMonth);
//
//                // 2. 프롬프트 생성
//                String prompt = GPTPromptBuilder.buildMonthlyFeedbackPrompt(summary);
//                appendMessage(new ChatMessage(prompt, ChatMessage.TYPE_USER));
//                sendPromptToGPT(prompt);
//
//            } catch (Exception e) {
//                appendMessage(new ChatMessage("오류 발생: " + e.getMessage(), ChatMessage.TYPE_GPT));
//            }
//        });
//    }


}
