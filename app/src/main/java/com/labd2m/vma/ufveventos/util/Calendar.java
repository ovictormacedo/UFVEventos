package com.labd2m.vma.ufveventos.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.CalendarContract;

import com.labd2m.vma.ufveventos.model.Evento;
import java.util.TimeZone;

/**
 * Created by vma on 15/01/2018.
 */

public class Calendar{
    public void updateEventNotification(Evento evento,String local,Context context,ContentResolver cr){
        int diaInicio = Integer.parseInt(evento.getDataInicio().substring(8,10));
        int mesInicio = Integer.parseInt(evento.getDataInicio().substring(5,7));
        int anoInicio = Integer.parseInt(evento.getDataInicio().substring(0,4));

        int horaInicio = Integer.parseInt(evento.getHoraInicio().substring(0,2));
        int minutoInicio = Integer.parseInt(evento.getHoraInicio().substring(3,5));

        java.util.Calendar beginTime = java.util.Calendar.getInstance();
        beginTime.set(anoInicio,mesInicio-1,diaInicio,horaInicio,minutoInicio);

        int diaFim = Integer.parseInt(evento.getDataFim().substring(8,10));
        int mesFim = Integer.parseInt(evento.getDataFim().substring(5,7));
        int anoFim = Integer.parseInt(evento.getDataFim().substring(0,4));

        int horaFim = Integer.parseInt(evento.getHoraFim().substring(0,2));
        int minutoFim = Integer.parseInt(evento.getHoraFim().substring(3,5));

        java.util.Calendar endTime = java.util.Calendar.getInstance();
        endTime.set(anoFim,mesFim-1,diaFim,horaFim,minutoFim);

        //Recupera id do evento
        SharedPreferences sharedPref = context.getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0agenda",
                Context.MODE_PRIVATE);
        int eventID = sharedPref.getInt(""+evento.getId(),-1);

        if (eventID != -1) {
            ContentValues values = new ContentValues();
            TimeZone timeZone = TimeZone.getDefault();
            Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);

            values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
            values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
            values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
            values.put(CalendarContract.Events.TITLE, evento.getDenominacao());
            values.put(CalendarContract.Events.DESCRIPTION, evento.getDescricao_evento());
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_LOCATION, local);
            values.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, "1");
            values.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, "1");

            int rows = cr.update(updateUri, values, null, null);
        }
    }

    public void addEvent(Evento evento, Context context, ContentResolver cr, Activity activity) {
        //Requisita permissão para escrita no calendar
        Permission permission = new Permission();
        permission.requestPermissionCalendar(activity,context);

        int diaInicio = Integer.parseInt(evento.getDataInicio().substring(8,10));
        int mesInicio = Integer.parseInt(evento.getDataInicio().substring(5,7));
        int anoInicio = Integer.parseInt(evento.getDataInicio().substring(0,4));

        int horaInicio = Integer.parseInt(evento.getHoraInicio().substring(0,2));
        int minutoInicio = Integer.parseInt(evento.getHoraInicio().substring(3,5));

        java.util.Calendar beginTime = java.util.Calendar.getInstance();
        beginTime.set(anoInicio,mesInicio-1,diaInicio,horaInicio,minutoInicio);

        int diaFim = Integer.parseInt(evento.getDataFim().substring(8,10));
        int mesFim = Integer.parseInt(evento.getDataFim().substring(5,7));
        int anoFim = Integer.parseInt(evento.getDataFim().substring(0,4));

        int horaFim = Integer.parseInt(evento.getHoraFim().substring(0,2));
        int minutoFim = Integer.parseInt(evento.getHoraFim().substring(3,5));

        java.util.Calendar endTime = java.util.Calendar.getInstance();
        endTime.set(anoFim,mesFim-1,diaFim,horaFim,minutoFim);

        ContentValues values = new ContentValues();
        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, evento.getDenominacao());
        values.put(CalendarContract.Events.DESCRIPTION, evento.getDescricao_evento());
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        String local = evento.getLocais().get(0).getDescricao()+","+
                evento.getLocais().get(0).getLatitude()+","+evento.getLocais().get(0).getLongitude();
        values.put(CalendarContract.Events.EVENT_LOCATION, local);
        values.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, "1");
        values.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, "1");

        Uri uri = null;
        try {
            cr.insert(CalendarContract.Events.CONTENT_URI, values);
        }catch (SecurityException e){}

        long eventID = Long.parseLong(uri.getLastPathSegment());

        //Grava id do evento
        SharedPreferences sharedPref = context.getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0agenda",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(""+evento.getId(),""+eventID);
        editor.commit();
    }
    public void addEventNotification(Evento evento, String local,Context context, ContentResolver cr) {
        int diaInicio = Integer.parseInt(evento.getDataInicio().substring(8,10));
        int mesInicio = Integer.parseInt(evento.getDataInicio().substring(5,7));
        int anoInicio = Integer.parseInt(evento.getDataInicio().substring(0,4));

        int horaInicio = Integer.parseInt(evento.getHoraInicio().substring(0,2));
        int minutoInicio = Integer.parseInt(evento.getHoraInicio().substring(3,5));

        java.util.Calendar beginTime = java.util.Calendar.getInstance();
        beginTime.set(anoInicio,mesInicio-1,diaInicio,horaInicio,minutoInicio);

        int diaFim = Integer.parseInt(evento.getDataFim().substring(8,10));
        int mesFim = Integer.parseInt(evento.getDataFim().substring(5,7));
        int anoFim = Integer.parseInt(evento.getDataFim().substring(0,4));

        int horaFim = Integer.parseInt(evento.getHoraFim().substring(0,2));
        int minutoFim = Integer.parseInt(evento.getHoraFim().substring(3,5));

        java.util.Calendar endTime = java.util.Calendar.getInstance();
        endTime.set(anoFim,mesFim-1,diaFim,horaFim,minutoFim);

        ContentValues values = new ContentValues();
        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, evento.getDenominacao());
        values.put(CalendarContract.Events.DESCRIPTION, evento.getDescricao_evento());
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.EVENT_LOCATION, local);
        values.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, "1");
        values.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, "1");

        Uri uri = null;
        try {
            cr.insert(CalendarContract.Events.CONTENT_URI, values);
        }catch (SecurityException e){}

        long eventID = Long.parseLong(uri.getLastPathSegment());

        //Grava id do evento
        SharedPreferences sharedPref = context.getSharedPreferences("UFVEVENTOS45dfd94be4b30d5844d2bcca2d997db0agenda",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(""+evento.getId(),""+eventID);
        editor.commit();
    }
}
