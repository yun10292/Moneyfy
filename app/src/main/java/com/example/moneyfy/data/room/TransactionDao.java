package com.example.moneyfy.data.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TransactionDao {

    // 삽입
    @Insert
    void insert(Transaction transaction);

    // 수정
    @Update
    void update(Transaction transaction);


    // 삭제
    @Delete
    void delete(Transaction transaction);

    // 전체 조회 (최신순)
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<Transaction> getAll();

    // 타입별 조회 (지출 or 수입)
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    List<Transaction> getByType(String type);

    // 카테고리 필터 조회 (예: 저축만)
    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    List<Transaction> getByCategory(String category);

    // 날짜 범위 내 조회
    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date ASC")
    List<Transaction> getBetweenDates(long start, long end);

    // 저축 합계 (기간 기준)
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense' AND category = '저축' AND date BETWEEN :start AND :end")
    Integer getTotalSaving(long start, long end);

    // 일별 지출 합계
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense' AND date BETWEEN :start AND :end")
    Integer getDailyExpense(long start, long end);

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'income' AND date BETWEEN :start AND :end")
    Integer getDailyIncome(long start, long end);


    // 월별 지출 합계
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense' AND strftime('%Y%m', date / 1000, 'unixepoch') = :yearMonth")
    Integer getMonthlyExpense(String yearMonth);

    // 월별 수입 합계
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'income' AND strftime('%Y%m', date / 1000, 'unixepoch') = :yearMonth")
    Integer getMonthlyIncome(String yearMonth);

    // 월별 지출 / 소득 카테고리별 합계
    @Query("SELECT category, SUM(amount) AS total FROM transactions WHERE type = :type AND strftime('%Y%m', date / 1000, 'unixepoch') = :yearMonth GROUP BY category ORDER BY total DESC")
    List<CategorySum> getMonthlyAmountByCategory(String type, String yearMonth);

}

