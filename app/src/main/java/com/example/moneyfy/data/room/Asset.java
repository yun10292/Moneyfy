package com.example.moneyfy.data.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "asset")
public class Asset {
    @PrimaryKey
    public int id = 1; // 항상 하나만 유지하기 위한 고정 ID

    public int totalAmount;
}
