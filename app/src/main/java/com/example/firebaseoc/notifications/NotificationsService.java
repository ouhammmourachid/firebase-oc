package com.example.firebaseoc.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.firebaseoc.R;
import com.example.firebaseoc.ui.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationsService extends FirebaseMessagingService {
    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "FIREBASEOC";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if(message.getNotification() != null){
            RemoteMessage.Notification notification = message.getNotification();
            sendVisualNotification(notification);
        }
    }

    private void sendVisualNotification(RemoteMessage.Notification notification) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_web_client_id);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this,channelId)
                        .setSmallIcon(R.drawable.ic_image_notification)
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getBody())
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        // Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Firebase Messages";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(NOTIFICATION_TAG,NOTIFICATION_ID,notificationBuilder.build());
    }
}
