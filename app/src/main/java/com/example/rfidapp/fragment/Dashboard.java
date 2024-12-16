package com.example.rfidapp.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.rfidapp.R;
import com.example.rfidapp.ReaderClass;
import com.example.rfidapp.activity.MainActivity;
import com.example.rfidapp.adapter.SliderAdapter;
import com.example.rfidapp.databinding.FragmentDashboardBinding;
import com.example.rfidapp.model.SliderItems;
import com.example.rfidapp.util.PreferenceManager;
import com.example.rfidapp.util.Util;
import com.example.rfidapp.util.WebViewUtil;
import com.example.rfidapp.util.constants.Constants;
import com.rscja.deviceapi.interfaces.KeyEventCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class Dashboard extends KeyDownFragment implements SliderAdapter.OnItemClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 100;
    FragmentDashboardBinding binding;
    MainActivity mContext;
    private String mParam1;
    private String mParam2;
    public Handler sliderHandler = new Handler();
    public final Runnable sliderRunnable = new Runnable() {
        public void run() {
            Dashboard.this.binding.viewPagerImageSlider.setCurrentItem(Dashboard.this.binding.viewPagerImageSlider.getCurrentItem() + 1);
        }
    };

    public static Dashboard newInstance(String str, String str2) {
        Dashboard dashboard = new Dashboard();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        dashboard.setArguments(bundle);
        return dashboard;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = FragmentDashboardBinding.inflate(layoutInflater, viewGroup, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        this.mContext = mainActivity;
        mainActivity.currentFrag = this;
        return this.binding.getRoot();
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        init();

        binding.detectedDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*if (binding.webView.getVisibility() == View.VISIBLE) {
                            String str = "Android data transfer";
                            binding.webView.loadUrl("javascript:xxx('" + str + "')");
                        }*/
                    }
                });
            }
        });
    }

    public void onMyKeyDown() {
        super.onMyKeyDown();
    }

    public void init() {
        if (PreferenceManager.getStringValue(Constants.TYPE) == "rfid") {
            this.mContext.setTitle("UHF RFID");
            ((ActionBar) Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
            this.binding.llOptions.setVisibility(View.GONE);
            this.binding.llQrcode.setVisibility(View.GONE);
            this.binding.llRfid.setVisibility(View.VISIBLE);
        }
        this.binding.detectedDevice.setText("Detected Device: " + Build.MODEL + " (" + Build.MANUFACTURER + ")");
        this.binding.cardRfid.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                /*if (!PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase("1")) {
                    PreferenceManager.setStringValue(Constants.TYPE, "rfid");
                    Dashboard.this.mContext.frm = 2;
                    Dashboard.this.mContext.setTitle("UHF RFID");
                    ((ActionBar) Objects.requireNonNull(((AppCompatActivity) Dashboard.this.requireActivity()).getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
                    Dashboard.this.binding.llOptions.setVisibility(android.view.View.GONE);
                    Dashboard.this.binding.llQrcode.setVisibility(android.view.View.GONE);
                    Dashboard.this.binding.llRfid.setVisibility(android.view.View.VISIBLE);
                } else if (Dashboard.this.mContext.isBtConnect) {
                    PreferenceManager.setStringValue(Constants.TYPE, "rfid");
                    Dashboard.this.mContext.frm = 2;
                    Dashboard.this.mContext.setTitle("UHF RFID");
                    ((ActionBar) Objects.requireNonNull(((AppCompatActivity) Dashboard.this.requireActivity()).getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
                    Dashboard.this.binding.llOptions.setVisibility(android.view.View.GONE);
                    Dashboard.this.binding.llQrcode.setVisibility(android.view.View.GONE);
                    Dashboard.this.binding.llRfid.setVisibility(android.view.View.VISIBLE);
                } else {
                    new AlertDialog.Builder(Dashboard.this.mContext).setMessage((CharSequence) "Please connect UHF device via bluetooth to use this functionality.").setTitle((CharSequence) "Alert").setPositiveButton((CharSequence) "Ok", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
                }*/

                PreferenceManager.setStringValue(Constants.TYPE, "rfid");
                Dashboard.this.mContext.frm = 2;
                Dashboard.this.mContext.setTitle("UHF RFID");
                ((ActionBar) Objects.requireNonNull(((AppCompatActivity) Dashboard.this.requireActivity()).getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
                Dashboard.this.binding.llOptions.setVisibility(android.view.View.GONE);
                Dashboard.this.binding.llQrcode.setVisibility(android.view.View.GONE);
                Dashboard.this.binding.llRfid.setVisibility(android.view.View.VISIBLE);
            }
        });
        this.binding.cardNfc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NfcAdapter defaultAdapter = ((NfcManager) Dashboard.this.mContext.getSystemService(android.content.Context.NFC_SERVICE)).getDefaultAdapter();
                if (defaultAdapter != null && defaultAdapter.isEnabled()) {
                    PreferenceManager.setStringValue(Constants.TYPE, "nfc");
                    Dashboard.this.mContext.setFragment(new NfcTabLayoutFragment(), "NFC Reader");
                    Dashboard.this.mContext.frm = 2;
                } else if (defaultAdapter == null || defaultAdapter.isEnabled()) {
                    new AlertDialog.Builder(Dashboard.this.mContext).setMessage((CharSequence) "NFC is not available in your device.").setTitle((CharSequence) "Alert").setPositiveButton((CharSequence) "Ok", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
                } else {
                    new AlertDialog.Builder(Dashboard.this.mContext).setMessage((CharSequence) "Please enable NFC from device settings.").setTitle((CharSequence) "Alert").setPositiveButton((CharSequence) "Ok", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
                }
            }
        });
        this.binding.cardBle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dashboard.this.mContext.setFragment(new BleScan(), "Scan BLE");
                Dashboard.this.mContext.frm = 2;
            }
        });
        this.binding.cardQr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dashboard.this.mContext.frm = 2;
                Dashboard.this.mContext.setTitle("QR CODE");
                ((ActionBar) Objects.requireNonNull(((AppCompatActivity) Dashboard.this.requireActivity()).getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
                Dashboard.this.binding.llOptions.setVisibility(View.GONE);
                Dashboard.this.binding.llRfid.setVisibility(android.view.View.GONE);
                Dashboard.this.binding.llQrcode.setVisibility(android.view.View.VISIBLE);
            }
        });
        this.binding.btContactUs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dashboard.this.mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.ruddersoft.com/contact-us")));
            }
        });
        if (ContextCompat.checkSelfPermission(this.mContext, "android.permission.READ_EXTERNAL_STORAGE") == -1) {
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 100);
        }
        File[] listFiles = new File("/sdcard/Documents").listFiles();
        Log.d("Files", "Size: " + listFiles.length);
        for (int i = 0; i < listFiles.length; i++) {
            Log.d("Files", "FileName:" + listFiles[i].getName());
        }
        if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equals("1") && ReaderClass.mBtReader != null) {
            ReaderClass.mBtReader.setKeyEventCallback(new KeyEventCallback() {
                public void onKeyDown(int i) {
                }

                public void onKeyUp(int i) {
                }
            });
        }
        this.mContext.setTitle("Dashboard");
        initSlider();
        this.binding.llTagCount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PreferenceManager.setStringValue(Constants.CUR_SC_TYPE, "Rfid");
                Dashboard.this.mContext.frm = 3;
                Dashboard.this.mContext.setFragment(new InventoryItems(), "Scan Tag");
            }
        });
        this.binding.llWrite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dashboard.this.mContext.setFragment(new WriteTag(), "Write Tag");
                Dashboard.this.mContext.frm = 3;
            }
        });
        this.binding.llSingleSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dashboard.this.mContext.setFragment(new SingleSearch(), "Single Search");
                Dashboard.this.mContext.frm = 3;
            }
        });
        this.binding.llInvList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dashboard.this.mContext.frm = 3;
                Dashboard.this.mContext.setFragment(new InventoryList(), "Inventory List");
            }
        });
        this.binding.llSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dashboard.this.mContext.setFragment(new AppSettings(), "Settings");
                Dashboard.this.mContext.frm = 3;
            }
        });
        this.binding.llScanNfc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dashboard.this.mContext.setFragment(new NfcReader(), "NFC Reader");
                Dashboard.this.mContext.frm = 2;
            }
        });
        this.binding.llWriteNfc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dashboard.this.mContext.setFragment(new NfcReader(), "NFC Writer");
                Dashboard.this.mContext.frm = 2;
            }
        });
        this.binding.llInvqrList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dashboard.this.mContext.frm = 3;
                Dashboard.this.mContext.setFragment(new InventoryList(), "Inventory List");
            }
        });
        this.binding.llQrSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dashboard.this.mContext.setFragment(new AppSettings(), "Settings");
                Dashboard.this.mContext.frm = 2;
            }
        });
        this.binding.llScanBarcode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PreferenceManager.setStringValue(Constants.CUR_SC_TYPE, "Barcode");
                Dashboard.this.mContext.frm = 2;
                Dashboard.this.mContext.setFragment(new InventoryItems(), "Tag Count");
            }
        });
    }

    private void initSlider() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new SliderItems(R.drawable.image1, "Jwl"));
        arrayList.add(new SliderItems(R.drawable.image2, "RsLens"));
        arrayList.add(new SliderItems(R.drawable.image3, "Visitor"));
        this.binding.viewPagerImageSlider.setAdapter(new SliderAdapter(arrayList, this.binding.viewPagerImageSlider, this));
        this.binding.viewPagerImageSlider.setPadding(100, 0, 100, 0);
        this.binding.viewPagerImageSlider.setClipToPadding(false);
        this.binding.viewPagerImageSlider.setClipChildren(false);
        this.binding.viewPagerImageSlider.setOffscreenPageLimit(3);
        this.binding.viewPagerImageSlider.getChildAt(0).setOverScrollMode(2);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            public void transformPage(View view, float f) {
                view.setScaleY(((1.0f - Math.abs(f)) * 0.15f) + 0.85f);
            }
        });
        this.binding.viewPagerImageSlider.setPageTransformer(compositePageTransformer);
        this.binding.viewPagerImageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            public void onPageSelected(int i) {
                super.onPageSelected(i);
                Dashboard.this.sliderHandler.removeCallbacks(Dashboard.this.sliderRunnable);
                Dashboard.this.sliderHandler.postDelayed(Dashboard.this.sliderRunnable, 2000);
            }
        });
    }

    public void onItemClick(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1838807297:
                if (str.equals("RsLens")) {
                    c = 0;
                    break;
                }
                break;
            case 74911:
                if (str.equals("Jwl")) {
                    c = 1;
                    break;
                }
                break;
            case 2131414094:
                if (str.equals("Visitor")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                Util.playStoreFromSlider("com.ruddersoft.rslens", this.mContext);
                return;
            case 1:
                Util.playStoreFromSlider("com.ruddersoft.jewelleryapp", this.mContext);
                return;
            case 2:
                Util.playStoreFromSlider("com.ruddersoft.visitorfirst", this.mContext);
                return;
            default:
                return;
        }
    }

    public void onResume() {
        if (PreferenceManager.getStringValue(Constants.TYPE).equalsIgnoreCase("rfid")) {
            this.mContext.setTitle("UHF RFID");
            this.mContext.frm = 2;
            ((ActionBar) Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
            this.binding.llOptions.setVisibility(View.GONE);
            this.binding.llQrcode.setVisibility(android.view.View.GONE);
            this.binding.llRfid.setVisibility(android.view.View.VISIBLE);
        } else {
            this.mContext.setTitle("Dashboard");
            this.mContext.frm = 1;
            ((ActionBar) Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())).setDisplayHomeAsUpEnabled(false);
            this.binding.llQrcode.setVisibility(View.GONE);
            this.binding.llRfid.setVisibility(View.GONE);
            this.binding.llOptions.setVisibility(android.view.View.VISIBLE);
        }
        super.onResume();
    }

    public void onPause() {
        ((ActionBar) Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        super.onPause();
    }
}
