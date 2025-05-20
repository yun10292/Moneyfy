package com.example.moneyfy.data.feedback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;

import com.example.moneyfy.data.feedback.NerApi;
import com.example.moneyfy.data.feedback.SmsRequest;
import com.example.moneyfy.data.feedback.SmsResponse;
import com.example.moneyfy.data.model.ExpenseItem;

import java.util.concurrent.Executors;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) return;

        StringBuilder messageBuilder = new StringBuilder();

        for (Object pdu : pdus) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
            messageBuilder.append(sms.getMessageBody());
        }

        String smsBody = messageBuilder.toString();

        // 서버에 보내기
        sendToFlask(context, smsBody);
    }

    private void sendToFlask(Context context, String smsText) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.40:5000/")  // 나의 PC IP 주소
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NerApi api = retrofit.create(NerApi.class);
        SmsRequest request = new SmsRequest(smsText);

        api.parseSms(request).enqueue(new Callback<SmsResponse>() {
            @Override
            public void onResponse(Call<SmsResponse> call, Response<SmsResponse> response) {
                SmsResponse res = response.body();
                if (res != null) {
                    // 추출된 데이터를 DB에 저장
                    insertToDatabase(context, res);
                }
            }

            @Override
            public void onFailure(Call<SmsResponse> call, Throwable t) {
                Log.e("SMSReceiver", "Flask 서버 통신 실패: " + t.getMessage());
            }
        });
    }


    private void insertToDatabase(Context context, SmsResponse res) {
        Transaction t = new Transaction();
        t.type = "expense";
        t.amount = Integer.parseInt(res.getAmount().replaceAll("[^\\d]", "")); // 숫자만 추출
        t.category = res.getCategory();
        t.method = "카드";
        t.memo = res.getStore();
        t.date = System.currentTimeMillis(); // 또는 추출한 날짜 파싱

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(context)
                    .transactionDao()
                    .insert(t);
        });
    }

}