package com.example.moneyfy.data.model;

import java.io.Serializable;

public class ExpenseItem implements Serializable {
    private String cost;
    private String method;
    private String date;
    private String category;
    private String memo;

    public ExpenseItem(String cost, String method, String date, String category, String memo) {
        this.cost = cost;
        this.method = method;
        this.date = date;
        this.category = category;
        this.memo = memo;
    }

    public String getCost() {
        return cost;
    }

    public String getMethod() {
        return method;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getMemo() {
        return memo;
    }


}
