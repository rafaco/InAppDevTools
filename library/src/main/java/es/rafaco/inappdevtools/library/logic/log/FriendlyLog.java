/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.logic.log;

import android.graphics.Color;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import java.util.Date;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Anr;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.NetSummary;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetFormatter;

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
        log.setSubcategory(type);
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
        log.setSubcategory(type);
        log.setMessage(message);
        log.setExtra(extra);

        logAtLogcat(log);
        insertOnBackground(log);
    }

    private static void logAtLogcat(Friendly log) {

        //TODO: use a configuration flag
        // Or print always and filter at log screen
        if (!Iadt.isDebug()){
            return;
        }

        runLogMethod(log);
    }

    private static void runLogMethod(Friendly log) {
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

    public static String convertCharToLongString(String logLevelChar) {

        switch (logLevelChar) {
            case "V":
                return "VERBOSE";
            case "D":
                return "DEBUG";
            case "I":
                return "INFO";
            case "W":
                return "WARN";
            case "E":
                return "ERROR";
            case "F":
                return "FAIL";
        }
        return ("UNKNOWN");
    }

    private static void insertOnBackground(final Friendly log) {
        ThreadUtils.runOnBack("Iadt-AddFriendlyLog",
                new Runnable() {
            @Override
            public void run() {
                IadtController.get().getDatabase().friendlyDao().insert(log);
            }
        });
    }

    @NonNull
    private static String getFormatted(Friendly log) {
        return String.format("[%s-%s] %s", log.getCategory(), log.getSubcategory(), log.getMessage());
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

        //TODO: Refactor needed, it's a mess
        //TODO: check log.getSubcategory()!=null before equals

        if (log.getCategory().equals("App")) {
            if (log.getSubcategory().equals("Foreground")) {
                return R.drawable.ic_flip_to_front_white_24dp;
            }
            else if (log.getSubcategory().equals("Background")) {
                return R.drawable.ic_flip_to_back_white_24dp;
            }
            else if (log.getSubcategory().equals("FirstStart")) {
                return R.drawable.ic_fiber_new_white_24dp;
            }
            else if (log.getSubcategory().equals("Start")) {
                return R.drawable.ic_flag_white_24dp;
            }
            else if (log.getSubcategory().equals("Navigation")) {
                return R.drawable.ic_view_carousel_white_24dp;
            }
            else if (log.getSubcategory().equals("TaskRemoved")) {
                return R.drawable.ic_close_white_24dp;
            }
            else if (log.getSubcategory().equals("Restart")) {
                return R.drawable.ic_replay_white_24dp;
            }
            else if (log.getSubcategory().equals("ForceStop")) {
                return R.drawable.ic_warning_white_24dp;
            }
        }
        else if (log.getCategory().equals("Device")) {
            if (log.getSubcategory().equals("Portrait")) {
                return R.drawable.ic_portrait_white_24dp;
            }
            else if (log.getSubcategory().equals("Landscape")) {
                return R.drawable.ic_landscape_white_24dp;
            }
            else if (log.getSubcategory().equals("ScreenOn")) {
                return R.drawable.ic_phonelink_ring_white_24dp;
            }
            else if (log.getSubcategory().equals("ScreenOff")) {
                return R.drawable.ic_phonelink_erase_white_24dp;
            }
            else if (log.getSubcategory().equals("UserPresent")) {
                return R.drawable.ic_person_pin_white_24dp;
            }
            else if (log.getSubcategory().startsWith("Power")) {
                return R.drawable.ic_battery_alert_white_24dp;
            }
        }
        else if (log.getCategory().equals("Touch")) {
            return R.drawable.ic_touch_app_white_24dp;
        }
        else if (log.getCategory().equals("User")){
            if (log.getSubcategory().equals("Action")){
                return R.drawable.ic_touch_app_white_24dp;
            }
            else if (log.getSubcategory().equals("BackKey")){
                return R.drawable.ic_arrow_back_white_24dp;
            }
            else if (log.getSubcategory().equals("HomeKey")){
                return R.drawable.ic_circle_white_24dp;
            }
            else if (log.getSubcategory().equals("RecentKey")){
                return R.drawable.ic_square_white_24dp;
            }
            else if (log.getSubcategory().equals("DreamKey")){
                return R.drawable.ic_power_white_24dp;
            }
            else if (log.getSubcategory().equals("Shake")){
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
            if (log.getSubcategory().equals("Requesting")){
                return R.drawable.ic_cloud_queue_white_24dp;
            }
            else if (log.getSubcategory().equals("Complete")){
                return R.drawable.ic_cloud_done_white_24dp;
            }
            else if (log.getSubcategory().equals("Error")){
                return R.drawable.ic_cloud_off_white_24dp;
            }
            else if (log.getSubcategory().equals("Connected")){
                return R.drawable.ic_wifi_tethering_white_24dp;
            }
            else if (log.getSubcategory().equals("Disconnected")){
                return R.drawable.ic_portable_wifi_off_white_24dp;
            }
            else if (log.getSubcategory().equals("AirplaneOn")){
                return R.drawable.ic_airplanemode_active_white_24dp;
            }
            else if (log.getSubcategory().equals("AirplaneOff")){
                return R.drawable.ic_airplanemode_inactive_white_24dp;
            }
        }
        else if (log.getCategory().equals("Error")){
            if (log.getSubcategory().equals("Crash")){
                return R.drawable.ic_bug_report_white_24dp;
            }else{
                return R.drawable.ic_block_white_24dp;
            }
        }else if (log.getSubcategory().equals("Codepoint")){
            return R.drawable.ic_pan_tool_white_24dp;
        }

        else if (log.getCategory().equals("Iadt")){
            if (log.getSubcategory().equals("Init")){
                return R.drawable.ic_new_releases_white_24dp;
            }
            else if (log.getSubcategory().equals("NewBuild")) {
                return R.drawable.ic_build_white_24dp;
            }
            else if (log.getSubcategory().equals("Navigation")){
                return R.drawable.ic_location_on_white_24dp;
            }
            else if (log.getSubcategory().equals("Hidden")){
                return R.drawable.ic_visibility_off_white_24dp;
            }
            else if (log.getSubcategory().equals("Screenshot")){
                return R.drawable.ic_add_a_photo_white_24dp;
            }
            else if (log.getSubcategory().equals("Exception")){
                return R.drawable.ic_error_white_24dp;
            }
            else if (log.getSubcategory().equals("Config")){
                return R.drawable.ic_settings_white_24dp;
            }
            else if (log.getSubcategory().equals("Delete")){
                return R.drawable.ic_delete_forever_white_24dp;
            }
            return R.drawable.ic_developer_mode_white_24dp;
        }
        else if (log.getCategory().equals("Log")){
            if (log.getSubcategory().equals("Quick")){
                return R.drawable.ic_message_white_24dp;
            }
            return R.drawable.ic_android_white_24dp;
        }
        return -1;
    }

    public static long logCrash(String message) {
        final Friendly log = new Friendly();
        log.setDate(new Date().getTime());
        log.setSeverity("E");
        log.setCategory("Error");
        log.setSubcategory("Crash");
        log.setMessage(message);
        logAtLogcat(log);
        return IadtController.get().getDatabase().friendlyDao().insert(log);
    }

    public static void logCrashDetails(long friendlyLogId, long crashId, Crash crash) {
        final Friendly log = IadtController.get().getDatabase().friendlyDao().findById(friendlyLogId);
        log.setDate(crash.getDate());
        log.setMessage("Session " + crash.getScreenId() + " crashed: " + crash.getMessage());
        log.setLinkedId(crashId);
        IadtController.get().getDatabase().friendlyDao().update(log);
    }

    public static void logAnr(long anrId, Anr anr) {
        final Friendly log = new Friendly();
        log.setDate(anr.getDate());
        log.setSeverity("E");
        log.setCategory("Error");
        log.setSubcategory("Anr");
        log.setMessage(anr.getMessage());
        log.setLinkedId(anrId);
        logAtLogcat(log);
        insertOnBackground(log);
    }

    public static void logNetStart(NetSummary summary) {
        final Friendly startLog = new Friendly();
        startLog.setDate(summary.start_time);
        startLog.setLinkedId(summary.uid);
        startLog.setCategory("Network");
        startLog.setSubcategory(new NetFormatter(summary).getStatusString());

        startLog.setMessage("Request to " + summary.url);
        startLog.setSeverity("D");

        logAtLogcat(startLog);
        insertOnBackground(startLog);
    }

    public static void logNetEnd(NetSummary summary) {
        //TODO: findNetStart is not working, disabled update severity
        /*final Friendly startLog = IadtController.get().getDatabase().friendlyDao().findNetStart(summary.start_time, summary.uid);
        if (startLog == null){
            Iadt.showError("Unable to link the network request");
            return;
        }
        startLog.setSeverity("V");*/

        NetFormatter formatter = new NetFormatter(summary);

        final Friendly endLog = new Friendly();
        endLog.setDate(summary.end_time);
        endLog.setLinkedId(summary.uid);
        endLog.setCategory("Network");
        endLog.setSubcategory(formatter.getStatusString());

        if (summary.status == NetSummary.Status.COMPLETE){
            endLog.setSeverity("I");
            endLog.setMessage("Response " + summary.code + " from " + summary.url);
        }
        else if (summary.status == NetSummary.Status.ERROR){
            endLog.setSeverity("E");
            endLog.setMessage("Error receiving " + summary.url);
        }
        else{
            endLog.setSeverity("W");
            endLog.setMessage("Error processing " + summary.url); //This should never happen
        }

        logAtLogcat(endLog);
        ThreadUtils.runOnBack("Iadt-NetworkLog",
                new Runnable() {
                    @Override
                    public void run() {
                        //IadtController.get().getDatabase().friendlyDao().update(startLog);
                        IadtController.get().getDatabase().friendlyDao().insert(endLog);
                    }
                });
    }

    public static void logException(String message, Throwable e) {
        log("W", "Iadt", "Exception",
                message + " -> " + e.getMessage(),
                Log.getStackTraceString(e));
    }

    public static void logDebug(String message) {
        if (IadtController.get().isDebug()){
            log("D", "Iadt", "Debug", message);
        }
    }
}
