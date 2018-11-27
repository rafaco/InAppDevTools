package es.rafaco.devtools.logic.steps;

import android.graphics.Color;
import androidx.annotation.NonNull;
import android.util.Log;

import com.readystatesoftware.chuck.internal.data.HttpTransaction;

import java.util.Date;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.utils.DateUtils;
import es.rafaco.devtools.logic.utils.ThreadUtils;
import es.rafaco.devtools.storage.db.entities.Anr;
import es.rafaco.devtools.storage.db.entities.Crash;
import es.rafaco.devtools.storage.db.entities.Friendly;

public class FriendlyLog {
    public static String TAG = "FriendlyLog";

    public enum LEVEL { V, D, I, W, E, F, WTF }

    public static void log(String severity, String category, String type, String message) {
        log(new Date().getTime(), severity, category, type, message);
    }

    public static void log(long date, String severity, String category, String type, String message) {
        final Friendly log = new Friendly();
        log.setDate(date);
        log.setSeverity(severity);
        log.setCategory(category);
        log.setType(type);
        log.setMessage(message);

        logAtLogcat(log);
        insertOnBackground(log);
    }

    private static void logAtLogcat(Friendly log) {
        String text = getFormatted(log);
        switch (log.getSeverity()){
            case "V":
                Log.v(TAG, text);
                break;
            case "D":
                Log.d(TAG, text);
                break;
            case "I":
                Log.i(TAG, text);
                break;
            case "W":
                Log.w(TAG, text);
                break;
            case "E":
                Log.e(TAG, text);
                break;
            case "F":
                Log.wtf(TAG, text);
                break;
        }

    }

    private static void insertOnBackground(final Friendly log) {
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                DevTools.getDatabase().friendlyDao().insert(log);
            }
        });
    }

    @NonNull
    private static String getFormatted(Friendly log) {
        Date date = new Date(log.getDate());

        return String.format("[%s:%s-%s] %s", DateUtils.formatPrecisionTime(log.getDate()), log.getCategory(), log.getType(), log.getMessage());
    }

    public static int getColor(Friendly log) {
        String type = log.getSeverity();
        int color = Color.WHITE;
        if(type.equals("V")){
            color = R.color.rally_blue; //Color.rgb(0, 0, 200);
        }
        if(type.equals("D")){
            color = R.color.rally_blue_med; //Color.rgb(0, 0, 200);
        }
        else if(type.equals("I")){
            color = R.color.rally_green; //Color.rgb(0, 128, 0);
        }
        else if(type.equals("W")){
            color = R.color.rally_yellow; //Color.parseColor("#827717");//rgb(255, 234, 0);
        }
        else if(type.equals("E") || type.equals("F")){
            color = R.color.rally_orange; //Color.rgb(255, 0, 0);
        }

        return color;
    }

    public static int getIcon(Friendly log) {
        if (log.getCategory().equals("App")) {
            if (log.getType().equals("Foreground")) {
                return R.drawable.ic_flip_to_front_white_24dp;
            } else if (log.getType().equals("Background")) {
                return R.drawable.ic_flip_to_back_white_24dp;
            } else if (log.getType().equals("Restarted")) {
                return R.drawable.ic_replay_white_24dp;
            } else if (log.getType().equals("Startup")) {
                return R.drawable.ic_flag_white_24dp;
            } else if (log.getType().equals("FirstStartup")) {
                return R.drawable.ic_fiber_new_white_24dp;
            } else if (log.getType().equals("Navigation")) {
                return R.drawable.ic_view_carousel_white_24dp;
            }
            else if (log.getType().equals("TaskRemoved")) {
                return R.drawable.ic_close_white_24dp;
            }
        }
        else if (log.getCategory().equals("Device")) {
            if (log.getType().equals("Portrait")) {
                return R.drawable.ic_portrait_white_24dp;
            }
            else if (log.getType().equals("Landscape")) {
                return R.drawable.ic_landscape_white_24dp;
            }
            else if (log.getType().equals("ScreenOn")) {
                return R.drawable.ic_phonelink_ring_black_24dp;
            }
            else if (log.getType().equals("ScreenOff")) {
                return R.drawable.ic_phonelink_erase_black_24dp;
            }
            else if (log.getType().equals("UserPresent")) {
                return R.drawable.ic_person_pin_black_24dp;
            }
        }
        else if (log.getCategory().equals("UserTouch")) {
            return R.drawable.ic_touch_app_white_24dp;
        }
        else if (log.getCategory().equals("User")){
            if (log.getType().equals("Touch")){
                return R.drawable.ic_touch_app_white_24dp;
            }
            else if (log.getType().equals("BackKey")){
                return R.drawable.ic_arrow_back_rally_24dp;
            }
            else if (log.getType().equals("HomeKey")){
                return R.drawable.ic_circle_white_24dp;
            }
            else if (log.getType().equals("RecentKey")){
                return R.drawable.ic_square_white_24dp;
            }
            else if (log.getType().equals("DreamKey")){
                return R.drawable.ic_power_white_24dp;
            }
        }
        else if (log.getCategory().equals("Process")){
            return R.drawable.ic_application_white_24dp;
        }
        else if (log.getCategory().equals("Run")){
            return R.drawable.ic_run_white_24dp;
        }
        else if (log.getCategory().equals("Activity")){
            return R.drawable.ic_activity_white_24dp;
        }
        else if (log.getCategory().equals("Fragment")){
            return R.drawable.ic_extension_white_24dp;
        }
        else if (log.getCategory().equals("Message")){
            return R.drawable.ic_message_white_24dp;
        }
        else if (log.getCategory().equals("Network")){
            if (log.getType().equals("Requested")){
                return R.drawable.ic_cloud_queue_white_24dp;
            }
            else if (log.getType().equals("Complete")){
                return R.drawable.ic_cloud_done_white_24dp;
            }
            else if (log.getType().equals("Failed")){
                return R.drawable.ic_cloud_off_white_24dp;
            }
            else if (log.getType().equals("Connected")){
                return R.drawable.ic_wifi_tethering_white_24dp;
            }
            else if (log.getType().equals("Disconnected")){
                return R.drawable.ic_portable_wifi_off_white_24dp;
            }
            else if (log.getType().equals("AirplaneOn")){
                return R.drawable.ic_airplanemode_active_white_24dp;
            }
            else if (log.getType().equals("AirplaneOff")){
                return R.drawable.ic_airplanemode_inactive_white_24dp;
            }
        }
        else if (log.getCategory().equals("Error")){
            if (log.getType().equals("Crash")){
                return R.drawable.ic_error_white_24dp;
            }else{
                return R.drawable.ic_block_white_24dp;
            }
        }else if (log.getType().equals("Breakpoint")){
            return R.drawable.ic_pan_tool_white_24dp;
        }

        else if (log.getCategory().equals("DevTools")){
            if (log.getType().equals("Screenshot")){
                return R.drawable.ic_add_a_photo_rally_24dp;
            }
            return R.drawable.ic_developer_mode_white_24dp;
        }
        return -1;
    }

    public static long logCrash(String message) {
        final Friendly log = new Friendly();
        log.setDate(new Date().getTime());
        log.setSeverity("E");
        log.setCategory("Error");
        log.setType("Crash");
        log.setMessage(message);
        logAtLogcat(log);
        return DevTools.getDatabase().friendlyDao().insert(log);
    }

    public static void logCrashDetails(long friendlyLogId, long crashId, Crash crash) {
        final Friendly log = DevTools.getDatabase().friendlyDao().findById(friendlyLogId);
        log.setDate(crash.getDate());
        log.setMessage(crash.getMessage());
        log.setLinkedId(crashId);
        DevTools.getDatabase().friendlyDao().update(log);
    }

    public static void logAnr(long anrId, Anr anr) {
        final Friendly log = new Friendly();
        log.setDate(anr.getDate());
        log.setSeverity("E");
        log.setCategory("Error");
        log.setType("Anr");
        log.setMessage(anr.getMessage());
        log.setLinkedId(anrId);
        logAtLogcat(log);
        insertOnBackground(log);
    }

    public static void logNetworkRequest(HttpTransaction transaction) {
        final Friendly log = new Friendly();
        log.setDate(transaction.getRequestDate().getTime());
        log.setSeverity("D");
        log.setCategory("Network");
        log.setType(transaction.getStatus().name());
        log.setMessage("Request to " + transaction.getPath());
        log.setLinkedId(transaction.getId());

        logAtLogcat(log);
        insertOnBackground(log);
    }

    public static void logNetworkUpdate(HttpTransaction transaction) {
        final Friendly log = DevTools.getDatabase().friendlyDao().findByLinkedId(transaction.getId());
        if (log == null){
            DevTools.showMessage("Unable to link the network request");
            return;
        }

        String overview;
        switch (transaction.getStatus()) {
            case Failed:
                overview = transaction.getError();
                break;
            case Requested:
                overview = "...";
                break;
            default:
                overview = transaction.getResponseMessage() + "(" + String.valueOf(transaction.getResponseCode()) + ")";
        }
        log.setMessage(overview + " from " + transaction.getPath());
        log.setType(transaction.getStatus().name());

        if (log.getType().equals(HttpTransaction.Status.Failed.name())){
            log.setSeverity("E");
        }else if (log.getType().equals(HttpTransaction.Status.Complete.name())){
            log.setSeverity("I");
        }

        logAtLogcat(log);
        ThreadUtils.runOnBackThread(() -> DevTools.getDatabase().friendlyDao().update(log));
    }
}
