package com.example.chie.notifitest0429;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.TextView;
//
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

//import static com.example.chie.notifitest0429.MainActivity.flag;
// add by ishii
import com.example.chie.notifitest0429.MainActivity;

/*
 * Created by chie on 2017/04/29.
 */

//Firebaseからの通知の受け取りを担う部分

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessService";
    //
    private LocalBroadcastManager broadcaster;

    // add by ishii
    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(getApplicationContext());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "onMessageReceived");

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        // FCMの通知を受けたことをSharedPreferencesにセットする
        Context context = getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences("FCMessages", Context.MODE_PRIVATE);
        sp.edit().putBoolean("ReceivedMessage", true).commit();

        //
        notifyToLockScreen();

        // TODO
        // activity_mainのflag_viewの表示を変化させる
        // トークンメッセージを受け取った事をLocalBroadcasterを使って発信する
        Intent intent = new Intent("ReceivedMessage");
        broadcaster.sendBroadcast(intent);
    }


    /**
     *
     */
    private void notifyToLockScreen() {

        Log.d("NotificationPublisher", "morningNotification");

        Context context = getApplicationContext();
        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setTicker("Yawn Chat Notification!!")
                .setContentText("チャットのリクエストの通知が届いていますー☀")
                .setContentTitle("Yawn")
                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.yawn_large_icon)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);  // SmallIcon を設定しないと通知されない（例外も発生しない）

        Log.d("NotificationPublisher", "onReceive 3 " + builder);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());

        /**
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, MORNING_ID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, MORNING_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        long futureInMillis = 24*60*60*1000;
        long futureInMillis = System.currentTimeMillis() + (24*60*60*1000);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
        Log.d("PushScheduler", "scheduleMorningPushNotification after alarm setPrepeating");
         */
    }

}
