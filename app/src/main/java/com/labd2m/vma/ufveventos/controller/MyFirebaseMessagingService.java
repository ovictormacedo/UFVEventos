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
import android.util.Log;

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
                List<Local> locais = new ArrayList<>();
                for (int i = 0; i < numLocais; i++) {
                    Local local = new Local(Integer.parseInt(jsonAux.getJSONObject(0).getString("id")),
                            jsonAux.getJSONObject(0).getString("descricao"),
                            jsonAux.getJSONObject(0).getString("lat"),
                            jsonAux.getJSONObject(0).getString("lng"));
                    locais.add(local);
                }
            }catch(JSONException e){Log.e("JSON ERRO",e.getMessage());}

            Log.i("EVENTO",""+acao+" ----- "+dadosJson);
            Evento evento = null;
            try {
                evento = new Evento(id,denominacao,horainicio,horafim,datainicio,datafim,descricao_evento,
                        programacao_evento,participantes,publico,null,);

                evento = new Evento(Integer.parseInt(dadosJson.getString("id")), dadosJson.getString("denominacao"),
                        dadosJson.getString("horainicio"), dadosJson.getString("horafim"),
                        dadosJson.getString("datainicio"), dadosJson.getString("datafim"),
                        dadosJson.getString("descricao"), "",Integer.parseInt(dadosJson.getString("participantes")),
                        "", null, null, null, 0, 0, null, 0);
            }catch(JSONException e){Log.e("JSON ERRO",e.getMessage());}

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
                        calendar.addEventNotification(evento,local,getBaseContext(),getContentResolver());

                /*Verifica se o usuário deseja gravar a notificação na agenda,
                e se ele acabou de ser atualizado no sistema*/
                }else if (agenda.equals("1") && acao.equals("atualizado")){
                    Log.i("UPDATE EVENTO",evento.getDenominacao());
                    if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_CALENDAR)
                            == PackageManager.PERMISSION_GRANTED)
                        calendar.updateEventNotification(evento,local,getBaseContext(),getContentResolver());

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
                    notificationIntent.putExtra("acao",acao);
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
                        notificationIntent.putExtra("acao",acao);
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
                        PendingIntent broadcast = PendingIntent.getBroadcast(this, 2, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.SECOND, 1); //Envia notificação imediatamente
                        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);

                        //Recupera id do evento
                        sharedPref = ctx.getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0agenda",
                                Context.MODE_PRIVATE);
                        long eventID = sharedPref.getLong(""+evento.getId(),-1);
                        if (eventID != -1)
                            calendar.updateEventNotification(evento,local,getApplicationContext(),getContentResolver());
                    }
        }
    }

    @Override
    public void onDeletedMessages(){Log.i("NOTIFICATION DELETED","CHEGOU");}
}