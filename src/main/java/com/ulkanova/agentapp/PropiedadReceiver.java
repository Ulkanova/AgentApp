package com.ulkanova.agentapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class PropiedadReceiver extends BroadcastReceiver {

    public static final String PROPIEDAD_CARGADA="com.ulkanova.agentapp.PROPIEDAD_CARGADA";

    @Override
    public void onReceive(Context context, Intent intent) {
        notificar(context,intent);
    }

    private void notificar(Context context, Intent intent){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "1")
                .setSmallIcon(android.R.drawable.star_on)
                .setAutoCancel(true)
                .setContentTitle("Propiedad guardada")
                .setContentText("La propiedad ha sido guardada correctamente en el dispositivo")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification notificacion =  mBuilder.build();


        // obtengo el notification manager
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        // envio la notificacion
        notificationManager.notify(1,notificacion);
    }
}
