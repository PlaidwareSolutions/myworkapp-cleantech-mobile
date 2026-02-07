package com.example.rfidapp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import com.example.rfidapp.R;

import java.util.HashMap;

public class Utils {
    private static final String TAG = "Utils";
    private static AudioManager am;
    private static final HashMap<Integer, Integer> soundMap = new HashMap<>();
    private static SoundPool soundPool;
    public static String[] storage_permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    public static String[] storage_permissions_33 = {"android.permission.READ_MEDIA_IMAGES"};
    private static float volumnRatio;
    Context context;
    ProgressDialog progressDialog;

    public Utils(Context context2) {
        this.context = context2;
        this.progressDialog = new ProgressDialog(context2);
    }

    public static void alert(Activity activity, int i, String str, int i2, DialogInterface.OnClickListener onClickListener) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(i);
            builder.setMessage(str);
            builder.setIcon(i2);
            builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            if (onClickListener != null) {
                builder.setPositiveButton(R.string.ok, onClickListener);
            }
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void alert(Activity activity, String str, View view, int i, DialogInterface.OnClickListener onClickListener) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(str);
            builder.setView(view);
            builder.setIcon(i);
            builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            if (onClickListener != null) {
                builder.setPositiveButton(R.string.ok, onClickListener);
            }
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgressBar(String str) {
        this.progressDialog.setMessage(str);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setProgressStyle(0);
        this.progressDialog.show();
    }

    public void hideProgressBar() {
        ProgressDialog progressDialog2 = this.progressDialog;
        if (progressDialog2 != null && progressDialog2.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    public static void openPlayStore(Context context2) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + context2.getPackageName()));
            context2.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(context2, "Play store not found", Toast.LENGTH_SHORT).show();
        }
    }

    public static void openPlayStore(Context context2, String str) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=" + str));
            context2.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(context2, "Play store not found", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareText(Context context2, String str) {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            /*intent.setType(HTTP.PLAIN_TEXT_TYPE);*/
            intent.putExtra("android.intent.extra.TEXT", str);
            context2.startActivity(Intent.createChooser(intent, "send"));
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(context2, "No app found", Toast.LENGTH_SHORT).show();
        }
    }

    public static String[] permissions() {
        if (Build.VERSION.SDK_INT >= 33) {
            return storage_permissions_33;
        }
        return storage_permissions;
    }
}
