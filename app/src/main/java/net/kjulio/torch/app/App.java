package net.kjulio.torch.app;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;

import net.kjulio.torch.R;
import net.kjulio.torch.timber.TimberInitializer;
import net.kjulio.torch.ui.main.MainActivity;

import javax.inject.Inject;

public class App extends Application {

    @Inject
    TimberInitializer timberInitializer;

    private AppComponent appComponent;

    // Global app visibility flag: true if app is really going to background instead of just switching activities
    private boolean notify = true;

    // Notification stuff
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .androidModule(new AndroidModule(this))
                .build();
        appComponent.inject(this);

        // Init Intents for notification
        Intent NotificationIntent = new Intent(this, MainActivity.class);
        PendingIntent NotificationPendingIntent = PendingIntent.getActivity(this, 0, NotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Init resources local var for notification text
        Resources res = getResources();

        // Init notification
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(res.getString(R.string.notif_title))
                .setContentText(res.getString(R.string.notif_text))
                .setContentIntent(NotificationPendingIntent)
                .setAutoCancel(true) // Hide the notification after its selected
                .setOngoing(true); // Set notification as ongoing so it can't be cleared
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean Notify) {
        notify = Notify;
    }

    // Raise notification if Notify flag is enabled
    public void sendNotif() {
        if (notify) {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    // Cancels existing notification, if any
    public void clearNotif() {
        notificationManager.cancel(0);
    }
}
