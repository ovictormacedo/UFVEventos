package com.example.vma.ufveventos.model;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Api{
    @GET("evento/indexinicial/{offset}/indexfinal/{limit}")
    Call<List<Evento>> getEventos(@Path("offset") int offset, @Path("limit") int limit);

}