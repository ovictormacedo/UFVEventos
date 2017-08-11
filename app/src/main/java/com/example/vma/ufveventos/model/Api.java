package com.example.vma.ufveventos.model;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface Api{
    @Headers({
            "Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0"
    })
    @GET("evento/indexinicial/{offset}/indexfinal/{limit}")
    Call<List<Evento>> getEventos(@Path("offset") int offset, @Path("limit") int limit);

    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("categoria")
    Call<Object> getCategorias();
}