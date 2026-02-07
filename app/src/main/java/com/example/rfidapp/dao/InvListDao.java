package com.example.rfidapp.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.rfidapp.entity.InventoryListEntity;

import java.util.List;

import io.reactivex.Single;;

@Dao
public interface InvListDao {
    @Query("SELECT * FROM InventoryList WHERE invName = :invName")
    Single<List<InventoryListEntity>> getCount(String invName);

    @Query("SELECT invId FROM InventoryList WHERE invName = :invName")
    int getInvId(String invName);

    @Query("SELECT * FROM InventoryList WHERE invName = :invName LIMIT :limit OFFSET :offset")
    Single<List<InventoryListEntity>> getInvListData(String invName, int limit, int offset);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertListData(InventoryListEntity inventoryListEntity);

    @Query("UPDATE InventoryList SET invName = :newName WHERE invName = :oldName")
    void update(String oldName, String newName);

    @Update
    void updateListData(InventoryListEntity inventoryListEntity);

    @Query("UPDATE InventoryList SET invName = :newName WHERE invName = :oldName")
    int updateName(String oldName, String newName);
}
