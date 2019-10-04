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

//#ifdef ANDROIDX
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
import es.rafaco.inappdevtools.library.logic.info.reporters.BuildInfoReporter;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.prefs.utils.FirstStartUtil;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PendingCrashUtil;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.logic.info.reporters.AppInfoReporter;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class NotificationService extends Service {

    private static final int NOTIFICATION_ID = 3002;
    private static final String GROUP_ID = "es.rafaco.iadt.foreground_service";
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

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (IadtController.get().isDebug())
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
                    OverlayService.performNavigation(ReportScreen.class);
                    //Iadt.startReportDialog();
                    break;
                case ACTION_CLEAN:
                    bringAppToFront();
                    Toast.makeText(getApplicationContext(), "You click CLEAN button.", Toast.LENGTH_LONG).show();
                    IadtController.cleanSession();
                    break;
                case ACTION_TOOLS:
                    bringAppToFront();
                    IadtController.get().showMain();
                    break;
            }
        }
        return Service.START_NOT_STICKY;
    }

    private void bringAppToFront() {
        //TODO: research a better way to close the headup notification
        //UiUtils.closeAllSystemWindows(getApplicationContext());
        Intent app = AppUtils.getAppLauncherIntent(getApplicationContext());
        getApplicationContext().startActivity(app);
    }

    private void startForegroundService() {
        if (IadtController.get().isDebug())
            Log.d(Iadt.TAG, "Start foreground service.");

        createNotificationChannels();

        /*Intent intent = AppUtils.getAppLauncherIntent(getApplicationContext());
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                (int)new Date().getTime(), intent, 0);*/

        Intent intent = new Intent(this, NotificationService.class);
        intent.setAction(ACTION_TOOLS);
        PendingIntent pendingIntent = PendingIntent.getService(this,
                (int)new Date().getTime(), intent, 0);


        Notification notification = buildMainNotification(pendingIntent,
                PendingCrashUtil.isSessionFromPending() ? new Crash() : null);

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
    private static NotificationService instance;

    public static void stop(){
        if (instance != null) instance.stopService();
    }
    //endregion

    private void stopService() {
        if (IadtController.get().isDebug())
            Log.d(Iadt.TAG, "Stopping NotificationService");
        stopForeground(true);
        stopSelf();
        instance = null;
    }


    //region [ NOTIFICATION ]

    private Notification buildMainNotification(PendingIntent pendingIntent, Crash crash) {

        String overview;
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(),
                UiUtils.getAppIconResourceId());
        AppInfoReporter appInfo = new AppInfoReporter(getApplicationContext());
        BuildInfoReporter buildInfo = new BuildInfoReporter(getApplicationContext());
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
            title = "Restarted from crashed, please report";
            subTitle = "Expand me for options...";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setGroup(GROUP_ID)
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

        builder.setChannelId(getCurrentChannel().getId());
        Log.w("RAFA", "Notification channel: " + getCurrentChannel());
        if (FirstStartUtil.isSessionFromFirstStart()){
            Log.w("RAFA", "Notification isFirstStart");
            builder.setDefaults(Notification.DEFAULT_VIBRATE)
                    .setPriority(Notification.PRIORITY_MAX);
        }
        else if (crash != null){
            Log.w("RAFA", "Notification crash");
            builder.setDefaults(Notification.DEFAULT_VIBRATE)
                    .setPriority(Notification.PRIORITY_MIN);
        }
        else{
            Log.w("RAFA", "Notification not isFirstStart");
            builder.setDefaults(Notification.DEFAULT_LIGHTS)
                    .setPriority(Notification.PRIORITY_MIN);
        }
        
        if (crash == null){
            Log.w("RAFA", "Notification not crash");
            builder.setColor(getResources().getColor(R.color.rally_blue_med));
        }
        else{
            Log.w("RAFA", "Notification crash");
            builder.setColor(getResources().getColor(R.color.rally_orange));
        }

        // Make notification showMain big text.
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

    private IadtChannel getCurrentChannel() {
        if (FirstStartUtil.isSessionFromFirstStart())
            return IadtChannel.CHANNEL_PRIORITY;
        else if (PendingCrashUtil.isSessionFromPending())
            return IadtChannel.CHANNEL_STANDARD;
        else
            return IadtChannel.CHANNEL_SILENT;
    }

    //TODO: delete?
    private Notification buildCrashNotification(PendingIntent pendingIntent) {

        AppInfoReporter infoHelper = new AppInfoReporter(getApplicationContext());
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_error_orange_24dp);
        String title = String.format("Ups, %s crashed", infoHelper.getAppName());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getCurrentChannel().getId())
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

        // Make notification showMain big text.
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

        Intent intent = new Intent(this, NotificationService.class);
        intent.setAction(action);
        PendingIntent pendingPrevIntent = PendingIntent.getService(this,
                (int)new Date().getTime(), intent, 0);

        //TODO: skipped icon
        return new NotificationCompat.Action(0, title, pendingPrevIntent);
    }

    private void createNotificationChannels() {
        createNotificationChannel(IadtChannel.CHANNEL_PRIORITY);
        createNotificationChannel(IadtChannel.CHANNEL_STANDARD);
        createNotificationChannel(IadtChannel.CHANNEL_SILENT);
    }

    private void createNotificationChannel(IadtChannel channelData) {
        // Create the IadtChannel, but only on API 26+ because
        // the IadtChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = notificationManager.getNotificationChannel(channelData.getId());
            if(channel==null){
                channel = new NotificationChannel(channelData.getId(), channelData.getName(), channelData.getPriority());
                if (channelData.getPriority() == NotificationManager.IMPORTANCE_LOW){
                    channel.setSound(null, null);
                }
                channel.setDescription(channelData.getDescription());
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void createNotificationGroup() {
        Notification summaryNotification = new NotificationCompat.Builder(this, getCurrentChannel().getId())
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
                new NotificationCompat.Builder(this, getCurrentChannel().getId())
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
