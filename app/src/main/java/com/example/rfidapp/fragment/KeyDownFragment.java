package com.example.rfidapp.fragment;

import android.util.Log;

import androidx.fragment.app.Fragment;

public abstract class KeyDownFragment extends Fragment {
    public void onMyKeyDown() {
        Log.e("keyDown", "key");
    }
}
