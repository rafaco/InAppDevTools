package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.structs.InfoGroup;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.structs.InfoReport;
import github.nisrulz.easydeviceinfo.base.BatteryHealth;
import github.nisrulz.easydeviceinfo.base.EasyBatteryMod;
import github.nisrulz.easydeviceinfo.base.EasyConfigMod;
import github.nisrulz.easydeviceinfo.base.EasyCpuMod;
import github.nisrulz.easydeviceinfo.base.EasyDisplayMod;
import github.nisrulz.easydeviceinfo.base.EasyMemoryMod;
import github.nisrulz.easydeviceinfo.base.EasySensorMod;

import static android.content.Context.WINDOW_SERVICE;
import static es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.OSInfoHelper.humanReadableByteCount;

public class DeviceInfoHelper {
    Context context;

    public DeviceInfoHelper(Context context) {
        this.context = context;
    }

    public InfoReport getReport() {

        EasyConfigMod configHelper = new EasyConfigMod(context);
        EasyDisplayMod displayHelper = new EasyDisplayMod(context);
        EasyMemoryMod memoryHelper = new EasyMemoryMod(context);

        InfoGroup model = new InfoGroup.Builder("")
                .add("Form factor", getDeviceType())
                .add(context.getString(R.string.brand), Build.BRAND)
                .add(context.getString(R.string.model), Build.MODEL)
                .add("isEmulator", configHelper.isRunningOnEmulator())
                .build();

        InfoGroup hardware = new InfoGroup.Builder("Hardware")
                .add("CPU", new EasyCpuMod().getStringSupportedABIS())
                .add("RAM", parseByte(memoryHelper.getTotalRAM()))
                .add("Screen", displayHelper.getResolution()
                        + " @ " + String.valueOf((int)displayHelper.getRefreshRate() + " fps"))
                .add("Screen Density", displayHelper.getDensity() )
                .add("Internal",  parseByte(memoryHelper.getAvailableInternalMemorySize())
                       + "/" + parseByte(memoryHelper.getTotalInternalMemorySize()))
                .add("External", parseByte(memoryHelper.getAvailableExternalMemorySize())
                       + "/" + parseByte(memoryHelper.getTotalExternalMemorySize()))
                .add("hasSDCard", configHelper.hasSdCard())
                .build();

        EasySensorMod sensorHelper = new EasySensorMod(context);
        List<Sensor> allSensors = sensorHelper.getAllSensors();
        InfoGroup.Builder sensorBuilder = new InfoGroup.Builder("Sensors");
        if (allSensors != null && !allSensors.isEmpty()){
            for(Sensor sensor : allSensors){
                String name = sensor.getName();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                    String type = sensor.getStringType();
                    String typeString = type.substring(type.lastIndexOf(".") + 1);
                    name = name + " (" + typeString + ")";
                }
                sensorBuilder.add("", name);
            }
        }
        InfoGroup sensor = sensorBuilder.build();

        /*
        //Network Mod
        EasyNetworkMod easyNetworkMod = new EasyNetworkMod(context);
        InfoGroup network = new InfoGroup.Builder("App")
            .add("WIFI MAC Address", easyNetworkMod.getWifiMAC())
            .add("WIFI LinkSpeed", easyNetworkMod.getWifiLinkSpeed())
            .add("WIFI SSID", easyNetworkMod.getWifiSSID())
            .add("WIFI BSSID", easyNetworkMod.getWifiBSSID())
            .add("IPv4 Address", easyNetworkMod.getIPv4Address())
            .add("IPv6 Address", easyNetworkMod.getIPv6Address())
            .add("Network Available", String.valueOf(easyNetworkMod.isNetworkAvailable()))
            .add("Wi-Fi enabled", String.valueOf(easyNetworkMod.isWifiEnabled()))
                .build();

        @NetworkType final int networkType = easyNetworkMod.getNetworkType();

        switch (networkType) {
            case NetworkType.CELLULAR_UNKNOWN:
                .add(this.getString(string.network_type), "Cellular Unknown");
                break;
            case NetworkType.CELLULAR_UNIDENTIFIED_GEN:
                .add(this.getString(string.network_type), "Cellular Unidentified Generation");
                break;
            case NetworkType.CELLULAR_2G:
                .add(this.getString(string.network_type), "Cellular 2G");
                break;
            case NetworkType.CELLULAR_3G:
                .add(this.getString(string.network_type), "Cellular 3G");
                break;
            case NetworkType.CELLULAR_4G:
                .add(this.getString(string.network_type), "Cellular 4G");
                break;

            case NetworkType.WIFI_WIFIMAX:
                .add(this.getString(string.network_type), "Wifi/WifiMax");
                break;
            case NetworkType.UNKNOWN:
            default:
                .add(this.getString(string.network_type), "Unknown");
                break;
        }

        */

        // Battery Mod
        final EasyBatteryMod easyBatteryMod = new EasyBatteryMod(context);
        InfoGroup.Builder batteryBuilder = new InfoGroup.Builder("Battery")
            .add("Battery Percentage", String.valueOf(easyBatteryMod.getBatteryPercentage()) + '%')
            .add("Is device charging", String.valueOf(easyBatteryMod.isDeviceCharging()))
            .add("Battery present", String.valueOf(easyBatteryMod.isBatteryPresent()))
            .add("Battery technology", String.valueOf(easyBatteryMod.getBatteryTechnology()))
            .add("Battery temperature",
                    easyBatteryMod.getBatteryTemperature() + " deg C")
            .add("Battery voltage",
                easyBatteryMod.getBatteryVoltage() + " mV");

        @BatteryHealth final int batteryHealth = easyBatteryMod.getBatteryHealth();
        switch (batteryHealth) {
            case BatteryHealth.GOOD:
                batteryBuilder.add("Battery health", "Good");
                break;
            case BatteryHealth.HAVING_ISSUES:
            default:
                batteryBuilder.add("Battery health", "Having issues");
                break;
        }
        InfoGroup battery = batteryBuilder.build();

        /*
        @ChargingVia final int isChargingVia = easyBatteryMod.getChargingSource();
        switch (isChargingVia) {
            case ChargingVia.AC:
                batteryBuilder.add("Charging via", "AC");
                break;
            case ChargingVia.USB:
                batteryBuilder.add("Charging via", "USB");
                break;
            case ChargingVia.WIRELESS:
                batteryBuilder.add("Charging via", "Wireless");
                break;
            case ChargingVia.UNKNOWN_SOURCE:
            default:
                batteryBuilder.add("Charging via", "Unknown Source");
                break;
        }

        InfoGroup.Builder othersBuilder = new InfoGroup.Builder("Others");

        //Bluetooth Mod
        final EasyBluetoothMod easyBluetoothMod = new EasyBluetoothMod(context);
        othersBuilder.add("BT MAC Address", easyBluetoothMod.getBluetoothMAC());


        // Location Mod
        final EasyLocationMod easyLocationMod = new EasyLocationMod(context);
        final double[] l = easyLocationMod.getLatLong();
        final String lat = String.valueOf(l[0]);
        final String lon = String.valueOf(l[1]);
        othersBuilder.add("Latitude", lat);
        othersBuilder.add("Longitude", lon);
        */


        return new InfoReport.Builder("")
                .add(model)
                .add(hardware)
                .add(battery)
                .add(sensor)
                .build();
    }

    private String getDeviceType() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches > 10.1) {
            return "TV";
        } else if (diagonalInches <= 10.1 && diagonalInches > 7) {
            return "TABLET";
        } else if (diagonalInches <= 7 && diagonalInches > 6.5) {
            return "PHABLET";
        } else if (diagonalInches <= 6.5 && diagonalInches >= 2) {
            return "PHONE";
        } else {
            return "WATCH";
        }
    }

    public static String parseByte(long bytes) {
        return humanReadableByteCount(bytes, true);
    }

    public String getFormattedDevice() {
        return String.format("%s %s %s", Build.BRAND, Build.MODEL, "");
    }

    @NonNull
    public String getFormattedDeviceLong() {
        return String.format("%s %s with Android %s", capitalize(Build.BRAND), Build.MODEL, Build.VERSION.RELEASE); //getVersionName()
    }

    public String capitalize(String s){
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }
}