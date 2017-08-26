package com.example.vma.ufveventos.controller;


import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vma on 26/08/2017.
 */

public class RetrofitAPI {
    public Retrofit retrofit(){
        Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("http://meettest.esy.es/API/api.php/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit;
    }
}
