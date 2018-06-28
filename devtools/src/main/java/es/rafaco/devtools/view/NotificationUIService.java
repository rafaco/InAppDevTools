package es.rafaco.devtools.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.utils.UiUtils;

public class NotificationUIService extends Service {

    private static final String TAG = "FOREGROUND_SERVICE";
    private static final int NOTIFICATION_ID = 3456;
    private static final String CHANNEL_ID = "es.rafaco.devtools";
    private static final String GROUP_ID = "es.rafaco.devtools.foreground_service";
    private static final int SUMMARY_ID = 0;

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String ACTION_REPORT = "ACTION_REPORT";
    public static final String ACTION_SCREEN = "ACTION_SCREEN";
    public static final String ACTION_CLEAN = "ACTION_CLEAN";

    public NotificationUIService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "My foreground service onCreate().");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null)
        {
            String action = intent.getAction();

            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    //Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    //Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_SCREEN:
                    UiUtils.closeAllSystemWindows(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "You click SCREEN button.", Toast.LENGTH_LONG).show();
                    DevTools.takeScreenshot();
                    break;
                case ACTION_REPORT:
                    UiUtils.closeAllSystemWindows(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "You click REPORT button.", Toast.LENGTH_LONG).show();
                    DevTools.sendReport();
                    break;
                case ACTION_CLEAN:
                    UiUtils.closeAllSystemWindows(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "You click CLEAN button.", Toast.LENGTH_LONG).show();
                    DevTools.cleanSession();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /* Used to build and start foreground service. */
    private void startForegroundService()
    {
        Log.d(TAG, "Start foreground service.");

        createNotificationChannel();

        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = buildNotification(pendingIntent);

        //createNotificationGroup();
        //createNotificationSummary();

        // Start foreground service.
        startForeground(NOTIFICATION_ID, notification);
    }

    private Notification buildNotification(PendingIntent pendingIntent) {

        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), UiUtils.getAppIconResourceId());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setGroup(GROUP_ID)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(UiUtils.getAppIconResourceId())
                .setLargeIcon(largeIconBitmap)
                //.setFullScreenIntent(pendingIntent, true)
                .setContentTitle("Report tool is recording") //Collapsed Main
                .setContentText("Expand me to use it")   //Collapsed Second
                //.setSubText("setSubText")           //Group second
                .setWhen(System.currentTimeMillis());//Group third

        // Make notification show big text.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        //bigTextStyle.setSummaryText("DevTools activated, expand me");
        bigTextStyle.setBigContentTitle("Report tool is recording underneath");
        bigTextStyle.bigText("Press send report when ready and choose what to include. Take some screenshots before and clean logs to minimize analysis time.\n" + "On crash you will be automatically prompted");
        builder.setStyle(bigTextStyle);

        builder.addAction(buildAction(ACTION_REPORT));
        builder.addAction(buildAction(ACTION_SCREEN));
        builder.addAction(buildAction(ACTION_CLEAN));

        // Build the notification.
        return builder.build();
    }

    @NonNull
    private NotificationCompat.Action buildAction(String action) {
        int icon;
        String title;

        switch (action){
            case ACTION_SCREEN:
                title = "TAKE SCREEN";
                icon = R.drawable.ic_add_a_photo_rally_24dp;
                break;
            case ACTION_CLEAN:
                title = "CLEAN LOGS";
                icon = R.drawable.ic_delete_forever_rally_24dp;
                break;
            case ACTION_REPORT:
            default:
                title = "SEND REPORT";
                icon = R.drawable.ic_email_rally_24dp;
                break;
        }

        Intent pauseIntent = new Intent(this, NotificationUIService.class);
        pauseIntent.setAction(action);
        PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        return new NotificationCompat.Action(icon, title, pendingPrevIntent);
    }

    private void stopForegroundService()
    {
        Log.d(TAG, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if(channel==null){
                CharSequence name = "NotificationUIService";
                String description = "To show DevTools report notification";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void createNotificationGroup() {
        Notification summaryNotification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(UiUtils.getAppIconResourceId())
                .setContentTitle("Group Title")
                .setContentText("Group text")
                .setAutoCancel(true)
                //TODO: add DevTools icon
                //.setLargeIcon(emailObject.getSenderAvatar())
                .setGroup(GROUP_ID)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(SUMMARY_ID, summaryNotification);
    }

    private void createNotificationSummary() {
        Notification summaryNotification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Summary Title")
                        .setContentText("Summary text")
                        .setSmallIcon(UiUtils.getAppIconResourceId())
                        //build summary info into InboxStyle template
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine("Alex Faarborg  Check this out")
                                .addLine("Jeff Chang    Launch Party")
                                .setBigContentTitle("2 new messages")
                                .setSummaryText("janedoe@example.com"))
                        //specify which group this notification belongs to
                        .setGroup(GROUP_ID)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(SUMMARY_ID, summaryNotification);
    }
}