package com.example.vma.ufveventos.controller;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.vma.ufveventos.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by vma on 07/12/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        //Verifica se a mensagem contém notificação
        if (remoteMessage.getNotification() != null || !remoteMessage.getData().isEmpty()){
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