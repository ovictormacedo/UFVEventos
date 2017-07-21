package com.example.vma.ufveventos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.vma.ufveventos.model.Categoria;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void entrar(View view){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://meettest.esy.es/API/api.php/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Categoria service = retrofit.create(Categoria.class);
    }
}
