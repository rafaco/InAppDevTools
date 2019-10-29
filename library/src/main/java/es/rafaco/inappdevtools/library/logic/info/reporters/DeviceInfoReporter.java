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

package es.rafaco.inappdevtools.library.logic.info.reporters;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.info.InfoReport;
import es.rafaco.inappdevtools.library.logic.info.data.InfoGroupData;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import github.nisrulz.easydeviceinfo.base.BatteryHealth;
import github.nisrulz.easydeviceinfo.base.EasyBatteryMod;
import github.nisrulz.easydeviceinfo.base.EasyConfigMod;
import github.nisrulz.easydeviceinfo.base.EasyCpuMod;
import github.nisrulz.easydeviceinfo.base.EasyDisplayMod;
import github.nisrulz.easydeviceinfo.base.EasyMemoryMod;
import github.nisrulz.easydeviceinfo.base.EasySensorMod;
import github.nisrulz.easydeviceinfo.common.EasyDeviceInfo;

import static android.content.Context.WINDOW_SERVICE;

public class DeviceInfoReporter extends AbstractInfoReporter {

    EasyConfigMod configHelper;
    EasyDisplayMod displayHelper;
    EasyMemoryMod memoryHelper;

    public DeviceInfoReporter(Context context) {
        this(context, InfoReport.DEVICE);
    }

    public DeviceInfoReporter(Context context, InfoReport report) {
        super(context, report);
        configHelper = new EasyConfigMod(context);
        displayHelper = new EasyDisplayMod(context);
        memoryHelper = new EasyMemoryMod(context);
    }

    @Override
    public String getOverview() {
        String firstLine = getFirstLineOverview();
        String secondLine = getSecondLineOverview();
        return firstLine + Humanizer.newLine() + secondLine;
    }

    public String getFirstLineOverview() {
        return (configHelper.isRunningOnEmulator() ? "Emulated " : "Real ")
                + getDeviceType();
    }

    public String getSecondLineOverview() {
        return configHelper.isRunningOnEmulator() ? Build.MODEL : Build.BRAND + " " + Build.MODEL;
    }

    @Override
    public InfoReportData getData() {
        return new InfoReportData.Builder(getReport())
                .setOverview(getOverview())
                .add(getDeviceInfo())
                .add(getHardwareInfo())
                .add(getBatteryInfo())
                .add(getSensorsInfo())
                .build();
    }

    private InfoGroupData getDeviceInfo() {
        return new InfoGroupData.Builder("Device")
                    .setIcon(R.string.gmd_phone_android)
                    .setOverview(Build.MODEL)
                    .add("Form factor", getDeviceType())
                    .add("Brand", Build.BRAND)
                    .add("Model", Build.MODEL)
                    .add("isEmulator", configHelper.isRunningOnEmulator())
                    .build();
    }

    private InfoGroupData getHardwareInfo() {
        return new InfoGroupData.Builder("Hardware")
                    .setIcon(R.string.gmd_memory)
                    .add("CPU", new EasyCpuMod().getStringSupportedABIS())
                    .add("RAM", Humanizer.parseByte(memoryHelper.getTotalRAM()))
                    .add("Screen", displayHelper.getResolution()
                            + " @ " + String.valueOf((int)displayHelper.getRefreshRate() + " fps"))
                    .add("Screen Density", getDensity())
                    .add("Internal",  Humanizer.parseByte(memoryHelper.getAvailableInternalMemorySize())
                           + "/" + Humanizer.parseByte(memoryHelper.getTotalInternalMemorySize()))
                    .add("External", Humanizer.parseByte(memoryHelper.getAvailableExternalMemorySize())
                           + "/" + Humanizer.parseByte(memoryHelper.getTotalExternalMemorySize()))
                    .add("hasSDCard", configHelper.hasSdCard())
                    .build();
    }

    private InfoGroupData getBatteryInfo() {
        final EasyBatteryMod easyBatteryMod = new EasyBatteryMod(context);
        InfoGroupData.Builder batteryBuilder = new InfoGroupData.Builder("Battery")
                .setIcon(R.string.gmd_battery_std)
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
        return batteryBuilder.build();
    }

    private InfoGroupData getSensorsInfo() {
        EasySensorMod sensorHelper = new EasySensorMod(context);
        List<Sensor> allSensors = sensorHelper.getAllSensors();
        InfoGroupData.Builder sensorBuilder = new InfoGroupData.Builder("Sensors")
                .setIcon(R.string.gmd_accessibility)
                .setOverview(allSensors.size() + "");
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
        return sensorBuilder.build();
    }

    private String getDensity() {
        String helperDensity = displayHelper.getDensity();
        if (helperDensity.equals(EasyDeviceInfo.notFoundVal)){
            //Parsing values not included in DeviceInfo
            int density = context.getResources().getDisplayMetrics().densityDpi;
            if (density >= DisplayMetrics.DENSITY_260
                    && density <= DisplayMetrics.DENSITY_300){
                return "XHDPI";
            }
            else if (density >= DisplayMetrics.DENSITY_340
                    && density <= DisplayMetrics.DENSITY_440){
                return "XXHDPI";
            }
            else if (density == DisplayMetrics.DENSITY_560){
                return "XXXHDPI";
            }
        }
        return helperDensity;
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
            return "Tablet";
        } else if (diagonalInches <= 7 && diagonalInches > 6.5) {
            return "Phablet";
        } else if (diagonalInches <= 6.5 && diagonalInches >= 2) {
            return "Phone";
        } else {
            return "Watch";
        }
    }

    public String getFormattedDevice() {
        return String.format("%s %s %s", Build.BRAND, Build.MODEL, "");
    }

    @NonNull
    public String getFormattedDeviceLong() {
        return String.format("%s %s with Android %s", Humanizer.toCapitalCase(Build.BRAND), Build.MODEL, Build.VERSION.RELEASE); //getVersionName()
    }


    //region [ PLAYGROUND ]

    /*
        //Network Mod
        EasyNetworkMod easyNetworkMod = new EasyNetworkMod(context);
        InfoGroupData network = new InfoGroupData.Builder("App")
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

        InfoGroupData.Builder othersBuilder = new InfoGroupData.Builder("Others");

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

    //endregion
}
