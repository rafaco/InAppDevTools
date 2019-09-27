package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.os.Build;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.info.InfoReport;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;
import es.rafaco.inappdevtools.library.storage.files.DevToolsFiles;
import es.rafaco.inappdevtools.library.view.overlay.screens.ScreenHelper;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class InfoHelper extends ScreenHelper {

    @Override
    public String getReportPath() {
        String filePath = DevToolsFiles.storeInfo(getReportContent(), System.currentTimeMillis());
        return filePath;
    }

    @Override
    public String getReportContent() {
        String result = "Generated report from info pages\n" + "//TODO:";

        InfoReport[] values = InfoReport.values();
        for (InfoReport report : values){
            InfoReportData reportData = IadtController.get().getInfoManager().getReportData(report);
            result += reportData.toString();
            result += Humanizer.newLine();
        }
        return result;
    }


    //region [ PROPERTY EXTRACTORS ]

    private Boolean isVirtual() {
        return Build.FINGERPRINT.contains("generic") ||
                Build.PRODUCT.contains("sdk");
    }


    public static String getLinuxInfo() {
        ArrayList<String> commandLine = new ArrayList<String>();
        commandLine.add("cat");
        commandLine.add("/proc/version");
        return runCommandLine(commandLine);
    }

    public static String getMemInfo() {
        ArrayList<String> commandLine = new ArrayList<String>();
        commandLine.add("cat");
        commandLine.add("/proc/meminfo");
        return runCommandLine(commandLine);
    }

    public static String getProcStat() {
        ArrayList<String> commandLine = new ArrayList<String>();
        commandLine.add("cat");
        commandLine.add("/proc/stat");
        //commandLine.add("/proc/pid/stat");
        //commandLine.add("adb top -n 1");
        //In adb shell: top -n 1
        return runCommandLine(commandLine);
    }

    @NonNull
    private static String runCommandLine(ArrayList<String> commandLine) {
        StringBuilder meminfo = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                meminfo.append(line);
                meminfo.append("\n");
            }
        } catch (IOException e) {
            Log.e(Iadt.TAG, "Could not read /proc/meminfo", e);
        }

        return meminfo.toString();
    }
    //endregion
}
