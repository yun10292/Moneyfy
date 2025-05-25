
package com.example.moneyfy.ui.advice;
import com.example.moneyfy.BuildConfig;

import android.app.Application;

import androidx.annotation.NonNull;
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

    public void fetchGptAdvice() {
        String token = "Bearer " + BuildConfig.OPENAI_API_KEY;

        executor.execute(() -> {
            try {
                // 1. 월별 소비 요약
                String yearMonth = new SimpleDateFormat("yyyyMM", Locale.getDefault()).format(new Date());
                List<CategorySum> summary = transactionDao.getMonthlyAmountByCategory("지출", yearMonth);

                // 2. 프롬프트 생성
                String prompt = GPTPromptBuilder.buildMonthlyFeedbackPrompt(summary);
                appendMessage(new ChatMessage(prompt, ChatMessage.TYPE_USER));

                // 3. GPT 요청
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
                    String errorMsg = "응답 실패. 코드: " + response.code() + ", 메시지: " + response.message();
                    appendMessage(new ChatMessage(errorMsg, ChatMessage.TYPE_GPT));
                }

            } catch (Exception e) {
                appendMessage(new ChatMessage("오류 발생: " + e.getMessage(), ChatMessage.TYPE_GPT));
            }
        });
    }

    private void appendMessage(ChatMessage message) {
        List<ChatMessage> current = messages.getValue();
        current.add(message);
        messages.postValue(current);
    }
}
