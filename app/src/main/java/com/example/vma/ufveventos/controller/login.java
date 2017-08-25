package com.example.vma.ufveventos.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.model.Api;

import java.io.ObjectStreamException;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class login extends AppCompatActivity {
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
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);

        Observable<Object> observable =  api.getCategorias();

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>(){
                    @Override
                    public void onCompleted(){

                    }

                    @Override
                    public void onError(Throwable e){

                    }

                    @Override
                    public void onNext(Object response){
                        Toast.makeText(getBaseContext(),response.toString(),Toast.LENGTH_SHORT).show();
                    }
                });

        Toast.makeText(getBaseContext(),"Clicou",Toast.LENGTH_SHORT).show();

        /*
        Call<Object> call = api.getCategorias();

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                List<Categoria> categoria = new ArrayList<Categoria>();
                //Verifica se a resposta é uma lista de categorias
                if (response.body() instanceof ArrayList) {
                    //Copia o response para a lista de categorias
                    Gson gson = new Gson();
                    for (int i = 0; i < ((ArrayList) response.body()).size(); i++)
                        categoria.add(gson.fromJson(((ArrayList) response.body()).get(i).toString(), Categoria.class));

                    Log.i("Retrofit error",""+categoria.get(1).getNome());
                }
                else{
                    String aux = "TESTE";
                    if (!response.isSuccessful())
                        aux = "TESTE_ERRO";
                    Log.i("Retrofit error",""+response.code()+aux);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("Retrofit error","ERRO:"+t.getMessage());
            }
        });
        */
    }
}

