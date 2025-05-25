package com.example.moneyfy.data.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GPTService {

    @POST("v1/chat/completions")
    Call<GPTResponse> getFeedback(
            @Header("Authorization") String token,
            @Body GPTRequest request
    );
}
