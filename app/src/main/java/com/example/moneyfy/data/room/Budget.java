package com.example.moneyfy.data.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budget")
public class Budget {
    @PrimaryKey
    public int yearMonth; // 예: 202505

    public int budgetAmount;
}

