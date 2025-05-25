package com.example.moneyfy.data.feedback;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FeedbackApi {
    @POST("/feedback")
    public Call<Void> sendFeedback(@Body FeedbackData data);
}
