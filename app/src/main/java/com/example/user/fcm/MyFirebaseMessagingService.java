package com.example.user.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;

/**
 * Created by USER on 2017/5/15.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    SharedPreferences settings;
    static String Message;
    private Notification n;
    private NotificationManager nm;
   public static final String BROADCAST_ACTION =
            "net.macdidi.broadcast01.action.MYBROADCAST01";

    // 建立準備發送廣播事件的Intent物件
    public static Intent intent = new Intent(BROADCAST_ACTION);
    // 如果需要的話，也可以設定資料到Intent物件
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Title: " + remoteMessage.getNotification().getTitle());
        Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Message=remoteMessage.getNotification().getBody();
        intent.putExtra("message", Message);

        settings = getSharedPreferences("DATA",0);
        /*
        settings.edit().putString("From","From")
                      .putString("Title","Title")
                      .putString("Message","Message")
                      .apply();*/
        settings.edit().putString("From",remoteMessage.getFrom())
                .putString("Title",remoteMessage.getNotification().getTitle())
                .putString("Message",remoteMessage.getNotification().getBody())
                .apply();

        // 發送廣播事件
        sendBroadcast(intent);      /*  String service = NOTIFICATION_SERVICE;
        nm = (NotificationManager)getSystemService(service);

        n = new Notification();
        // int icon = n.icon =R.drawable.icon;
        String tickerText = MyFirebaseMessagingService.Message;
        Log.e("tickerText",tickerText);
        long when = System.currentTimeMillis();
        // n.icon = icon;
        n.tickerText = tickerText;
        n.when = when;*/

    }
}
