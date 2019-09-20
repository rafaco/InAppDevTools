package es.rafaco.inappdevtools.library.storage.files;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class PluginList {

    public static final String PLUGIN_LIST = "inappdevtools/gradle_plugins.txt";

    public static String getIadtVersion() {
        return getPluginVersion("inappdevtools-plugin");
    }

    public static String getAndroidVersion() {
        return getPluginVersion("gradle-api");
    }

    private static String getPluginVersion(String pluginName) {
        String fileContents = getFileContents(PLUGIN_LIST);
        String[] lines = fileContents.split(Humanizer.newLine());
        for (String line : lines) {
            if (line.startsWith(pluginName + "-")){
                return getVersion(line);
            }
        }
        return "";
    }

    private static String getVersion(String line) {
        int i = 0;
        while (i < line.length() && !Character.isDigit(line.charAt(i)))
            i++;

        return line.substring(i, line.length()-4);
    }

    private static String getFileContents(String target) {
        StringBuilder builder = null;
        try {
            InputStream stream = getContext().getAssets().open(target);
            BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            builder = new StringBuilder();
            String str;

            while ((str = in.readLine()) != null) {
                builder.append(str + Humanizer.newLine());
            }

            in.close();

        } catch (IOException e) {
            FriendlyLog.log("E", "Iadt", "Config",
                    "Unable to read '" + target + "'", Log.getStackTraceString(e));
        }

        return (builder != null) ? builder.toString() : null;
    }

    private static Context getContext(){
        return IadtController.get().getContext();
    }
}
