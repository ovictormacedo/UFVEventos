package com.example.vma.ufveventos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

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

        Call<List<String>> repos = service.listRepos();
        try {
            List<String> aux = repos.execute();
        }catch (java.io.IOException e){Toast.makeText(this,"ERRO:"+e.getMessage(),Toast.LENGTH_SHORT).show();}

        Toast.makeText(this,repos.toString(),Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"Clicou",Toast.LENGTH_SHORT).show();
    }
}
