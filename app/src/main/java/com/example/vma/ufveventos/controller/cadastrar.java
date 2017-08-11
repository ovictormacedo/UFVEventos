package com.example.vma.ufveventos.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.vma.ufveventos.R;

public class cadastrar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);
    }

    public void faca_login(View view) {
        finish();
    }
    public void cadastrar(View view){
        Toast.makeText(this,"Cadastrado",Toast.LENGTH_SHORT).show();
    }
}
