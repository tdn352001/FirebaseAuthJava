package com.example.firebasemessaging.Service;

import static com.example.firebasemessaging.Service.MyApplication.CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.firebasemessaging.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseMessaging extends FirebaseMessagingService {

    public static final String TAG = FirebaseMessaging.class.getName();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        if(notification != null){
            String strTitle = notification.getTitle();
            String strMessage = notification.getBody();
            pushNotification(strTitle, strMessage);
            return;
        }

        Map<String, String> stringMap = remoteMessage.getData();
        String username = stringMap.get("username");
        String password = stringMap.get("password");
        String action = stringMap.get("action");
        pushNotification(username, password + action);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("BBBB", s);
    }

    private void pushNotification(String strTitle, String strMessage) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(strTitle)
                .setContentText(strMessage)
                .setSmallIcon(R.drawable.ic_launcher_background);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}