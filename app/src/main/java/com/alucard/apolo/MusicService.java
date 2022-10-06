package com.alucard.apolo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import static com.alucard.apolo.AplicationClass.ACTION_NEXT;
import static com.alucard.apolo.AplicationClass.ACTION_PLAY;
import static com.alucard.apolo.AplicationClass.ACTION_PREV;

public class MusicService extends Service {
    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    ActionPlaying actionPlaying;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        return mBinder;
    }

    public class MyBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String actionName = intent.getStringExtra("myActionName");
        if (actionName!=null){
            switch (actionName) {
                case ACTION_PLAY:
                    if (actionPlaying != null) {
                        actionPlaying.btn_play_pause_Clicked();
                    }
                    break;
                case ACTION_NEXT:
                    if (actionPlaying != null) {
                        actionPlaying.btn_next_clicked();
                    }
                    break;
                case ACTION_PREV:
                    if (actionPlaying != null) {
                        actionPlaying.btn_back_clicked();
                    }
                    break;
            }
        }
        return START_STICKY;
        /*Log.e("OnStart", "Method");
        return super.onStartCommand(intent, flags, startId);*/
    }

    public void setCallBack(ActionPlaying actionPlaying){
        this.actionPlaying = actionPlaying;
    }
}
