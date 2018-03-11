package com.labd2m.vma.ufveventos.controller;

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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.gson.Gson;
import com.labd2m.vma.ufveventos.R;
import com.labd2m.vma.ufveventos.model.Evento;
import com.labd2m.vma.ufveventos.util.Calendar;

/**
 * Created by vma on 04/01/2018.
 */

public class NotificationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(final Context context, Intent it){
        Log.i("NOTIFICATION RECEIVER","CHEGOU");
        String eventoJson = it.getStringExtra("evento");
        Gson gson = new Gson();
        Evento evento = gson.fromJson(eventoJson, Evento.class);
        String acao = it.getStringExtra("acao");
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_sobre, null);

            if (acao.equals("cancelado")) {//Evento cancelado
                Log.i("NOTIFICATION RECEIVER","CANCELADO");
                String descricao = evento.getDescricao_evento();
                Intent notificationIntent = null;
                if (!descricao.equals(""))
                    notificationIntent = new Intent(context,evento_cancelado_com_descricao.class);
                else
                    notificationIntent = new Intent(context,evento_cancelado_sem_descricao.class);
                /*
                notificationIntent.putExtra("descricao_evento",it.getStringExtra("descricao_evento"));
                notificationIntent.putExtra("participantes",it.getStringExtra("participantes"));
                notificationIntent.putExtra("mostrarparticipantes",it.getStringExtra("mostrarparticipantes"));
                notificationIntent.putExtra("programacao_evento",it.getStringExtra("programacao_evento"));
                notificationIntent.putExtra("teminscricao",it.getStringExtra("teminscricao"));
                notificationIntent.putExtra("valorinscricao",it.getStringExtra("valorinscricao"));
                notificationIntent.putExtra("linklocalinscricao",it.getStringExtra("linklocalinscricao"));
                notificationIntent.putExtra("id",it.getStringExtra("id"));
                notificationIntent.putExtra("acao",it.getStringExtra("acao"));
                notificationIntent.putExtra("denominacao", it.getStringExtra("denominacao"));
                notificationIntent.putExtra("horainicio", it.getStringExtra("horainicio"));
                notificationIntent.putExtra("horafim", it.getStringExtra("horafim"));
                notificationIntent.putExtra("datainicio", it.getStringExtra("datainicio"));
                notificationIntent.putExtra("datafim", it.getStringExtra("datafim"));
                notificationIntent.putExtra("publico", it.getStringExtra("publico"));
                */

                gson = new Gson();
                String json = gson.toJson(evento);
                notificationIntent.putExtra("evento", json);
                notificationIntent.putExtra("acao", acao);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                if (!descricao.equals(""))
                    stackBuilder.addParentStack(evento_cancelado_com_descricao.class);
                else
                    stackBuilder.addParentStack(evento_cancelado_sem_descricao.class);

                stackBuilder.addNextIntent(notificationIntent);

                PendingIntent pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
                Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                Notification notification = builder.setContentTitle("UFV Eventos")
                        .setAutoCancel(true)
                        .setContentText("Um evento foi cancelado")
                        .setTicker("Um evento foi cancelado")
                        .setLargeIcon(bitmap)
                        .setSmallIcon(R.drawable.notificacao)
                        .setSound(notificationSound)
                        .setContentIntent(pendingIntent).build();

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification);
            } else if (acao.equals("novo")) { //Novo evento adicionado
                    Log.i("NOTIFICATION RECEIVER","NOVO");
                    Intent notificationIntent = new Intent(context, inicial.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(inicial.class);
                    stackBuilder.addNextIntent(notificationIntent);
                    PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                    Notification notification = builder.setContentTitle("UFV Eventos")
                            .setAutoCancel(true)
                            .setContentText("Chegaram novos eventos")
                            .setTicker("Chegaram novos eventos")
                            .setLargeIcon(bitmap)
                            .setSmallIcon(R.drawable.notificacao)
                            .setSound(notificationSound)
                            .setContentIntent(pendingIntent).build();

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notification);
                } else if (acao.equals("atualizado")) { //Evento atualizado
                        Log.i("NOTIFICATION RECEIVER","ATUALIZADO");
                        String descricao = evento.getDescricao_evento();
                        Intent notificationIntent = null;
                        if (!descricao.equals(""))
                            notificationIntent = new Intent(context,evento_atualizado_com_descricao.class);
                        else
                            notificationIntent = new Intent(context,evento_atualizado_sem_descricao.class);
                        /*
                        notificationIntent.putExtra("descricao_evento",it.getStringExtra("descricao_evento"));
                        notificationIntent.putExtra("participantes",it.getStringExtra("participantes"));
                        notificationIntent.putExtra("mostrarparticipantes",it.getStringExtra("mostrarparticipantes"));
                        notificationIntent.putExtra("programacao_evento",it.getStringExtra("programacao_evento"));
                        notificationIntent.putExtra("teminscricao",it.getStringExtra("teminscricao"));
                        notificationIntent.putExtra("valorinscricao",it.getStringExtra("valorinscricao"));
                        notificationIntent.putExtra("linklocalinscricao",it.getStringExtra("linklocalinscricao"));
                        notificationIntent.putExtra("id",it.getStringExtra("id"));
                        notificationIntent.putExtra("acao",it.getStringExtra("acao"));
                        notificationIntent.putExtra("denominacao", it.getStringExtra("denominacao"));
                        notificationIntent.putExtra("horainicio", it.getStringExtra("horainicio"));
                        notificationIntent.putExtra("horafim", it.getStringExtra("horafim"));
                        notificationIntent.putExtra("datainicio", it.getStringExtra("datainicio"));
                        notificationIntent.putExtra("datafim", it.getStringExtra("datafim"));
                        notificationIntent.putExtra("publico", it.getStringExtra("publico"));
                        */

                        gson = new Gson();
                        String json = gson.toJson(evento);
                        notificationIntent.putExtra("evento", json);
                        notificationIntent.putExtra("acao", acao);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        if (!descricao.equals(""))
                            stackBuilder.addParentStack(evento_atualizado_com_descricao.class);
                        else
                            stackBuilder.addParentStack(evento_atualizado_sem_descricao.class);
                        stackBuilder.addNextIntent(notificationIntent);

                        PendingIntent pendingIntent = stackBuilder.getPendingIntent(2, PendingIntent.FLAG_UPDATE_CURRENT);
                        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        Notification notification = builder.setContentTitle("UFV Eventos")
                                .setContentText("Um evento foi atualizado")
                                .setTicker("Um evento foi atualizado")
                                .setLargeIcon(bitmap)
                                .setSmallIcon(R.drawable.notificacao)
                                .setSound(notificationSound)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent).build();

                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(2, notification);
                    }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
