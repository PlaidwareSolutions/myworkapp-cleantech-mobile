package com.example.rfidapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.rfidapp.entity.BleItemEntity;

import java.util.List;

@Dao
public interface BleItemsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertBleItem(BleItemEntity bleItemEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllBleItems(List<BleItemEntity> list);

    @Query("DELETE FROM BleItem WHERE id = :bleItemId")
    void deleteBleItem(String bleItemId);

    @Query("SELECT * FROM BleItem")
    LiveData<List<BleItemEntity>> getAllBleItems();
}
