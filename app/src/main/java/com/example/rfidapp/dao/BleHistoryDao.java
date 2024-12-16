package com.example.rfidapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.rfidapp.entity.BleEntity;
import com.example.rfidapp.entity.BleItemEntity;

import java.util.List;

@Dao()
public interface BleHistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertBleEntity(BleEntity bleEntity);

    @Delete
    void deleteBleItem(BleEntity bleEntity);

    @Query("DELETE FROM ble_history WHERE id = :historyID")
    void deleteBleItemsByHistoryID(String historyID);

    @Query("SELECT * FROM ble_history")
    LiveData<List<BleEntity>> getAllBleEntities();

    @Query("SELECT * FROM BleItem WHERE historyID = :historyID1")
    LiveData<List<BleItemEntity>> getBleItemsByHistoryID(String historyID1);
}
