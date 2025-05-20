package com.example.moneyfy.data.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Transaction.class, Budget.class, SavingGoal.class, Asset.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    // DAO 인터페이스 연결
    public abstract TransactionDao transactionDao();
    public abstract BudgetDao budgetDao();
    public abstract SavingGoalDao savingGoalDao();

    public abstract AssetDao assetDao();


    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "moneyfy_db"
            ).fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}

