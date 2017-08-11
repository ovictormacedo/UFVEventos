package com.example.vma.ufveventos.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.model.Api;
import com.example.vma.ufveventos.model.Categoria;
import com.example.vma.ufveventos.model.Evento;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class login extends AppCompatActivity {
    private Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void cadastrar(View view){
        Intent it = new Intent(this,cadastrar.class);
        startActivity(it);
    }

    public void entrar(View view){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://meettest.esy.es/API/api.php/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Api api = retrofit.create(Api.class);
        Toast.makeText(getBaseContext(),"Clicou",Toast.LENGTH_SHORT).show();
        Call<Object> call = api.getCategorias();

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                List<Categoria> categoria = new ArrayList<Categoria>();
                //Verifica se a resposta é uma lista de categorias
                if (response.body() instanceof ArrayList) {
                    //Copia o response para a lista de categorias
                    //for (int i = 0; i < response.body().size(); i++)
                    //    categoria.add((Categoria) response.body().get(i));
                    //categoria = (ArrayList<Categoria>) response.body();

                    Gson gson = new Gson();
                    for (int i = 0; i < ((ArrayList) response.body()).size(); i++)
                        categoria.add(gson.fromJson(((ArrayList) response.body()).get(i).toString(), Categoria.class));

                    Log.i("Retrofit error",""+categoria.get(1).getNome());
                }
                else{
                    //A resposta é um número de erro
                    double aux = (double) response.body();
                    int erro = (int) aux;
                    Log.i("Retrofit error",""+erro);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("Retrofit error",t.getMessage());
            }
        });
    }
}

