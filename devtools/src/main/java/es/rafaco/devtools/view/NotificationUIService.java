package es.rafaco.devtools.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
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
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.logic.crash.PendingCrashUtil;
import es.rafaco.devtools.utils.UiUtils;
import es.rafaco.devtools.view.overlay.tools.info.InfoHelper;

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
    public static final String ACTION_TOOLS = "ACTION_TOOLS";
    public static final String ACTION_DISMISS = "ACTION_DISMISS";

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
                    DevTools.takeScreenshot();
                    break;
                case ACTION_REPORT:
                    UiUtils.closeAllSystemWindows(getApplicationContext());
                    DevTools.startReportDialog();
                    break;
                case ACTION_CLEAN:
                    UiUtils.closeAllSystemWindows(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "You click CLEAN button.", Toast.LENGTH_LONG).show();
                    DevTools.cleanSession();
                    break;
                case ACTION_TOOLS:
                    UiUtils.closeAllSystemWindows(getApplicationContext());
                    //TODO: research a better way to close the headup notification
                    //Intent app = AppUtils.getAppLauncherIntent(getApplicationContext());
                    //getApplicationContext().startActivity(app);
                    DevTools.openTools(false);
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

        Notification notification = buildMainNotification(pendingIntent,
                PendingCrashUtil.isPending() ? new Crash() : null);

        //createNotificationGroup();
        //createNotificationSummary();

        // Start foreground service.
        startForeground(NOTIFICATION_ID, notification);

        if (PendingCrashUtil.isPending()){
            //Notification crashNotification = buildCrashNotification(pendingIntent);
            //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            //notificationManager.notify((int)new Date().getTime(), crashNotification);

            //PendingCrashUtil.clearPending();
        }
    }

    private Notification buildMainNotification(PendingIntent pendingIntent, Crash crash) {

        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), UiUtils.getAppIconResourceId());
        InfoHelper infoHelper = new InfoHelper(getApplicationContext());
        PackageInfo packageInfo = infoHelper.getPackageInfo();
        String title, subTitle;
        if (crash == null){
            String environment = "DEBUG";
            String version = packageInfo.versionName + " (" + packageInfo.versionCode + ")";

            title = String.format("%s %s %s", infoHelper.getAppName(), environment, version);
            subTitle = "Expand me for options...";
        }else{
            title = String.format("Ups, %s crashed", infoHelper.getAppName());
            subTitle = "Expand me for options...";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setGroup(GROUP_ID)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(Notification.VISIBILITY_PRIVATE)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_bug_report_white_24dp)
                .setLargeIcon(largeIconBitmap)
                .setColorized(true)
                //.setFullScreenIntent(pendingIntent, true)
                .setContentTitle(title) //Collapsed Main
                .setContentText(subTitle)   //Collapsed Second
                //.setSubText("setSubText")           //Group second
                .setWhen(System.currentTimeMillis()); //Group third

        if (crash == null){
            builder.setColor(getResources().getColor(R.color.rally_blue_med));
        }else{
            builder.setColor(getResources().getColor(R.color.rally_orange));
        }

        // Make notification show big text.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText("Speak to developer's team!\nFor bug reports try to reproduce it while grabbing screens, then press REPORT just after the issue happen. We are recording everything underneath to understand what went wrong.");
        builder.setStyle(bigTextStyle);

        if (crash == null)
            builder.addAction(buildAction(ACTION_SCREEN));
        else
            builder.addAction(buildAction(ACTION_DISMISS));

        builder.addAction(buildAction(ACTION_REPORT));
        builder.addAction(buildAction(ACTION_TOOLS));

        if (crash == null)
            builder.addAction(buildAction(ACTION_CLEAN));

        // Build the notification.
        return builder.build();
    }

    private Notification buildCrashNotification(PendingIntent pendingIntent) {

        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_error_orange_24dp);
        InfoHelper infoHelper = new InfoHelper(getApplicationContext());
        String title = String.format("Ups, %s crashed", infoHelper.getAppName());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setGroup(GROUP_ID)
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(Notification.VISIBILITY_PRIVATE)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_bug_report_white_24dp)
                .setLargeIcon(largeIconBitmap)
                .setColor(getResources().getColor(R.color.rally_orange))
                .setColorized(true)
                //.setFullScreenIntent(pendingIntent, true)
                .setContentTitle(title) //Collapsed Main
                .setContentText("Expand me for options...")   //Collapsed Second
                //.setSubText("setSubText")           //Group second
                .setWhen(System.currentTimeMillis()); //Group third

        // Make notification show big text.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText("Speak to developer's team!\nFor bug reports try to reproduce it while grabbing screens, then press REPORT just after the issue happen. We are recording everything underneath to understand what went wrong.");
        builder.setStyle(bigTextStyle);

        builder.addAction(buildAction(ACTION_REPORT));
        builder.addAction(buildAction(ACTION_TOOLS));

        // Build the notification.
        return builder.build();
    }

    @NonNull
    private NotificationCompat.Action buildAction(String action) {
        int icon;
        String title;

        switch (action){
            case ACTION_SCREEN:
                title = "SCREEN";
                icon = R.drawable.ic_add_a_photo_rally_24dp;
                break;
            case ACTION_REPORT:
                title = "REPORT";
                icon = R.drawable.ic_email_rally_24dp;
                break;
            case ACTION_CLEAN:
                title = "CLEAN";
                icon = R.drawable.ic_delete_forever_rally_24dp;
                break;
            case ACTION_TOOLS:
            default:
                title = "<TOOLS>";
                icon = R.drawable.ic_more_vert_rally_24dp;
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
                .setPriority(Notification.PRIORITY_MAX)
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
                        .setGroup(GROUP_ID)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setGroupSummary(true)
                        .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(SUMMARY_ID, summaryNotification);
    }
}