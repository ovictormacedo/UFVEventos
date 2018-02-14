package com.labd2m.vma.ufveventos.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.labd2m.vma.ufveventos.model.Evento;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
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
            com.labd2m.vma.ufveventos.util.Calendar calendar = new com.labd2m.vma.ufveventos.util.Calendar();
            //Verifica se o usuário deseja que a notificação seja adicionada à agenda
            SharedPreferences sharedPref = this.getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0", Context.MODE_PRIVATE);
            String idUsuario = sharedPref.getString("id","default");
            Log.i("ADD EVENTO","PREPARANDO PRA ADD");
            //Se o usuário está logado
            if (!idUsuario.equals("default")) {
                Log.i("ADD EVENTO","LOGADO");
                String agenda = sharedPref.getString("agenda","default");
                //O usuário deseja gravar a notificação na agenda
                if (agenda.equals("1")){
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
                    Log.i("ADD EVENTO",evento.getDenominacao());
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