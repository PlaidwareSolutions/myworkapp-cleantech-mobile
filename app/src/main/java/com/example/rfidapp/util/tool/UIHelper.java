package com.example.rfidapp.util.tool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

public class UIHelper {
    public static void ToastMessage(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context context, int i) {
        Toast.makeText(context, i, android.widget.Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context context, String str, int i) {
        Toast.makeText(context, str, i).show();
    }

    public static void alert(Activity activity, int i, int i2, int i3) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(i);
            builder.setMessage(i2);
            builder.setIcon(i3);
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void alert(Activity activity, int i, String str, int i2) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(i);
            builder.setMessage(str);
            builder.setIcon(i2);
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
