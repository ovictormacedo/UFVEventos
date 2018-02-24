package com.labd2m.vma.ufveventos.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.labd2m.vma.ufveventos.R;
import com.labd2m.vma.ufveventos.util.Permission;

/**
 * Created by vma on 28/01/2018.
 */

public class splash_screen extends Activity {
    // Timer da splash screen
    private static int SPLASH_TIME_OUT = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Log.i("DEBUG-----","ANTES DO HANDLE");

        new Handler().postDelayed(new Runnable() {
            /*
             * Exibindo splash com um timer.
             */
            @Override
            public void run() {
                Log.i("DEBUG-----","DENTRO DO RUN,ANTES DAS PERMISSÕES");

                //Permission permission = new Permission();
                //permission.requestPermissionMaps(getParent(),getBaseContext());

                Log.i("DEBUG-----","VAI CHAMAR LOGIN TELA");

                Intent i = new Intent(splash_screen.this, login.class);
                startActivity(i);

                Log.i("DEBUG-----","CHAMOU LOGIN TELA");
                // Fecha esta activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
