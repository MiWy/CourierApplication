package com.tryouts.courierapplication.firebaseservices;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tryouts.courierapplication.MainActivity;
import com.tryouts.courierapplication.R;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String NOTIFICATION_TIMESTAMP = "TIMESTAMP";
    public static final String NOTIFICATION_TYPE = "TYPE";

    private String mType;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload, if yes, then prepare notification
        if (remoteMessage.getData().size() > 0) {
            mType = remoteMessage.getData().get("type");
            decideOnTypeOfNotificationAndMakeIt(remoteMessage);
        }
    }

    private void decideOnTypeOfNotificationAndMakeIt(RemoteMessage remoteMessage) {
        switch (mType) {
            case "new":
                makeNewOrderNotification(remoteMessage);
                break;
            case "taken":
                makeCustomerTakenNotification(remoteMessage);
                break;
            default:
                makeDefaultNotification(remoteMessage);
                break;
        }
    }

    private void makeNewOrderNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        String content = remoteMessage.getData().get("mainTxt");
        String title = remoteMessage.getData().get("title");
        intent.putExtra(NOTIFICATION_TIMESTAMP, remoteMessage.getData().get("timeStamp"));
        intent.putExtra(NOTIFICATION_TYPE, mType);
        sendNotification(intent, content, title);
    }

    private void makeCustomerTakenNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        String content = remoteMessage.getData().get("mainTxt");
        String title = remoteMessage.getData().get("title");
        intent.putExtra(NOTIFICATION_TIMESTAMP, remoteMessage.getData().get("timeStamp"));
        intent.putExtra(NOTIFICATION_TYPE, mType);
        sendNotification(intent, content, title);
    }

    private void makeDefaultNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        String title = remoteMessage.getData().get("title");
        String content = remoteMessage.getData().get("mainTxt");
        intent.putExtra(NOTIFICATION_TYPE, mType);
        sendNotification(intent, content, title);
    }

    private void sendNotification(Intent intent, String content, String title) {

        intent.putExtra(NOTIFICATION_TYPE, mType);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.n_icon_couriers)
                .setContentText(content)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(001 /* ID of notification */, notificationBuilder.build());
    }
}
