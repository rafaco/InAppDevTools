package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import java.lang.reflect.Field;

import es.rafaco.inappdevtools.library.view.overlay.screens.info.structs.InfoGroup;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.structs.InfoReport;
import github.nisrulz.easydeviceinfo.base.EasyConfigMod;
import github.nisrulz.easydeviceinfo.base.EasyDeviceMod;
import github.nisrulz.easydeviceinfo.base.EasyMemoryMod;
import github.nisrulz.easydeviceinfo.base.RingerMode;

public class OSInfoHelper {

    Context context;
    EasyConfigMod configHelper;
    EasyDeviceMod deviceHelper;
    EasyMemoryMod memoryHelper;

    public OSInfoHelper(Context context) {
        this.context = context;
        this.configHelper = new EasyConfigMod(context);
        this.deviceHelper = new EasyDeviceMod(context);
        this.memoryHelper = new EasyMemoryMod(context);
    }

    public InfoReport getReport() {
        return new InfoReport.Builder("")
                .add(getAndroidGroup(deviceHelper))
                .add(getConfigGroup(configHelper, deviceHelper))
                .add(getMemoryGroupGroup(configHelper, memoryHelper))
                .build();
    }

    protected InfoGroup getAndroidGroup(EasyDeviceMod deviceHelper) {
        return new InfoGroup.Builder("")
                .add("Android version", deviceHelper.getOSCodename() + " (" + Build.VERSION.RELEASE + ")")
                .add("Android SDK version",  getVersionCodeName()+ " (" + String.valueOf(Build.VERSION.SDK_INT) + ")")
                .add("isRooted", deviceHelper.isDeviceRooted())
                    .build();
    }

    protected InfoGroup getConfigGroup(EasyConfigMod configHelper, EasyDeviceMod deviceHelper) {
        return new InfoGroup.Builder("Android")
                    .add("Local time", configHelper.getFormattedTime()
                            + " - " + configHelper.getFormattedDate())
                    .add("Up time", configHelper.getFormattedUpTime())
                    .add("Language", deviceHelper.getLanguage())
                    .add("Ringer mode", parseRingerMode(configHelper.getDeviceRingerMode()))
                    .add("Orientation", "//TODO")
                    .build();
    }

    protected InfoGroup getMemoryGroupGroup(EasyConfigMod configHelper, EasyMemoryMod memoryHelper) {
        return new InfoGroup.Builder("Memory & Storage")
                    .add("RAM", parseByte(memoryHelper.getTotalRAM()))
                    .add("Internal",  parseByte(memoryHelper.getAvailableInternalMemorySize())
                            + "/" + parseByte(memoryHelper.getTotalInternalMemorySize()))
                    .add("External", parseByte(memoryHelper.getAvailableExternalMemorySize())
                            + "/" + parseByte(memoryHelper.getTotalExternalMemorySize()))
                    .add("hasSDCard", configHelper.hasSdCard())
                    .build();
    }


    @NonNull
    private String parseRingerMode(int deviceRingerMode) {
        String ringerMode;
        switch (deviceRingerMode) {
            case RingerMode.NORMAL:
                ringerMode = "Normal";
                break;
            case RingerMode.SILENT:
                ringerMode = "Silent";
                break;
            case RingerMode.VIBRATE:
                ringerMode = "Vibrate";
                break;
            default:
                ringerMode = "Unknown";
                break;
        }
        return ringerMode;
    }

    private String getVersionCodeName(){
        Field[] fields = Build.VERSION_CODES.class.getFields();
        String osName = fields[Build.VERSION.SDK_INT].getName();
        return osName;
    }

    public static String parseByte(long bytes) {
        return humanReadableByteCount(bytes, true);
    }
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
