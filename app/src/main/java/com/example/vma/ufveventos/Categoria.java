package com.example.vma.ufveventos;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Categoria{
    @GET("categoria")
    Call<List<String>> listRepos();
}