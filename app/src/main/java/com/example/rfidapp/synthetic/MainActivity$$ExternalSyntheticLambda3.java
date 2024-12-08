package com.example.rfidapp.synthetic;

import android.widget.TextView;

import com.example.rfidapp.activity.MainActivity;
import com.google.android.material.slider.Slider;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class MainActivity$$ExternalSyntheticLambda3 implements Slider.OnChangeListener {
    public final /* synthetic */ MainActivity f$0;
    public final /* synthetic */ TextView f$1;

    public /* synthetic */ MainActivity$$ExternalSyntheticLambda3(MainActivity mainActivity, TextView textView) {
        this.f$0 = mainActivity;
        this.f$1 = textView;
    }

    public final void onValueChange(Slider slider, float f, boolean z) {
        this.f$0.m519lambda$volumeDialog$4$comruddersoftrfidscannerMainActivity(this.f$1, slider, f, z);
    }
}
