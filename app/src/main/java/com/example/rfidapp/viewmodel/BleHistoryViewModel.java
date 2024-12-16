package com.example.rfidapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.rfidapp.dao.BleHistoryDao;
import com.example.rfidapp.database.InvDB;
import com.example.rfidapp.entity.BleEntity;
import com.example.rfidapp.util.Util;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BleHistoryViewModel extends AndroidViewModel {
    private LiveData<List<BleEntity>> allBleEntities;
    private BleHistoryDao bleHistoryDao;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public BleHistoryViewModel(Application application) {
        super(application);
        BleHistoryDao bleHistoryDao2 = InvDB.getInstance(application).bleHistoryDao();
        this.bleHistoryDao = bleHistoryDao2;
        this.allBleEntities = bleHistoryDao2.getAllBleEntities();
    }

    public LiveData<List<BleEntity>> getAllBleEntities() {
        return this.allBleEntities;
    }

    public void saveBleEntity(String str, String str2) {
        String dateTime = Util.getDateTime();
        BleEntity bleEntity = new BleEntity(str2, str);
        bleEntity.setTime(dateTime);
        executorService.execute(() -> insertData(bleEntity));
    }

    public void insertData(BleEntity bleEntity) {
        this.bleHistoryDao.insertBleEntity(bleEntity);
    }

    @Override
    public void onCleared() {
        super.onCleared();
        this.executorService.shutdown();
    }
}
