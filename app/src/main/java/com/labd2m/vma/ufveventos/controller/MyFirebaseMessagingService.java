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
        Log.i("NOTIFICATION","Chegou notificação");
        //Verifica se a mensagem contém notificação
        if (remoteMessage.getNotification() != null || !remoteMessage.getData().isEmpty()){
            //Recupera dados da notificação
            String tipo = "";
            Map<String,String> dados = remoteMessage.getData();
            JSONObject dadosJson = null;
            try {
                dadosJson = new JSONObject(dados.get("body"));
                tipo = dadosJson.getString("tipo");
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

            com.labd2m.vma.ufveventos.util.Calendar calendar = new com.labd2m.vma.ufveventos.util.Calendar();
            //Verifica se o usuário deseja que a notificação seja adicionada à agenda
            SharedPreferences sharedPref = this.getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0", Context.MODE_PRIVATE);
            String idUsuario = sharedPref.getString("id","default");
            //Se o usuário está logado
            if (!idUsuario.equals("default")) {
                Log.i("ADD EVENTO","LOGADO");
                String agenda = sharedPref.getString("agenda","default");
                //O usuário deseja gravar a notificação na agenda
                if (agenda.equals("1")){
                    Log.i("ADD EVENTO",evento.getDenominacao());
                    calendar.addEventNotification(evento,local,getBaseContext(),getContentResolver());
                }
            }

            //Verifica se o usuário deseja receber notificacoes
            String notificacoes = sharedPref.getString("notificacoes","1");
            Log.i("NOTIFICATION",notificacoes);
            Log.i("NOTIFICATION",tipo);
            if (notificacoes.equals("1") && tipo.equals("1")) { //tipo = deferido
                Log.i("NOTIFICATION","Envia notificação");
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
            }else
                if (notificacoes.equals("1") && tipo.equals("3")) { //tipo = cancelado
                    //Agenda nova notificação
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                    notificationIntent.putExtra("tipo",tipo);
                    notificationIntent.putExtra("denominacao",evento.getDenominacao());
                    notificationIntent.putExtra("horainicio",evento.getHoraInicio());
                    notificationIntent.putExtra("horafim",evento.getHoraFim());
                    notificationIntent.putExtra("datainicio",evento.getDataInicio());
                    notificationIntent.putExtra("datafim",evento.getDataFim());
                    if (evento.getPublicoAlvo() != null)
                        notificationIntent.putExtra("publico",evento.getPublicoAlvo());
                    else
                        notificationIntent.putExtra("publico","");
                    if (local != "") {
                        notificationIntent.putExtra("local",local);
                    }else{
                        notificationIntent.putExtra("local","");
                    }

                    notificationIntent.addCategory("android.intent.category.DEFAULT");
                    PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, 5);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
                }
        }
    }
}