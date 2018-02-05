package com.example.vma.ufveventos.controller;

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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.vma.ufveventos.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by vma on 04/01/2018.
 */

public class NotificationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(final Context context, Intent it){
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.logo_sobre,null);

            Intent notificationIntent = new Intent(context, inicial.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(inicial.class);
            stackBuilder.addNextIntent(notificationIntent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            Notification notification = builder.setContentTitle("UFV Eventos")
                    .setContentText("Chegaram novos eventos!")
                    .setTicker("Chegaram novos eventos!")
                    .setLargeIcon(bitmap)
                    .setSmallIcon(R.drawable.logo_ufv1)
                    .setSound(notificationSound)
                    .setContentIntent(pendingIntent).build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
