package com.ulkanova.agentapp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyIntentServices extends IntentService {

    public MyIntentServices() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Intent i = new Intent();
        i.setAction(PropiedadReceiver.PROPIEDAD_CARGADA);
        sendBroadcast(i);
        this.sendBroadcast(i);
    }
}
