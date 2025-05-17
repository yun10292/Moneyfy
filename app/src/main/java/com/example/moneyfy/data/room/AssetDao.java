package com.example.moneyfy.data.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AssetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Asset asset);

    @Query("SELECT * FROM asset WHERE id = 1")
    Asset get();
}

