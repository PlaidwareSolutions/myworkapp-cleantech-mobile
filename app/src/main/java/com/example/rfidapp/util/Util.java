package com.example.rfidapp.util;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.example.rfidapp.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Util {
    Context context;
    ProgressDialog progressDialog;

    public Util(Context context2) {
        this.context = context2;
        this.progressDialog = new ProgressDialog(context2);
    }

    public static void showToast(Context context2, String str) {
        Toast.makeText(context2, str, Toast.LENGTH_SHORT).show();
    }

    public static void makeDirectry() {
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/Solar App");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static String getDateTime() {
        return new SimpleDateFormat("dd-MM-yyyy hh:mm aaa", Locale.getDefault()).format(Calendar.getInstance().getTime());
    }

    public static String getDate() {
        return new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
    }

    public static String getTime() {
        return new SimpleDateFormat("hh:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static void openDocs(Context context2, File file, String str) {
        Uri uriForFile = FileProvider.getUriForFile(context2, context2.getPackageName() + ".provider", file);
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(uriForFile, str);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context2.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(context2, "No app Found", Toast.LENGTH_SHORT).show();
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

    public static void playStoreFromSlider(String str, Context context2) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + str));
            context2.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(context2, "Play store not found", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    public static void showfreeDialog(final Context context2) {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(context2);
        View inflate = LayoutInflater.from(context2).inflate(R.layout.dialog_free_limit, (ViewGroup) null);
        builder.setView(inflate);
        final AlertDialog create = builder.create();
        ((TextView) inflate.findViewById(R.id.tv_free_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.this.cancel();
            }
        });
        ((Button) inflate.findViewById(R.id.bt_contact_us)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                context2.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.ruddersoft.com/contact-us")));
            }
        });
        ((Button) inflate.findViewById(R.id.bt_email_us)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("plan/text");
                intent.putExtra("android.intent.extra.EMAIL", new String[]{"sales@ruddersoft.com"});
                context2.startActivity(Intent.createChooser(intent, ""));
            }
        });
        create.show();*/
    }

    public static void showCustDialog(final Context context2) {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(context2);
        View inflate = LayoutInflater.from(context2).inflate(R.layout.dialog_cusomize, (ViewGroup) null);
        builder.setView(inflate);
        final AlertDialog create = builder.create();
        ((TextView) inflate.findViewById(R.id.tv_cust_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.this.cancel();
            }
        });
        ((Button) inflate.findViewById(R.id.bt_contact_us)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                context2.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.ruddersoft.com/contact-us")));
            }
        });
        create.show();*/
    }
}
