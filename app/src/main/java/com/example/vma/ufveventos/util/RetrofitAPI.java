package com.example.vma.ufveventos.util;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vma on 07/09/2017.
 */

class RetrofitAPI {
    public static final RetrofitAPI ourInstance = new RetrofitAPI();

    Retrofit retrofit;
    String BASE_URL = "";

    static RetrofitAPI getInstance() {
        return ourInstance;
    }

    public RetrofitAPI() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }
}
