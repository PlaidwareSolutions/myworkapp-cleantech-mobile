package com.example.rfidapp.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.rfidapp.entity.InventoryItemsEntity;

import java.util.List;

import io.reactivex.Single;


@Dao
public interface InvItemsDao {
    @Query("DELETE FROM InventoryItems WHERE epc = :epc")
    int delData(String epc);

    @Query("SELECT * FROM InventoryItems")
    List<InventoryItemsEntity> getData();

    @Query("SELECT * FROM InventoryItems WHERE epc = :epc AND epcInv = :epcInv LIMIT :limit OFFSET :offset")
    Single<List<InventoryItemsEntity>> getEpcData(String epc, String epcInv, int limit, int offset);

    @Query("SELECT * FROM InventoryItems WHERE inventory = :inventory LIMIT :limit OFFSET :offset")
    Single<List<InventoryItemsEntity>> getInvData(String inventory, int limit, int offset);

    @Query("SELECT COUNT(epc) FROM InventoryItems WHERE inventory = :inventory")
    String getItemCount(String inventory);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insetData(InventoryItemsEntity inventoryItemsEntity);

    @Update
    void updateData(InventoryItemsEntity inventoryItemsEntity);
}
