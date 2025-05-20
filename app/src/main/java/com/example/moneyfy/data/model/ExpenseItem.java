package com.example.moneyfy.data.model;

import java.io.Serializable;

public class ExpenseItem implements Serializable {

    private int id;

    private String cost;
    private String method;
    private String date;
    private String category;
    private String memo;

    private String type;

    public ExpenseItem(int id, String cost, String method, String date, String category, String memo, String type) {
        this.id = id;
        this.cost = cost;
        this.method = method;
        this.date = date;
        this.category = category;
        this.memo = memo;
        this.type = type;
    }

    // Getter & Setter for id
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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

    public String getType() {
        return type;
    }
}
