package com.labd2m.vma.ufveventos.controller;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.FloatProperty;
import android.util.Log;

import com.google.gson.Gson;
import com.labd2m.vma.ufveventos.R;
import com.labd2m.vma.ufveventos.model.Evento;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.labd2m.vma.ufveventos.model.Local;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        Log.i("NOTIFICATION","Chegou notificação");
        //Verifica se a mensagem contém notificação
        if (remoteMessage.getNotification() != null || remoteMessage.getData().size() > 0){
            //Recupera dados da notificação
            String acao="",id="",denominacao="",descricao_evento="",programacao_evento="",
                    horainicio="",horafim="",datainicio="",datafim="",participantes="",
                    publico="",teminscricao="",valorinscricao="",linklocalinscricao="",mostrarparticipantes="";
            Map<String,String> dados = remoteMessage.getData();
            JSONObject dadosJson = null;
            List<Local> locais = new ArrayList<>();
            try {
                dadosJson = new JSONObject(dados.get("body"));
                acao = dadosJson.getString("acao");
                id = dadosJson.getString("id");
                denominacao = dadosJson.getString("denominacao");
                descricao_evento = dadosJson.getString("descricao_evento");
                programacao_evento = dadosJson.getString("programacao_evento");
                horainicio = dadosJson.getString("horainicio").substring(0, 5);
                horafim = dadosJson.getString("horafim").substring(0, 5);
                datainicio = dadosJson.getString("datainicio");
                datainicio = datainicio.substring(8, 10) + "/" + datainicio.substring(5, 7) + "/" + datainicio.substring(0, 4);
                datafim = dadosJson.getString("datafim");
                datafim = datafim.substring(8, 10) + "/" + datafim.substring(5, 7) + "/" + datafim.substring(0, 4);
                participantes = dadosJson.getString("participantes");
                publico = dadosJson.getString("publico");
                teminscricao = dadosJson.getString("teminscricao");
                valorinscricao = dadosJson.getString("valorinscricao");
                linklocalinscricao = dadosJson.getString("linklocalinscricao");
                mostrarparticipantes = dadosJson.getString("mostrarparticipantes");
                JSONArray jsonAux = new JSONArray(dadosJson.getString("locais"));
                int numLocais = jsonAux.length();
                for (int i = 0; i < numLocais; i++) {
                    Local local = new Local(Integer.parseInt(jsonAux.getJSONObject(i).getString("id")),
                            jsonAux.getJSONObject(i).getString("descricao"),
                            jsonAux.getJSONObject(i).getString("lat"),
                            jsonAux.getJSONObject(i).getString("lng"));
                    locais.add(local);
                }
            }catch(JSONException e){Log.e("JSON ERRO",e.getMessage());}

            Log.i("EVENTO",""+acao+" ----- "+dadosJson);
            Evento evento = new Evento(Integer.parseInt(id),denominacao,horainicio,horafim,datainicio,datafim,descricao_evento,
                    programacao_evento,Integer.parseInt(participantes),publico,null,locais,null,Integer.parseInt(teminscricao),
                    Float.parseFloat(valorinscricao),linklocalinscricao,Integer.parseInt(mostrarparticipantes));

            com.labd2m.vma.ufveventos.util.Calendar calendar = new com.labd2m.vma.ufveventos.util.Calendar();

            //Verifica se o usuário deseja que a notificação seja adicionada à agenda
            SharedPreferences sharedPref = this.getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0", Context.MODE_PRIVATE);
            String idUsuario = sharedPref.getString("id","default");

            //Se o usuário está logado
            if (!idUsuario.equals("default")) {
                Log.i("ADD EVENTO","LOGADO");
                String agenda = sharedPref.getString("agenda","default");
                /*Verifica se o usuário deseja gravar a notificação na agenda,
                e se ele acabou de ser adicionado ao sistema*/
                if (agenda.equals("1") && acao.equals("novo")){
                    Log.i("ADD EVENTO",evento.getDenominacao());

                    if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_CALENDAR)
                            == PackageManager.PERMISSION_GRANTED)
                        calendar.addEventNotification(evento,getBaseContext(),getContentResolver());

                /*Verifica se o usuário deseja gravar a notificação na agenda,
                e se ele acabou de ser atualizado no sistema*/
                }else if (agenda.equals("1") && acao.equals("atualizado")){
                    Log.i("UPDATE EVENTO",evento.getDenominacao());
                    if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_CALENDAR)
                            == PackageManager.PERMISSION_GRANTED)
                        calendar.updateEventNotification(evento,getBaseContext(),getContentResolver());

                }else if (agenda.equals("1") && acao.equals("cancelado")) {
                    /*Verifica se o usuário deseja gravar a notificação na agenda,
                    e se ele acabou de ser cancelado no sistema*/
                    Log.i("UPDATE EVENTO CANCELADO",evento.getDenominacao());
                    if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_CALENDAR)
                            == PackageManager.PERMISSION_GRANTED)
                        calendar.deleteEventNotification(evento,getBaseContext(),getContentResolver());
                }
            }

            //Verifica se o usuário deseja receber notificacoes
            String notificacoes = sharedPref.getString("notificacoes","1");
            //Chegou novo evento
            if (notificacoes.equals("1") && acao.equals("novo")) {
                Log.i("NOTIFICATION","Envia notificação - novo");
                /*Cancela notificação já agendada, a fim de impedir que o app
                inunde o celular de notificações de novos eventos*/
                Context ctx = getApplicationContext();
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent cancelServiceIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                PendingIntent cancelServicePendingIntent = PendingIntent.getBroadcast(
                        ctx,
                        0, // integer constant used to identify the service
                        cancelServiceIntent,
                        0 //no FLAG needed for a service cancel
                );
                alarmManager.cancel(cancelServicePendingIntent);

                //Agenda nova notificação
                Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                notificationIntent.addCategory("android.intent.category.DEFAULT");
                notificationIntent.putExtra("acao",acao);
                PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, 5); //Envia a notificação num horário agendado
                 alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
                //Evento cancelado
            }else if (notificacoes.equals("1") && acao.equals("cancelado")) {
                    Log.i("NOTIFICATION","Envia notificação - cancelado");
                    //Agenda nova notificação
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");

                    /*
                    notificationIntent.putExtra("descricao_evento",descricao_evento);
                    notificationIntent.putExtra("programacao_evento",programacao_evento);
                    notificationIntent.putExtra("participantes",participantes);
                    notificationIntent.putExtra("mostrarparticipantes",mostrarparticipantes);
                    notificationIntent.putExtra("teminscricao",teminscricao);
                    notificationIntent.putExtra("valorinscricao",valorinscricao);
                    notificationIntent.putExtra("linklocalinscricao",linklocalinscricao);
                    notificationIntent.putExtra("acao",acao);
                    notificationIntent.putExtra("id",id);
                    notificationIntent.putExtra("denominacao",denominacao);
                    notificationIntent.putExtra("horainicio",horainicio);
                    notificationIntent.putExtra("horafim",horafim);
                    notificationIntent.putExtra("datainicio",datainicio);
                    notificationIntent.putExtra("datafim",datafim);
                    notificationIntent.putExtra("publico",publico);
                    */

                    Gson gson = new Gson();
                    String json = gson.toJson(evento);
                    notificationIntent.putExtra("evento", json);
                    notificationIntent.putExtra("acao", acao);

                    notificationIntent.addCategory("android.intent.category.DEFAULT");
                    PendingIntent broadcast = PendingIntent.getBroadcast(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, 1); //Envia notificação imediatamente
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);

                    //Recupera id do evento
                    sharedPref = getApplicationContext().getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0agenda",
                            Context.MODE_PRIVATE);
                    long eventID = sharedPref.getLong(""+evento.getId(),-1);
                    if (eventID != -1)
                        calendar.deleteEventNotification(evento,getApplicationContext(),getContentResolver());
                    //Evento atualizado
                    }else if (notificacoes.equals("1") && acao.equals("atualizado")) {
                        Log.i("NOTIFICATION","Envia notificação - atualizado");
                        /*Cancela notificação já agendada, a fim de impedir que o app
                        inunde o celular de notificações de novos eventos*/
                        Context ctx = getApplicationContext();
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent cancelServiceIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                        PendingIntent cancelServicePendingIntent = PendingIntent.getBroadcast(
                                ctx,
                                2, // integer constant used to identify the service
                                cancelServiceIntent,
                                0 //no FLAG needed for a service cancel
                        );
                        alarmManager.cancel(cancelServicePendingIntent);

                        //Agenda nova notificação
                        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                        /*
                        notificationIntent.putExtra("descricao_evento",descricao_evento);
                        notificationIntent.putExtra("programacao_evento",programacao_evento);
                        notificationIntent.putExtra("participantes",participantes);
                        notificationIntent.putExtra("mostrarparticipantes",mostrarparticipantes);
                        notificationIntent.putExtra("teminscricao",teminscricao);
                        notificationIntent.putExtra("valorinscricao",valorinscricao);
                        notificationIntent.putExtra("linklocalinscricao",linklocalinscricao);
                        notificationIntent.putExtra("acao",acao);
                        notificationIntent.putExtra("id",id);
                        notificationIntent.putExtra("denominacao",denominacao);
                        notificationIntent.putExtra("horainicio",horainicio);
                        notificationIntent.putExtra("horafim",horafim);
                        notificationIntent.putExtra("datainicio",datainicio);
                        notificationIntent.putExtra("datafim",datafim);
                        notificationIntent.putExtra("publico",publico);
                        */

                        Gson gson = new Gson();
                        String json = gson.toJson(evento);
                        notificationIntent.putExtra("evento", json);
                        notificationIntent.putExtra("acao", acao);

                        notificationIntent.addCategory("android.intent.category.DEFAULT");
                        PendingIntent broadcast = PendingIntent.getBroadcast(this, 2, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.SECOND, 1); //Envia notificação imediatamente
                        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);

                        //Recupera id do evento
                        sharedPref = ctx.getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0agenda",
                                Context.MODE_PRIVATE);
                        long eventID = sharedPref.getLong(""+evento.getId(),-1);
                        if (eventID != -1)
                            calendar.updateEventNotification(evento,getApplicationContext(),getContentResolver());
                    }
        }
    }

    @Override
    public void onDeletedMessages(){Log.i("NOTIFICATION DELETED","CHEGOU");}
}