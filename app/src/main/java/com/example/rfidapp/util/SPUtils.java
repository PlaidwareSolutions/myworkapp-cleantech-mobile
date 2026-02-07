package com.example.rfidapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {
    public static final String AUTO_RECONNECT = "autoReconnect";
    public static final String DISCONNECT_TIME = "disconnectTime";
    private static SPUtils mInstance;
    private static SharedPreferences sp;

    private SPUtils(Context context) {
        sp = getSP(context);
    }

    public static SPUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SPUtils.class) {
                if (mInstance == null) {
                    mInstance = new SPUtils(context);
                }
            }
        }
        return mInstance;
    }

    private static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences("config", 0);
    }

    public boolean getSPBoolean(String str, boolean z) {
        return sp.getBoolean(str, z);
    }

    public long getSPLong(String str, long j) {
        return sp.getLong(str, j);
    }
}
