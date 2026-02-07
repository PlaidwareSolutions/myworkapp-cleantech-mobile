package com.example.rfidapp.repo;

import android.app.Application;


import com.example.rfidapp.dao.InvListDao;
import com.example.rfidapp.database.InvDB;
import com.example.rfidapp.entity.InventoryListEntity;

import java.util.List;

import io.reactivex.Single;


public class InvListRepo {
    InvListDao invListDao;

    public InvListRepo(Application application) {
        this.invListDao = InvDB.getInstance(application).invListDao();
    }

    public void insert(InventoryListEntity inventoryListEntity) {
        this.invListDao.insertListData(inventoryListEntity);
    }

    public void update(InventoryListEntity inventoryListEntity) {
        this.invListDao.updateListData(inventoryListEntity);
    }

    public Single<List<InventoryListEntity>> getAllProducts(String str, int i, int i2) {
        return this.invListDao.getInvListData(str, i, i2);
    }

    public int getInvId(String str) {
        return this.invListDao.getInvId(str);
    }

    public Single<List<InventoryListEntity>> getCount(String str) {
        return this.invListDao.getCount(str);
    }
}
