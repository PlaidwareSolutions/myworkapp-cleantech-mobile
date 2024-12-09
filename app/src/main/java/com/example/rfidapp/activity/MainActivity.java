package com.example.rfidapp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.rfidapp.R;
import com.example.rfidapp.ReaderClass;
import com.example.rfidapp.database.InvDB;
import com.example.rfidapp.databinding.ActivityMainBinding;
import com.example.rfidapp.fragment.Dashboard;
import com.example.rfidapp.fragment.InventoryItems;
import com.example.rfidapp.util.PreferenceManager;
import com.example.rfidapp.util.Utils;
import com.example.rfidapp.util.constants.Constants;
import com.example.rfidapp.viewmodel.InvListViewModel;
import com.google.android.material.slider.Slider;
import com.rscja.deviceapi.interfaces.ConnectionStatus;

public class MainActivity extends ReaderClass {
    AudioManager audioManager;
    ActivityMainBinding binding;
    public Fragment fragment;
    public int frm = 1;
    InvListViewModel invListViewModel;
    public boolean isReady = false;
    public NfcAdapter nfcAdapter;
    public PendingIntent pendingIntent;
    int power = 0;
    int volume = 0;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityMainBinding inflate = ActivityMainBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());
//        setSupportActionBar(this.binding.toolbar);
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.invListViewModel = new ViewModelProvider(this).get(InvListViewModel.class);
        this.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        init();
        initUI();
    }

    public void init() {
        this.frm = 1;
        PreferenceManager.setStringValue(Constants.TYPE, "dashboard");
        setFragment(new Dashboard(), "Dashboard");
        setInventoryItemCount(getItemCount());
        setInventoryItemBarCount(getItemBarCount());
        this.pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.right_menu, menu);
        this.btItem = menu.findItem(R.id.menu_bt);
        this.btItem.setVisible(/*PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase("1")*/false);
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        int i = this.frm;
        if (i == 2) {
            this.frm = 1;
            PreferenceManager.setStringValue(Constants.TYPE, "dashboard");
            setInventoryItemCount(getItemCount());
            setInventoryItemBarCount(getItemBarCount());
            setFragment(new Dashboard(), "Dashboard");
        } else if (i == 3) {
            this.frm = 2;
            setInventoryItemCount(getItemCount());
            setInventoryItemBarCount(getItemBarCount());
            setFragment(new Dashboard(), "Dashboard");
        } else if (i == 4) {
            this.frm = 2;
            InventoryItems inventoryItems = new InventoryItems();
            Bundle bundle = new Bundle();
            if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("null") || PreferenceManager.getStringValue(Constants.CUR_SC_TYPE) == "") {
                bundle.putString("inv_type", PreferenceManager.getStringValue(Constants.CUR_SC_TYPE));
            }
            inventoryItems.setArguments(bundle);
            setFragment(inventoryItems, "Scan Tag");
        } else if (i == 5) {
           /* this.frm = 2;
            setFragment(new BleScan(), "Scan BLE");*/
        } else if (i == 6) {
         /*  this.frm = 5;
            setFragment(new BleItemHistoryName(), "BLE History List");*/
        } else if (i == 1) {
            setFragment(new Dashboard(), "Dashboard");
        }
    }

    public String getItemCount() {
        return (PreferenceManager.getStringValue(Constants.INV_ID_RFID).equals("null") || PreferenceManager.getStringValue(Constants.INV_ID_RFID).equals("")) ? "0" : Room.databaseBuilder(getApplicationContext(), InvDB.class, "Inventory_db").fallbackToDestructiveMigration().allowMainThreadQueries().build().invItemsDao().getItemCount(PreferenceManager.getStringValue(Constants.INV_ID_RFID)).toString();
    }

    public String getItemBarCount() {
        return (PreferenceManager.getStringValue(Constants.INV_ID_BAR).equals("null") || PreferenceManager.getStringValue(Constants.INV_ID_BAR).equals("")) ? "0" : Room.databaseBuilder(getApplicationContext(), InvDB.class, "Inventory_db").fallbackToDestructiveMigration().allowMainThreadQueries().build().invItemsDao().getItemCount(PreferenceManager.getStringValue(Constants.INV_ID_BAR)).toString();
    }

    public void setInventoryItemCount(String str) {
//        Room.databaseBuilder(getApplicationContext(), InvDB.class, "Inventory_db").fallbackToDestructiveMigration().allowMainThreadQueries().build().invListDao().update(PreferenceManager.getStringValue(Constants.INV_ID_RFID), str);
    }

    public void setInventoryItemBarCount(String str) {
//        Room.databaseBuilder(getApplicationContext(), InvDB.class, "Inventory_db").fallbackToDestructiveMigration().allowMainThreadQueries().build().invListDao().update(PreferenceManager.getStringValue(Constants.INV_ID_BAR), str);
    }

    public int setInventoryName(String str, String str2) {
        return Room.databaseBuilder(getApplicationContext(), InvDB.class, "Inventory_db").fallbackToDestructiveMigration().allowMainThreadQueries().build().invListDao().updateName(str, str2);
    }

    public int getInvId(String str) {
        return Room.databaseBuilder(getApplicationContext(), InvDB.class, "Inventory_db").fallbackToDestructiveMigration().allowMainThreadQueries().build().invListDao().getInvId(str);
    }

    public void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.power_dbm, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.tv_pwr);
        Slider slider = (Slider) inflate.findViewById(R.id.sl_pwr);
        TextView textView2 = (TextView) inflate.findViewById(R.id.bt_setPower);
        if (this.isBTDevice.booleanValue()) {
            if (this.isBtConnect) {
                this.power = mBtReader.getPower();
            } else {
                this.power = 6;
            }
        } else if (this.isC5Device.booleanValue()) {
            this.power = mReader.getPower();
        }
        slider.setValue(Float.parseFloat("" + this.power));
        textView.setText(this.power + " dBm");
        builder.setView(inflate);
        builder.setCancelable(false);
        AlertDialog create = builder.create();
        slider.addOnChangeListener((slider1, f, z) -> createDialog1(textView, slider1, f, z));
        create.show();
        textView2.setOnClickListener(view -> createDialog(create, view));
        inflate.findViewById(R.id.bt_cancel).setOnClickListener(view -> create.dismiss());
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$createDialog$0$com-ruddersoft-rfidscanner-MainActivity  reason: not valid java name */
    public  void createDialog1(TextView textView, Slider slider, float f, boolean z) {
        if (this.isBTDevice.booleanValue()) {
            if (mBtReader.isWorking()) {
                highlightToast("Please Stop Reading First", 2);
                return;
            }
            int i = (int) f;
            this.power = i;
            textView.setText(i + " dBm");
        } else if (!this.isC5Device.booleanValue()) {
        } else {
            if (mReader.isWorking()) {
                highlightToast("Please Stop Reading First", 2);
                return;
            }
            int i2 = (int) f;
            this.power = i2;
            textView.setText(i2 + " dBm");
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$createDialog$1$com-ruddersoft-rfidscanner-MainActivity  reason: not valid java name */
    public  void createDialog(AlertDialog alertDialog, View view) {
        boolean z;
        if (!this.isBTDevice.booleanValue()) {
            z = this.isC5Device.booleanValue() ? mReader.setPower(this.power) : false;
        } else if (this.isBtConnect) {
            z = mBtReader.setPower(this.power);
        } else {
            highlightToast("Kindly Connect bluetooth Device First.", 2);
            alertDialog.dismiss();
            return;
        }
        if (z) {
            highlightToast("Power Set Success.", 1);
        } else {
            highlightToast("Power Set Failure.", 2);
        }
        alertDialog.dismiss();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (this.isBTDevice.booleanValue() && this.isBtConnect) {
            this.power = mBtReader.getPower();
        } else if (this.isC5Device.booleanValue()) {
            this.power = mReader.getPower();
        }
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (menuItem.getItemId() == R.id.menu_bt) {
            if (!PreferenceManager.getStringValue(Constants.BTDEVICE).equals("")) {
                Utils.alert(
                        (Activity) this,
                        (int) R.string.ble_disconnect,
                        getString(R.string.tips_disconnect_the_ble),
                        (int) R.drawable.webtext,
                        (dialogInterface, i) -> disConnect1(dialogInterface, i)
                );
                return true;
            } else if (this.isScanning) {
                showToast("2131951836");
                return true;
            } else {
                Log.e("as_con error", "try new scanning ......");
                if (checkLocationEnable()) {
                    try {
                        Log.e("as_con error", "e.toString()");
                        if (!checkBLEEnable()) {
                            return true;
                        }
                        showBluetoothDevice(false);
                        return true;
                    } catch (Exception e) {
                        Log.e("as_con error", e.toString());
                        return true;
                    }
                } else {
                    Log.e("as_con error", "e.toString()1");
                    return true;
                }
            }
        } else if (menuItem.getItemId() == R.id.menu_power) {
            if (this.isBTDevice.booleanValue()) {
                if (!this.isBtConnect) {
                    highlightToast("Kindly Connect Device First..", 2);
                    return true;
                } else if (mBtReader.isWorking()) {
                    stopReadingDialog();
                    return true;
                } else if (this.power > 0) {
                    createDialog();
                    return true;
                } else {
                    highlightToast("Please Reopen The App", 2);
                    return true;
                }
            } else if (!this.isC5Device.booleanValue()) {
                highlightToast("Kindly Use RFID Device", 2);
                return true;
            } else if (mReader.isWorking()) {
                stopReadingDialog();
                return true;
            } else if (this.power > 0) {
                createDialog();
                return true;
            } else {
                highlightToast("Please Reopen The App", 2);
                return true;
            }
        } else if (menuItem.getItemId() == R.id.menu_volume) {
            volumeDialog();
            return true;
        } else {
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onOptionsItemSelected$3$com-ruddersoft-rfidscanner-MainActivity  reason: not valid java name */
    public /* synthetic */ void disConnect1(DialogInterface dialogInterface, int i) {
        Log.e("as_con", "problem in connect");
        disconnect(true);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        checkBTConnect();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != 1) {
            if (i == 2) {
                if (i2 == -1) {
                    showToast("Bluetooth Has Turned On ");
                } else {
                    showToast("Problem in BT Turning ON ");
                }
            }
        } else if (i2 == -1 && intent != null) {
            if (mBtReader.getConnectStatus() == ConnectionStatus.CONNECTED) {
                disconnect(true);
            }
            String stringExtra = intent.getStringExtra("android.bluetooth.device.extra.DEVICE");
            this.mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(stringExtra);
            connect(stringExtra);
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Fragment fragment2 = this.fragment;
        /*if (fragment2 instanceof NfcReader) {
            try {
                ((NfcReader) fragment2).resolveIntent(intent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (fragment2 instanceof NfcWriter) {
            try {
                ((NfcWriter) fragment2).writtingTag(intent);
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }*/
    }

    @SuppressLint("SetTextI18n")
    private void volumeDialog() {
        this.volume = this.audioManager.getStreamVolume(3);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.volume_set, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.tv_vol);
        Slider slider = (Slider) inflate.findViewById(R.id.sl_vol);
        builder.setView(inflate);
        builder.setCancelable(false);
        AlertDialog create = builder.create();
        slider.setValueTo((float) this.audioManager.getStreamMaxVolume(3));
        slider.setValue((float) this.volume);
        textView.setText(this.volume + " Unit");
        slider.addOnChangeListener((slider1, f, z) -> {
            this.volume = (int) f;
            textView.setText(this.volume + " Unit");
        });
        create.show();
        inflate.findViewById(R.id.bt_setvol).setOnClickListener(view -> {
            setVolume(this.volume);
            create.dismiss();
        });
        inflate.findViewById(R.id.bt_cancel).setOnClickListener(view -> create.dismiss());
    }



    public void hideKeybaord(View view) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setVolume(int i) {
        this.audioManager.setStreamVolume(3, i, 0);
    }

    public void exitDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.bt_e)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialogInterface, i) -> finish())
                .setNegativeButton("No", null)
                .show();    }


    public void setFragment(Fragment fragment2, String str) {
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.frame_layout, fragment2);
        beginTransaction.commit();
    }

    public void setTitle(String str) {
        this.binding.toolbar.setTitle((CharSequence) str);
    }

    public void highlightToast(String str, int i) {
        int i2;
        if (i == 1) {
            playSound(1);
            i2 = R.drawable.toast_blue_style;
        } else {
            playSound(2);
            i2 = R.drawable.toast_red_style;
        }
        View inflate = LayoutInflater.from(this).inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.ll_custom_toast));
        ((LinearLayout) inflate.findViewById(R.id.ll_custom_toast)).setBackgroundResource(i2);
        ((TextView) inflate.findViewById(R.id.toast_text)).setText(str);
        Toast toast = new Toast(this);
        toast.setGravity(17, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(inflate);
        new Handler().postDelayed(toast::show, 100);
        new Handler().postDelayed(() -> toast.cancel(), 2000);
    }
}
