package com.example.vma.ufveventos.controller;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.model.Evento;
import com.example.vma.ufveventos.model.UsuarioSingleton;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by vma on 07/12/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        //Verifica se a mensagem contém notificação
        if (remoteMessage.getNotification() != null || !remoteMessage.getData().isEmpty()){
            //Adiciona à agenda
            com.example.vma.ufveventos.util.Calendar calendar = new com.example.vma.ufveventos.util.Calendar();
            //Verifica se o usuário deseja que a notificação seja adicionada à agenda
            SharedPreferences sharedPref = this.getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0", Context.MODE_PRIVATE);
            String idUsuario = sharedPref.getString("id","default");
            //Se o usuário está logado
            if (!idUsuario.equals("default")) {
                sharedPref = getBaseContext().
                        getSharedPreferences("UFVEVENTOS" + idUsuario, Context.MODE_PRIVATE);
                String agenda = sharedPref.getString("agenda","default");
                //O usuário deseja gravar a notificação na agenda
                if (agenda.equals("true")){
                    //Recupera dados da notificação
                    Map<String,String> dados = remoteMessage.getData();
                    JSONObject dadosJson = null;
                    try {
                        dadosJson = new JSONObject(dados.get("body"));
                    }catch(JSONException e){Log.e("JSON ERRO",e.getMessage());}

                    //Busca locais
                    String local = "";
                    for (int i = 0; i < 3; i++) {
                        if (dadosJson.has("local" + i)) {
                            try {
                                local = local + " " + dadosJson.getString("local" + i);
                            } catch (JSONException e) {
                                Log.e("JSON ERRO", e.getMessage());
                            }
                        }
                    }
                    Evento evento = null;
                    try {
                        evento = new Evento(Integer.parseInt(dadosJson.getString("id")), dadosJson.getString("denominacao"),
                                dadosJson.getString("horainicio"), dadosJson.getString("horafim"),
                                dadosJson.getString("datainicio"), dadosJson.getString("datafim"),
                                dadosJson.getString("descricao"), "",Integer.parseInt(dadosJson.getString("participantes")),
                                "", null, null, null);
                    }catch(JSONException e){Log.e("JSON ERRO",e.getMessage());}
                    calendar.addEventNotification(evento,local,getBaseContext(),getContentResolver());
                }
            }

            /*Cancela notificação já agendada, a fim de impedir que o app
            inunde o celular de notificações de novos eventos*/
            Context ctx = getApplicationContext();
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent cancelServiceIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            PendingIntent cancelServicePendingIntent = PendingIntent.getBroadcast(
                    ctx,
                    100, // integer constant used to identify the service
                    cancelServiceIntent,
                    0 //no FLAG needed for a service cancel
            );
            alarmManager.cancel(cancelServicePendingIntent);

            //Agenda nova notificação
            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");
            PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, 5);
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
        }
    }
}