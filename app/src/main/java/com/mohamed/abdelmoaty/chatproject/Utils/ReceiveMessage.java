package com.mohamed.abdelmoaty.chatproject.Utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mohamed.abdelmoaty.chatproject.R;

public class ReceiveMessage extends FirebaseMessagingService {
    int id=1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        final String title=remoteMessage.getNotification().getTitle();
       final String body=remoteMessage.getNotification().getBody();
        final String click_action=remoteMessage.getNotification().getClickAction();
        final String dataFrom=remoteMessage.getData().get("messageFrom");


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(body);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();

                Intent intent = new Intent(click_action);
                intent.putExtra("user_id",dataFrom);  // must be "messageFrom" here like above "messageFrom"
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(ReceiveMessage.this,0 , intent,PendingIntent.FLAG_UPDATE_CURRENT);

                Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ReceiveMessage.this)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(title)
                        .setContentText(name + " want to be your friend")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(id, notificationBuilder.build());
                id++;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }

}
