package com.example.rfidapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;


import com.example.rfidapp.entity.InventoryItemsEntity;
import com.example.rfidapp.repo.InvItemRepo;

import java.util.List;
import io.reactivex.Single;


public class InvItemsViewModel extends AndroidViewModel {
    private InvItemRepo invItemRepo;

    public InvItemsViewModel(Application application) {
        super(application);
        this.invItemRepo = new InvItemRepo(application);
    }

    public void insert(InventoryItemsEntity inventoryItemsEntity) {
        this.invItemRepo.insert(inventoryItemsEntity);
    }

    public void update(InventoryItemsEntity inventoryItemsEntity) {
        this.invItemRepo.update(inventoryItemsEntity);
    }

    public Single<List<InventoryItemsEntity>> getInvData(String str, int i, int i2) {
        return this.invItemRepo.getInvData(str, i, i2);
    }

    public Single<List<InventoryItemsEntity>> getEpcData(String str, String str2, int i, int i2) {
        return this.invItemRepo.getEpcData(str, str2, i, i2);
    }
}
