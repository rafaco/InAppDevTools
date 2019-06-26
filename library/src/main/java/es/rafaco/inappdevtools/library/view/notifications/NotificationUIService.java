package es.rafaco.inappdevtools.library.view.notifications;

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
import android.util.Log;
import android.widget.Toast;

//#ifdef MODERN
//@import androidx.annotation.NonNull;
//@import androidx.core.app.NotificationCompat;
//@import androidx.core.app.NotificationManagerCompat;
//#else
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
//#endif

import java.util.Date;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PendingCrashUtil;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.AppInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.BuildInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class NotificationUIService extends Service {

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

    String testerText = "Speak to developer's team!\nFor bug reports try to reproduce it while grabbing screens, then press REPORT just after the issue happen. We are recording everything underneath to understand what went wrong.";
    String developerText = "Inspect your running app on the go and report your findings for further analysis";

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
        Log.d(Iadt.TAG, "My foreground service onCreate().");
        instance = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            String action = intent.getAction();

            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                case ACTION_DISMISS:
                    stopService();
                    break;


                case ACTION_SCREEN:
                    bringAppToFront();
                    Iadt.takeScreenshot();
                    break;
                case ACTION_REPORT:
                    bringAppToFront();
                    OverlayUIService.performNavigation(ReportScreen.class);
                    //Iadt.startReportDialog();
                    break;
                case ACTION_CLEAN:
                    bringAppToFront();
                    Toast.makeText(getApplicationContext(), "You click CLEAN button.", Toast.LENGTH_LONG).show();
                    IadtController.cleanSession();
                    break;
                case ACTION_TOOLS:
                    bringAppToFront();
                    IadtController.get().showOverlay(false);
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void bringAppToFront() {
        //TODO: research a better way to close the headup notification
        //UiUtils.closeAllSystemWindows(getApplicationContext());
        Intent app = AppUtils.getAppLauncherIntent(getApplicationContext());
        getApplicationContext().startActivity(app);
    }

    private void startForegroundService() {
        Log.d(Iadt.TAG, "Start foreground service.");

        createNotificationChannel();

        /*Intent intent = AppUtils.getAppLauncherIntent(getApplicationContext());
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                (int)new Date().getTime(), intent, 0);*/

        Intent intent = new Intent(this, NotificationUIService.class);
        intent.setAction(ACTION_TOOLS);
        PendingIntent pendingIntent = PendingIntent.getService(this,
                (int)new Date().getTime(), intent, 0);


        Notification notification = buildMainNotification(pendingIntent,
                PendingCrashUtil.isPending() ? new Crash() : null);

        //createNotificationGroup();
        //createNotificationSummary();
        /*if (PendingCrashUtil.isPending()){
            Notification crashNotification = buildCrashNotification(pendingIntent);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify((int)new Date().getTime(), crashNotification);

            PendingCrashUtil.clearPending();
        }*/

        startForeground(NOTIFICATION_ID, notification);
    }

    //region [ STATIC STOP ]

    //TODO: [LOW:Arch] Replace by bounded service
    private static NotificationUIService instance;

    public static void stop(){
        if (instance != null) instance.stopService();
    }
    //endregion

    private void stopService() {
        Log.d(Iadt.TAG, "Stopping NotificationUIService");
        stopForeground(true);
        stopSelf();
        instance = null;
    }


    //region [ NOTIFICATION ]

    private Notification buildMainNotification(PendingIntent pendingIntent, Crash crash) {

        String overview;
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(),
                UiUtils.getAppIconResourceId());
        AppInfoHelper appInfo = new AppInfoHelper(getApplicationContext());
        BuildInfoHelper buildInfo = new BuildInfoHelper(getApplicationContext());
        overview = appInfo.getAppNameAndVersions() + "\n"
                + buildInfo.getFriendlyBuildType();
        if (buildInfo.isGitEnabled()){
            overview += " from " + buildInfo.getRepositoryOverview();
        }
        //overview += ", " + buildInfo.getFriendlyElapsedTime();

        String title, subTitle;
        if (crash == null){
            title = "Open developer tools";
            subTitle = overview;
        }else{
            title = "Ups, your app crashed";
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
                .setColorized(false)
                .setContentIntent(pendingIntent)
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
        bigTextStyle.bigText(overview);
        builder.setStyle(bigTextStyle);

        //builder.addAction(buildAction(ACTION_TOOLS));
        //builder.addAction(buildAction(ACTION_CLEAN));

        builder.addAction(buildAction(ACTION_DISMISS));
        builder.addAction(buildAction(ACTION_SCREEN));
        builder.addAction(buildAction(ACTION_REPORT));

        return builder.build();
    }

    //TODO: delete?
    private Notification buildCrashNotification(PendingIntent pendingIntent) {

        AppInfoHelper infoHelper = new AppInfoHelper(getApplicationContext());
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_error_orange_24dp);
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
                title = "SCREENSHOT";
                icon = R.drawable.ic_add_a_photo_white_24dp;
                break;
            case ACTION_REPORT:
                title = "REPORT";
                icon = R.drawable.ic_send_white_24dp;
                break;
            case ACTION_CLEAN:
                title = "CLEAN";
                icon = R.drawable.ic_delete_forever_white_24dp;
                break;
            case ACTION_TOOLS:
                title = "TOOLS";
                icon = R.drawable.ic_developer_mode_white_24dp;
                break;
            case ACTION_DISMISS:
            default:
                title = "DISMISS";
                icon = R.drawable.ic_close_white_24dp;
                break;
        }

        Intent intent = new Intent(this, NotificationUIService.class);
        intent.setAction(action);
        PendingIntent pendingPrevIntent = PendingIntent.getService(this,
                (int)new Date().getTime(), intent, 0);

        //TODO: skipped icon
        return new NotificationCompat.Action(0, title, pendingPrevIntent);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if(channel==null){
                CharSequence name = "NotificationUIService";
                String description = "To show Iadt report notification";
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
                //TODO: add Iadt icon
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

    //endregion
}
