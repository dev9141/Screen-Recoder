package com.manddprojectconsultant.screencam.service;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.manddprojectconsultant.screencam.R;
public class ScreenRecordingService extends Service {
    private static final int NOTIFICATION_ID = 1;

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        ScreenRecordingService getService() {
            return ScreenRecordingService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotification();
        startForeground(NOTIFICATION_ID, createNotification());
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    private Notification createNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    "screen_recording",
                    "Screen Recording",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "screen_recording")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle("Screen Recording")
                .setContentText("Recording in progress")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder.build();
    }
}