package com.lly.socketgame.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.lly.socketgame.R;

/**
 * AudioServer[v 1.0.0]
 * classes:com.lly.socketgame.service.AudioServer
 *
 * @author lileiyi
 * @date 2018/11/23
 * @time 9:48
 * @description
 */
public class AudioServer extends Service {

    MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.lol);
        mediaPlayer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }


    public static void startAudioServer(Context context) {
        Intent intent = new Intent(context, AudioServer.class);
        context.startService(intent);
    }

    public static void stopAudioServer(Context context) {
        Intent intent = new Intent(context, AudioServer.class);
        context.stopService(intent);
    }
}
