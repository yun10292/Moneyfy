package com.example.moneyfy.data.room;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(
        tableName = "saving_goals",
        indices = {@Index(value = {"startDate", "endDate"}, unique = true)}
)
public class SavingGoal {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long startDate;
    public long endDate;

    public int goalAmount;
    public int currentSavedAmount;
}

