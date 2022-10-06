package com.alucard.apolo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

import static com.alucard.apolo.AlbumDetailsAdapter.albumFiles;
import static com.alucard.apolo.AplicationClass.ACTION_NEXT;
import static com.alucard.apolo.AplicationClass.ACTION_PLAY;
import static com.alucard.apolo.AplicationClass.ACTION_PREV;
import static com.alucard.apolo.AplicationClass.CHANNEL_ID_2;
import static com.alucard.apolo.BibliotecaActivity.barrita;
import static com.alucard.apolo.BibliotecaActivity.reproduccion;
import static com.alucard.apolo.ListaDetailsAdapter.listFiles;
import static com.alucard.apolo.BibliotecaActivity.musicFiles;
import static com.alucard.apolo.BibliotecaActivity.repeat;
import static com.alucard.apolo.BibliotecaActivity.shuffle;

public class MusicPlayActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, ActionPlaying, ServiceConnection {

    TextView tvTime, tvDuration, nameArtist, titleSong;
    SeekBar seekBarTime;
    Button btn_back, btn_play_pause, btn_next, btn_add_playlist, btn_favorite, btn_loop, btn_suffle;
    ImageView cover, btn_back_2;
    static String sender = "";
    static int position = -1;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;
    String name, artist, title, time, prueba;
    String fav = "null";
    private String filename = "lista.txt";
    ArchivoJson archivoJson;
    static int Vista = 0;
    int aux;

    //BarraNotificacion
    MusicService musicService;
    MediaSessionCompat mediaSession;

    @Override
    protected void onPause() {
        super.onPause();

        //Bind para notificacion
        unbindService(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //Ocultamos barra
        getSupportActionBar().hide();

        mediaSession = new MediaSessionCompat(this, "PlayerAudio");

        initViews();
        getIntentMethod();
        getPreferences();

        titleSong.setText(listSongs.get(position).getTitle());
        nameArtist.setText(listSongs.get(position).getArtist());

        name = listSongs.get(position).getAlbum();
        artist = listSongs.get(position).getArtist();
        title = listSongs.get(position).getTitle();
        time = listSongs.get(position).getDuration();
        prueba = listSongs.get(position).getPath();

        archivoJson = new ArchivoJson(this, filename);
        fav = archivoJson.BuscarFavorito(titleSong.getText().toString());
        if (fav.equals("True")){
            btn_favorite.setBackgroundResource(R.drawable.liked);
        }
        else {
            btn_favorite.setBackgroundResource(R.drawable.like);
        }

        mediaPlayer.setOnCompletionListener(this);

        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progreso, boolean isFromUser) {
                if (mediaPlayer != null && isFromUser){
                    mediaPlayer.seekTo(progreso);
                    seekBar.setProgress(progreso);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        MusicPlayActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null){
                    double current = mediaPlayer.getCurrentPosition();
                    String elapsedTime = milisecondsToString((int)current);
                    tvTime.setText(elapsedTime);
                    seekBarTime.setProgress((int) current);
                }
                handler.postDelayed(this, 1000);
            }
        });
        btn_suffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffle){
                    shuffle = false;
                    btn_suffle.setBackgroundResource(R.drawable.shuffle_off);
                    Toast.makeText(MusicPlayActivity.this, "Aleatorio desactivado", Toast.LENGTH_SHORT).show();
                }else{
                    shuffle = true;
                    btn_suffle.setBackgroundResource(R.drawable.shuffle_on);
                    Toast.makeText(MusicPlayActivity.this, "Aleatorio activado", Toast.LENGTH_SHORT).show();
                }
                SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean("shuffle",shuffle);
                edit.apply();
            }
        });
        btn_loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeat){
                    repeat = false;
                    btn_loop.setBackgroundResource(R.drawable.loop_off);
                    Toast.makeText(MusicPlayActivity.this, "Repetir desactivado", Toast.LENGTH_SHORT).show();
                }else{
                    repeat = true;
                    btn_loop.setBackgroundResource(R.drawable.loop_on);
                    Toast.makeText(MusicPlayActivity.this, "Repetir activado", Toast.LENGTH_SHORT).show();
                }
                SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean("repeat",repeat);
                edit.apply();
            }
        });

        btn_add_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicPlayActivity.this, Listas.class);
                intent.putExtra("titulo", title);
                intent.putExtra("artista", artist);
                intent.putExtra("tiempo", time);
                intent.putExtra("album", name);
                intent.putExtra("caratula", prueba);
                MusicPlayActivity.this.startActivity(intent);
            }
        });

        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fav.equals("False")){
                    btn_favorite.setBackgroundResource(R.drawable.liked);
                    archivoJson = new ArchivoJson(MusicPlayActivity.this, filename);
                    archivoJson.AgregarCancion(0, title, artist, time, name, prueba);
                    fav = "True";
                }else if(fav.equals("True")) {
                    btn_favorite.setBackgroundResource(R.drawable.like);
                    archivoJson = new ArchivoJson(MusicPlayActivity.this, filename);
                    archivoJson.QuitarDeFavoritos(title);
                    fav = "False";
                }else {
                    btn_favorite.setBackgroundResource(R.drawable.liked);
                    archivoJson = new ArchivoJson(MusicPlayActivity.this, filename);
                    archivoJson.AgregarCancion(0, title, artist, time, name, prueba);
                    fav = "True";
                }
            }
        });

        btn_back_2.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            }
        );
    }

    public void actualizaBarrita(){
        reproduccion = true;
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("position",position);
        edit.putString("sender",sender);
        edit.apply();
        barrita.actualizaBarra();
    }

    private void getPreferences() {
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        shuffle = sp.getBoolean("shuffle",false);
        repeat = sp.getBoolean("repeat",false);
        if (shuffle){
            btn_suffle.setBackgroundResource(R.drawable.shuffle_on);
        }else{
            btn_suffle.setBackgroundResource(R.drawable.shuffle_off);
        }
        if (repeat){
            btn_loop.setBackgroundResource(R.drawable.loop_on);
        }else{
            btn_loop.setBackgroundResource(R.drawable.loop_off);
        }
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this,MusicService.class);
        bindService(intent,this, BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    private void prevThreadBtn() {
        prevThread = new Thread(){
            @Override
            public void run() {
                super.run();
                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_back_clicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    public void btn_back_clicked() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();

            //Validamos si el random esta activo
            if (shuffle && !repeat){
                position = getRandom(listSongs.size()-1);
            }else if(!shuffle && !repeat){
                position = ((position-1) < 0 ? (listSongs.size() -1 ) : (position - 1));
            }

            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri.toString());
            titleSong.setText(listSongs.get(position).getTitle());
            nameArtist.setText(listSongs.get(position).getArtist());
            archivoJson = new ArchivoJson(this, filename);
            fav = archivoJson.BuscarFavorito(titleSong.getText().toString());
            if (fav.equals("True")){
                btn_favorite.setBackgroundResource(R.drawable.liked);
            }
            else {
                btn_favorite.setBackgroundResource(R.drawable.like);
            }

            seekBarTime.setMax(mediaPlayer.getDuration());
            MusicPlayActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        double current = mediaPlayer.getCurrentPosition();
                        seekBarTime.setProgress((int) current);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            btn_play_pause.setBackgroundResource(R.drawable.pause);
            //Indicamos cuanto dura la cancion
            String duration = milisecondsToString(mediaPlayer.getDuration());
            tvDuration.setText(duration);
            mediaPlayer.start();
            showNotification(R.drawable.pause_nt);
            actualizaBarrita();
        }else{
            mediaPlayer.stop();
            mediaPlayer.release();

            //Validamos si el random esta activo
            if (shuffle && !repeat){
                position = getRandom(listSongs.size()-1);
            }else if(!shuffle && !repeat){
                position = ((position-1) < 0 ? (listSongs.size() -1 ) : (position - 1));
            }

            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri.toString());
            titleSong.setText(listSongs.get(position).getTitle());
            nameArtist.setText(listSongs.get(position).getArtist());
            archivoJson = new ArchivoJson(this, filename);
            fav = archivoJson.BuscarFavorito(titleSong.getText().toString());
            if (fav.equals("True")){
                btn_favorite.setBackgroundResource(R.drawable.liked);
            }
            else {
                btn_favorite.setBackgroundResource(R.drawable.like);
            }

            seekBarTime.setMax(mediaPlayer.getDuration());
            MusicPlayActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        double current = mediaPlayer.getCurrentPosition();
                        seekBarTime.setProgress((int) current);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            //Indicamos cuanto dura la cancion
            String duration = milisecondsToString(mediaPlayer.getDuration());
            tvDuration.setText(duration);
            btn_play_pause.setBackgroundResource(R.drawable.play);
            showNotification(R.drawable.play_arrow_nt);
            actualizaBarrita();
        }
    }

    private void nextThreadBtn() {
        nextThread = new Thread(){
            @Override
            public void run() {
                super.run();
                btn_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_next_clicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    public void btn_next_clicked() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();

            //Validamos si el random esta activo
            if (shuffle && !repeat){
                position = getRandom(listSongs.size()-1);
            }else if(!shuffle && !repeat){
                position = ((position+1) % listSongs.size());
            }

            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri.toString());
            titleSong.setText(listSongs.get(position).getTitle());
            nameArtist.setText(listSongs.get(position).getArtist());
            archivoJson = new ArchivoJson(this, filename);
            fav = archivoJson.BuscarFavorito(titleSong.getText().toString());
            if (fav.equals("True")){
                btn_favorite.setBackgroundResource(R.drawable.liked);
            }
            else {
                btn_favorite.setBackgroundResource(R.drawable.like);
            }

            seekBarTime.setMax(mediaPlayer.getDuration());
            MusicPlayActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        double current = mediaPlayer.getCurrentPosition();
                        seekBarTime.setProgress((int) current);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            btn_play_pause.setBackgroundResource(R.drawable.pause);
            //Indicamos cuanto dura la cancion
            String duration = milisecondsToString(mediaPlayer.getDuration());
            tvDuration.setText(duration);
            mediaPlayer.start();
            showNotification(R.drawable.pause_nt);
            actualizaBarrita();
        }else{
            mediaPlayer.stop();
            mediaPlayer.release();

            //Validamos si el random esta activo
            if (shuffle && !repeat){
                position = getRandom(listSongs.size()-1);
            }else if(!shuffle && !repeat){
                position = ((position+1) % listSongs.size());
            }

            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri.toString());
            titleSong.setText(listSongs.get(position).getTitle());
            nameArtist.setText(listSongs.get(position).getArtist());
            archivoJson = new ArchivoJson(this, filename);
            fav = archivoJson.BuscarFavorito(titleSong.getText().toString());
            if (fav.equals("True")){
                btn_favorite.setBackgroundResource(R.drawable.liked);
            }
            else {
                btn_favorite.setBackgroundResource(R.drawable.like);
            }

            seekBarTime.setMax(mediaPlayer.getDuration());
            MusicPlayActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        double current = mediaPlayer.getCurrentPosition();
                        seekBarTime.setProgress((int) current);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            //Indicamos cuanto dura la cancion
            String duration = milisecondsToString(mediaPlayer.getDuration());
            tvDuration.setText(duration);
            btn_play_pause.setBackgroundResource(R.drawable.play);
            showNotification(R.drawable.play_arrow_nt);
            actualizaBarrita();
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i+1);
    }

    private void playThreadBtn() {
        playThread = new Thread(){
            @Override
            public void run() {
                super.run();
                btn_play_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_play_pause_Clicked();
                    }
                });
            }
        };
        playThread.start();
    }

    public void btn_play_pause_Clicked() {
        if (mediaPlayer.isPlaying()){
            btn_play_pause.setBackgroundResource(R.drawable.play_arrow_nt);
            showNotification(R.drawable.play_arrow_nt);
            mediaPlayer.pause();
            seekBarTime.setMax(mediaPlayer.getDuration());
            MusicPlayActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        double current = mediaPlayer.getCurrentPosition();
                        seekBarTime.setProgress((int) current);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }else{
            btn_play_pause.setBackgroundResource(R.drawable.pause);
            showNotification(R.drawable.pause_nt);
            mediaPlayer.start();
            seekBarTime.setMax(mediaPlayer.getDuration());
            MusicPlayActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        double current = mediaPlayer.getCurrentPosition();
                        seekBarTime.setProgress((int) current);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private void getIntentMethod() {
        if (reproduccion){
            SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
            Log.e("Sender", sp.getString("sender", "")+"");
            sender = sp.getString("sender", "");
        }else{
            position = getIntent().getIntExtra("position",-1);
            sender = getIntent().getStringExtra("sender");
        }

        if (sender != null && sender.equals("albumDetails")){
            listSongs = albumFiles;
        }else if (sender != null && sender.equals("listDetails")){
            listSongs = listFiles;
        }
        else{
            listSongs = musicFiles;
        }

        if (listSongs != null){
            btn_play_pause.setBackgroundResource(R.drawable.pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        if (mediaPlayer != null){
            if (!reproduccion) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                showNotification(R.drawable.pause_nt);
                actualizaBarrita();
            }
        }else{
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            showNotification(R.drawable.pause_nt);
            actualizaBarrita();
        }
        seekBarTime.setMax(mediaPlayer.getDuration());
        //Indicamos cuanto dura la cancion
        String duration = milisecondsToString(mediaPlayer.getDuration());
        tvDuration.setText(duration);
        //Cargamos portada
        metaData(uri.toString());
    }

    private void initViews(){
        tvTime = findViewById(R.id.tvTime);
        tvDuration = findViewById(R.id.tvDuration);
        nameArtist = findViewById(R.id.nameArtist);
        titleSong = findViewById(R.id.titleSong);

        seekBarTime = findViewById(R.id.seekBarTime);

        btn_back = findViewById(R.id.btn_back);
        btn_play_pause = findViewById(R.id.btn_play_pause);
        btn_next = findViewById(R.id.btn_next);
        btn_add_playlist = findViewById(R.id.btn_add_playlist);
        btn_favorite = findViewById(R.id.btn_favorite);
        btn_loop = findViewById(R.id.btn_loop);
        btn_suffle = findViewById(R.id.btn_suffle);

        cover = findViewById(R.id.coverArt);

        btn_back_2 = findViewById(R.id.btn_back_2);
    }

    private String milisecondsToString(int time){
        String elapsedTime="";
        int minutos = time / 1000 / 60;
        int segundos = time / 1000 % 60;
        elapsedTime = minutos+":";
        if (segundos<10){
            elapsedTime += "0";
        }
        elapsedTime+=segundos;

        return elapsedTime;
    }

    private void metaData(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null){
            Glide.with(getApplicationContext()).asBitmap().load(art).into(cover);
            //Cambia el fondo de acorde al color de la portada
            bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null){
                        RelativeLayout gradient = findViewById(R.id.mContainer);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), swatch.getRgb()});
                        gradient.setBackground(gradientDrawable);
                    }else{
                        RelativeLayout gradient = findViewById(R.id.mContainer);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0xff000000});
                        gradient.setBackground(gradientDrawable);
                    }
                }
            });
        }else{
            Glide.with(getApplicationContext()).asBitmap().load(R.drawable.no_cover).into(cover);
            RelativeLayout gradient = findViewById(R.id.mContainer);
            gradient.setBackgroundResource(R.color.colorSecondary);
        }
        retriever.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        btn_next_clicked();
        if (mediaPlayer != null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            btn_play_pause.setBackgroundResource(R.drawable.pause);
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder)service;
        musicService = myBinder.getService();
        musicService.setCallBack(MusicPlayActivity.this);
        //Toast.makeText(this, "Conectado "+musicService,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
        //Toast.makeText(this, "Desconectado "+musicService,Toast.LENGTH_SHORT).show();
    }

    public void showNotification(int playPauseBtn){
        Intent intent = new Intent(this, MusicPlayActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,0);

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap picture;
        byte[] image = getAlbumArt(listSongs.get(position).getPath());
        if(image != null){
            picture = BitmapFactory.decodeByteArray(image,0,image.length);
        }else{
            picture = BitmapFactory.decodeResource(getResources(), R.drawable.no_cover);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(R.drawable.no_cover)
                .setLargeIcon(picture)
                .setContentTitle(listSongs.get(position).getTitle())
                .setContentText(listSongs.get(position).getArtist())
                .addAction(R.drawable.previous_nt, "Anterior", prevPendingIntent)
                .addAction(playPauseBtn, "Play", playPendingIntent)
                .addAction(R.drawable.next_nt, "Siguiente", nextPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
