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

import java.io.ObjectStreamException;
import java.util.List;

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
        //Cria objeto para acessar a API de dados Siseventos
        RetrofitAPI retrofit = new RetrofitAPI();
        Api api = retrofit.retrofit().create(Api.class);

        //Chama método
        Observable<List<Categoria>> observable =  api.getCategorias();

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Categoria>>(){
                    @Override
                    public void onCompleted(){}

                    @Override
                    public void onError(Throwable e){
                        Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<Categoria> response){
                        Toast.makeText(getBaseContext(),response.get(0).getNome(),Toast.LENGTH_SHORT).show();
                    }
                });

        Toast.makeText(getBaseContext(),"Clicou",Toast.LENGTH_SHORT).show();
    }
}

