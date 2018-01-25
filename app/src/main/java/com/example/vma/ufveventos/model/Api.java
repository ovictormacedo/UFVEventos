package com.example.vma.ufveventos.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface Api{
    //Retorna dados do evento
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("evento/{idEvento}")
    Observable<Evento> getEvento(@Path("idEvento") int idEvento);

    //Testado
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("evento/indexinicial/{offset}/indexfinal/{limit}")
    Observable<List<Evento>> getEventos(@Path("offset") int offset, @Path("limit") int limit);

    //Não testado
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("evento/categoria/{idCategoria}/indexinicial/{offset}/indexfinal/{limit}")
    Observable<List<Evento>> getEventosPorCategoria(@Path("idCategoria") String idCategoria,
                                                    @Path("offset") int offset, @Path("limit") int limit);
    //Não testado
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("evento/categoria/personalizado/idusuario/{idUsuario}/indexinicial/{offset}/indexfinal/{limit}")
    Observable<List<Evento>> getEventosPorUsuario(@Path("idUsuario") String idUsuario,
                                                  @Path("offset") int offset, @Path("limit") int limit);
    //Não testado
    //a senha deve ser passada utilizando MD5
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("usuario/{idUsuario}")
    Observable<String> getUsuario(@Header("a") String password, @Path("idUsuario") String idUsuario);

    //Retorna categorias de evento que o usuário deseja receber notificacoes
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("preferencias_notificacoes/{idUsuario}")
    Observable<List<Categoria>> getPreferenciasDeNotificacoes(@Path("idUsuario") String idUsuario);

    //Retorna categorias de evento preferidas do usuário
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("preferencias_categorias/{idUsuario}")
    Observable<List<Categoria>> getPreferenciasDeCategorias(@Path("idUsuario") String idUsuario);

    //Retorna categorias de evento
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("categoria")
    Observable<List<Categoria>> getCategorias();

    //Não testado
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @GET("dispositivos/{idUsuario}")
    Observable<Usuario> getDispositivos(@Path("idUsuario") String idUsuario);

    //Recupera senha
    @FormUrlEncoded
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @POST("email")
    Observable<Void> recuperaSenha(@Field("data") JSONObject data);

    //Autentica usuário
    @FormUrlEncoded
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @POST("usuario_auth")
    Observable<Usuario> authUsuario(@Field("data") JSONObject data);

    //Cria novo usuário
    @FormUrlEncoded
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @POST("usuario")
    Observable<Integer> setUsuario(@Field("data") JSONObject data);

    //Cria novo usuário google
    @FormUrlEncoded
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @POST("usuario_google")
    Observable<Integer> setUsuarioGoogle(@Field("data") JSONObject data);

    //Cadastra novo dispositivo do usuário
    @FormUrlEncoded
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @POST("dispositivos")
    Observable<Void> setDispositivo(@Field("data") JSONObject data);

    @FormUrlEncoded
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @POST("agenda_notificacoes")
    Observable<Dispositivo> getAgendaNotificacoes(@Field("data") JSONObject data);

    @FormUrlEncoded
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @POST("agenda")
    Observable<Void> setAgenda(@Field("data") JSONObject data);

    @FormUrlEncoded
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @POST("notificacoes")
    Observable<Void> setNotificacoes(@Field("data") JSONObject data);

    //Atualiza dados do cadastro do usuário
    @FormUrlEncoded
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @PUT("usuario/{idUsuario}")
    Observable<Void> updateUsuario(@Field("data") JSONObject data,@Path("idUsuario") String idUsuario);

    //Atualiza a opção do usuário receber notificções
    @FormUrlEncoded
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @PUT("notificacoes/{idUsuario}/dispositivo/{token}")
    Observable<Void> updateNotificacoes(@Field("data") JSONObject data,@Path("idUsuario") String idUsuario,@Path("token") String token);

    //Atualiza a opção do usuário adicionar eventos que chegam via notificação automaticamente à agenda
    @FormUrlEncoded
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @PUT("agenda/{idUsuario}/dispositivo/{token}")
    Observable<Void> updateAgenda(@Field("data") JSONObject data,@Path("idUsuario") String idUsuario,@Path("token") String token);

    //Atualiza preferências de categoria do usuário
    @FormUrlEncoded
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @PUT("preferencias_categorias/{idUsuario}")
    Observable<Void> updatePreferenciasCategorias(@Field("data") String data,@Path("idUsuario") String idUsuario);

    //Atualiza preferências de categoria do usuário
    @FormUrlEncoded
    @Headers("Authorization:Basic 45dfd94be4b30d5844d2bcca2d997db0")
    @PUT("preferencias_notificacoes/{idUsuario}")
    Observable<Void> updatePreferenciasNotificacoes(@Field("data") String data,@Path("idUsuario") String idUsuario);

    @GET("maps/api/directions/json")
    Call<Object> getRota(
            @Query("origin") String srcParam,
            @Query("destination") String destParam,
            @Query("sensor") String sensor,
            @Query("units") String units,
            @Query("driving") String mode,
            @Query("key") String key
    );
}