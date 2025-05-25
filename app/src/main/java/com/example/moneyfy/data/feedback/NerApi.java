package com.example.moneyfy.data.feedback;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NerApi {
    @POST("/parse")
    Call<SmsResponse> parseSms(@Body SmsRequest request);
}

