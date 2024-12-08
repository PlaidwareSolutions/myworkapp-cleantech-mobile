package com.example.rfidapp.repo;

import android.app.Application;


import com.example.rfidapp.dao.InvItemsDao;
import com.example.rfidapp.database.InvDB;
import com.example.rfidapp.entity.InventoryItemsEntity;

import java.util.List;

import io.reactivex.Single;


public class InvItemRepo {
    InvItemsDao invItemsDao;

    public InvItemRepo(Application application) {
        this.invItemsDao = InvDB.getInstance(application).invItemsDao();
    }

    public void insert(InventoryItemsEntity inventoryItemsEntity) {
        this.invItemsDao.insetData(inventoryItemsEntity);
    }

    public void update(InventoryItemsEntity inventoryItemsEntity) {
        this.invItemsDao.updateData(inventoryItemsEntity);
    }

    public Single<List<InventoryItemsEntity>> getInvData(String str, int i, int i2) {
        return this.invItemsDao.getInvData(str, i, i2);
    }

    public Single<List<InventoryItemsEntity>> getEpcData(String str, String str2, int i, int i2) {
        return this.invItemsDao.getEpcData(str, str2, i, i2);
    }
}
