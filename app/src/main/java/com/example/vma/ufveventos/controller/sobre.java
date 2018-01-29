package com.example.vma.ufveventos.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.vma.ufveventos.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class sobre extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        //Google Analytics
        MyApplication application = (MyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("sobre");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
