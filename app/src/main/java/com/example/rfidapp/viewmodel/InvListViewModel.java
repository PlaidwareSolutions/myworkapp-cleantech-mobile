package com.example.rfidapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.rfidapp.entity.InventoryListEntity;
import com.example.rfidapp.repo.InvListRepo;

import java.util.List;

import io.reactivex.Single;;


public class InvListViewModel extends AndroidViewModel {
    private InvListRepo invListRepo;

    public InvListViewModel(Application application) {
        super(application);
        this.invListRepo = new InvListRepo(application);
    }

    public void insert(InventoryListEntity inventoryListEntity) {
        this.invListRepo.insert(inventoryListEntity);
    }

    public void update(InventoryListEntity inventoryListEntity) {
        this.invListRepo.update(inventoryListEntity);
    }

    public Single<List<InventoryListEntity>> getAllProducts(String str, int i, int i2) {
        return this.invListRepo.getAllProducts(str, i, i2);
    }

    public int getInvId(String str) {
        return this.invListRepo.getInvId(str);
    }

    public Single<List<InventoryListEntity>> getCount(String str) {
        return this.invListRepo.getCount(str);
    }
}
