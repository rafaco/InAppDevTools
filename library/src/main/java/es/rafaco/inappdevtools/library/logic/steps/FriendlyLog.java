package es.rafaco.inappdevtools.library.logic.steps;

import android.graphics.Color;
import android.util.Log;

//#ifdef MODERN
import androidx.annotation.NonNull;
//#else
//@import android.support.annotation.NonNull;
//#endif

import com.readystatesoftware.chuck.internal.data.HttpTransaction;

import java.util.Date;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Anr;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;

public class FriendlyLog {
    public static final String TAG = "FriendlyLog";

    public enum LEVEL { V, D, I, W, E, F, WTF }

    public static void log(String message){
        log("I", "Log", "Quick", message);
    }

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

    public static void log(String severity, String category, String type, String message, String extra) {
        log(new Date().getTime(), severity, category, type, message, extra);
    }

    public static void log(long date, String severity, String category, String type, String message, String extra) {
        final Friendly log = new Friendly();
        log.setDate(date);
        log.setSeverity(severity);
        log.setCategory(category);
        log.setType(type);
        log.setMessage(message);
        log.setExtra(extra);

        logAtLogcat(log);
        insertOnBackground(log);
    }

    private static void logAtLogcat(Friendly log) {
        String text = getFormatted(log);
        switch (log.getSeverity()){
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

            case "V":
            default:
                Log.v(TAG, text);
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
        return String.format("[%s-%s] %s", log.getCategory(), log.getType(), log.getMessage());
    }

    public static int getColor(Friendly log) {
        String type = log.getSeverity();
        int color = Color.WHITE;
        if(type.equals("V")){
            color = R.color.rally_blue;
        }
        if(type.equals("D")){
            color = R.color.rally_blue_med;
        }
        else if(type.equals("I")){
            color = R.color.rally_green;
        }
        else if(type.equals("W")){
            color = R.color.rally_yellow;
        }
        else if(type.equals("E") || type.equals("F")){
            color = R.color.rally_orange;
        }

        return color;
    }

    public static int getIcon(Friendly log) {
        if (log.getCategory().equals("App")) {
            if (log.getType().equals("Foreground")) {
                return R.drawable.ic_flip_to_front_white_24dp;
            } else if (log.getType().equals("Background")) {
                return R.drawable.ic_flip_to_back_white_24dp;
            } else if (log.getType().equals("Restart")) {
                return R.drawable.ic_replay_white_24dp;
            } else if (log.getType().equals("Start")) {
                return R.drawable.ic_flag_white_24dp;
            } else if (log.getType().equals("FirstStart")) {
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
                return R.drawable.ic_phonelink_ring_white_24dp;
            }
            else if (log.getType().equals("ScreenOff")) {
                return R.drawable.ic_phonelink_erase_white_24dp;
            }
            else if (log.getType().equals("UserPresent")) {
                return R.drawable.ic_person_pin_white_24dp;
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
                return R.drawable.ic_arrow_back_white_24dp;
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
            else if (log.getType().equals("Shake")){
                return R.drawable.ic_vibration_white_24dp;
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
                return R.drawable.ic_add_a_photo_white_24dp;
            }
            else if (log.getType().equals("Exception")){
                return R.drawable.ic_bug_report_white_24dp;
            }
            else if (log.getType().equals("Delete")){
                return R.drawable.ic_delete_forever_white_24dp;
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
                overview = transaction.getResponseMessage() + "(" + transaction.getResponseCode() + ")";
        }
        log.setMessage(overview + " from " + transaction.getPath());
        log.setType(transaction.getStatus().name());

        if (log.getType().equals(HttpTransaction.Status.Failed.name())){
            log.setSeverity("E");
        }else if (log.getType().equals(HttpTransaction.Status.Complete.name())){
            log.setSeverity("I");
        }

        logAtLogcat(log);
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                DevTools.getDatabase().friendlyDao().update(log);
            }
        });
    }

    public static void logException(String message, Throwable e) {
        log("W", "DevTools", "Exception",
                message + " -> " + e.getMessage(),
                Log.getStackTraceString(e));
    }
}
