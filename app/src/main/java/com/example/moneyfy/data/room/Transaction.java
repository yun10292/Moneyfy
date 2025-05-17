package com.example.moneyfy.data.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String type;       // "expense" or "income"
    public int amount;

    public String category;

    public String method;
    public String memo;
    public long date;         // timestamp
}
