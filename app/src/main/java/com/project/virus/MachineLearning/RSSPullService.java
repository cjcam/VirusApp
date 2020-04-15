package com.project.virus.MachineLearning;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.project.virus.R;
import com.project.virus.StartingPoint;

public class RSSPullService extends IntentService {
    //Run a foreground service
    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        //Jobs go here
        startForeground();

        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, StartingPoint.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        startForeground(NOTIF_ID, new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)//create notification channel
                .setOngoing(true)
                .setSmallIcon(R.drawable.virus)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running in background")
                .setContentIntent(pendingIntent)
                .build());
    }


    @Override
    protected void onHandleIntent(Intent workIntent) {
        //Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

        //Basedon the contents of dataString
    }

    public RSSPullService() {
        super("RSSPULLSERVICE");
    }
}
