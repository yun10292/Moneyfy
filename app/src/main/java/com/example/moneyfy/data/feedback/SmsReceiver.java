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
import android.widget.Toast;

import com.example.moneyfy.data.feedback.NerApi;
import com.example.moneyfy.data.feedback.SmsRequest;
import com.example.moneyfy.data.feedback.SmsResponse;
import com.example.moneyfy.data.model.ExpenseItem;
import com.example.moneyfy.data.room.AppDatabase;
import com.example.moneyfy.data.room.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

        // "총누적"이 있다면 그 앞까지만 잘라내기
        int index = smsBody.indexOf("총누적");
        if (index != -1) {
            smsBody = smsBody.substring(0, index).trim();  // "총누적" 앞까지만 유지
            Log.d("SMSReceiver", "'총누적' 이후 삭제된 문자: " + smsBody);
        }

        if (smsBody.contains("카드") || smsBody.contains("거래시간") ||
                smsBody.contains("KB국민") ||
                smsBody.contains("신한카드") ||
                smsBody.contains("우리카드") ||
                smsBody.contains("IBK기업") ||
                smsBody.contains("NH농협") ||
                smsBody.contains("BC바로")) {
            sendToFlask(context, smsBody);
        } else {
            Log.d("SMSReceiver", "일반 문자로 판단되어 무시함");
        }
    }

    private void sendToFlask(Context context, String smsText) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ner-sms-server.onrender.com/")  // 나의 PC IP 주소 http://192.168.0.40:5000/
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
                } else {
                    Log.e("SMSReceiver", "서버 응답이 null입니다!");
                }
            }

            @Override
            public void onFailure(Call<SmsResponse> call, Throwable t) {
                Log.e("SMSReceiver", "Flask 서버 통신 실패: " + t.getMessage());
            }
        });
    }


    private void insertToDatabase(Context context, SmsResponse res) {
        Log.d("SMSReceiver", "insertToDatabase 진입 성공");

        Transaction t = new Transaction();
        t.type = "expense";
        t.amount = Integer.parseInt(res.getAmount().replaceAll("[^\\d]", "")); // 숫자만 추출
        t.category = res.getCategory();
        t.method = "카드";
        t.memo = res.getStore();
        try {
            String raw = res.getDatetime(); // "05/21 14:32"
            String datetimeWithYear = "2025/" + raw; // → "2025/05/21 14:32"

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
            Date parsedDate = sdf.parse(datetimeWithYear);
            t.date = parsedDate.getTime();
            Log.d("SMSReceiver", "날짜 파싱 성공: " + res.getDatetime() + " → " + t.date);
        } catch (ParseException e) {
            t.date = System.currentTimeMillis(); // 실패 시 현재 시각으로
            Log.e("SMSReceiver", "날짜 파싱 실패! 현재 시간으로 저장됨. 원본 문자열: " + res.getDatetime());
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(context)
                    .transactionDao()
                    .insert(t);
                    Log.d("SMSReceiver", "SMS 저장 완료: " + t.amount + "원 " + t.memo);
                    Log.d("SMSReceiver", "카테고리 확인: " + res.getCategory());
        });
    }

}