package com.example.rfidapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.rfidapp.dao.BleHistoryDao;
import com.example.rfidapp.dao.BleItemsDao;
import com.example.rfidapp.dao.InvItemsDao;
import com.example.rfidapp.dao.InvListDao;
import com.example.rfidapp.entity.BleEntity;
import com.example.rfidapp.entity.BleItemEntity;
import com.example.rfidapp.entity.InventoryItemsEntity;
import com.example.rfidapp.entity.InventoryListEntity;

@Database(entities = {InventoryItemsEntity.class, InventoryListEntity.class, BleEntity.class, BleItemEntity.class}, version = 1, exportSchema = false)
public abstract class InvDB extends RoomDatabase {
    public static InvDB instance;

    public abstract BleHistoryDao bleHistoryDao();

    public abstract BleItemsDao bleItemsDao();

    public abstract InvItemsDao invItemsDao();

    public abstract InvListDao invListDao();

    public static synchronized InvDB getInstance(Context context) {
        InvDB invDB;
        synchronized (InvDB.class) {
            if (instance == null) {
                instance = Room.databaseBuilder(context.getApplicationContext(), InvDB.class, "Inventory_db").fallbackToDestructiveMigration().build();
            }
            invDB = instance;
        }
        return invDB;
    }
}
