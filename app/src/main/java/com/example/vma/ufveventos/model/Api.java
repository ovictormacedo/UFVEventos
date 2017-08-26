package com.example.vma.ufveventos.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import rx.Observable;

public interface Api{
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("evento/indexinicial/{offset}/indexfinal/{limit}")
    Observable<List<Evento>> getEventos(@Path("offset") int offset, @Path("limit") int limit);

    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("evento/categoria/{idCategoria}/indexinicial/{offset}/indexfinal/{limit}")
    Observable<List<Evento>> getEventosPorCategoria(@Path("idCategoria") int idCategoria,
                                                    @Path("offset") int offset, @Path("limit") int limit);

    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("evento/categoria/personalizado/idusuario/{idUsuario}/indexinicial/{offset}/indexfinal/{limit}")
    Observable<List<Evento>> getEventosPorUsuario(@Path("idUsuario") int idUsuario,
                                                  @Path("offset") int offset, @Path("limit") int limit);

    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("usuario/{idUsuario}")
    //A senha deve ser passada utilizando MD5
    Observable<Usuario> getUsuario(@Path("idUsuario") int idUsuario, @Header("X-UserPassword") String password);

    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("preferencias_notificacoes/{idUsuario}")
    Observable<Usuario> getPreferenciasDeNotificacoes(@Path("idUsuario") int idUsuario);

    //TESTADO
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("categoria")
    Observable<List<Categoria>> getCategorias();

    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("dispositivos/{idUsuario}")
    Observable<Usuario> getDispositivos(@Path("idUsuario") int idUsuario);
}