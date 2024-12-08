package com.example.rfidapp.synthetic;

import com.example.rfidapp.fragment.InventoryItems;
import com.rscja.barcode.BarcodeDecoder;
import com.rscja.deviceapi.entity.BarcodeEntity;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class InventoryItems$$ExternalSyntheticLambda0 implements BarcodeDecoder.DecodeCallback {
    public final /* synthetic */ InventoryItems f$0;

    public /* synthetic */ InventoryItems$$ExternalSyntheticLambda0(InventoryItems inventoryItems) {
        this.f$0 = inventoryItems;
    }

    public final void onDecodeComplete(BarcodeEntity barcodeEntity) {
        this.f$0.m547lambda$open$13$comruddersoftrfidscannerviewsfragmentsInventoryItems(barcodeEntity);
    }
}
