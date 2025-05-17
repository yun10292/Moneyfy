package com.example.moneyfy.data.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SavingGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SavingGoal savingGoal);

    @Delete
    void delete(SavingGoal savingGoal);

    @Query("SELECT * FROM saving_goals ORDER BY endDate DESC")
    List<SavingGoal> getAll();

    @Query("SELECT * FROM saving_goals WHERE :today BETWEEN startDate AND endDate")
    List<SavingGoal> getActiveGoals(long today);
}

