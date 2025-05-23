package com.example.moneyfy.data.feedback;

import com.google.gson.annotations.SerializedName;

public class SmsResponse {

    @SerializedName("amount")
    public String amount;

    @SerializedName("store")
    public String store;

    @SerializedName("datetime")
    public String datetime;

    @SerializedName("category")
    public String category;

    public String getAmount() {
        return amount;
    }

    public String getStore() {
        return store;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
