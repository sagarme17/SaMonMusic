package com.alucard.apolo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.alucard.apolo.AplicationClass.ACTION_NEXT;
import static com.alucard.apolo.AplicationClass.ACTION_PLAY;
import static com.alucard.apolo.AplicationClass.ACTION_PREV;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, MusicService.class);
        if (intent.getAction() != null){
            switch (intent.getAction()) {
                case ACTION_PLAY:
                    intent1.putExtra("myActionName", intent.getAction());
                    context.startService(intent1);
                    break;
                case ACTION_NEXT:
                    intent1.putExtra("myActionName", intent.getAction());
                    context.startService(intent1);
                    break;
                case ACTION_PREV:
                    intent1.putExtra("myActionName", intent.getAction());
                    context.startService(intent1);
                    break;
            }
        }
    }
}
