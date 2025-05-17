package com.example.moneyfy.data.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BudgetDao {

    // 예산 삽입 또는 갱신 (같은 달이면 덮어쓰기)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Budget budget);

    // 특정 월 예산 가져오기
    @Query("SELECT * FROM budget WHERE yearMonth = :yearMonth")
    Budget getByYearMonth(int yearMonth);

    // 전체 예산 목록 (최신순)
    @Query("SELECT * FROM budget ORDER BY yearMonth DESC")
    List<Budget> getAll();
}

