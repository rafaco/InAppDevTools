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

package es.rafaco.inappdevtools.library.logic.documents.generators.info;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import java.lang.reflect.Field;
import java.util.Locale;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.utils.InstalledAppsUtils;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import github.nisrulz.easydeviceinfo.base.EasyConfigMod;
import github.nisrulz.easydeviceinfo.base.EasyDeviceMod;
import github.nisrulz.easydeviceinfo.base.EasyMemoryMod;
import github.nisrulz.easydeviceinfo.base.RingerMode;

public class OSInfoDocumentGenerator extends AbstractDocumentGenerator {

    private final long sessionId;
    EasyConfigMod configHelper;
    EasyDeviceMod deviceHelper;
    EasyMemoryMod memoryHelper;

    public OSInfoDocumentGenerator(Context context, DocumentType report, long param) {
        super(context, report, param);
        this.sessionId = param;
        this.configHelper = new EasyConfigMod(context);
        this.deviceHelper = new EasyDeviceMod(context);
        this.memoryHelper = new EasyMemoryMod(context);
    }

    @Override
    public String getTitle() {
        return getDocumentType().getName() + " Info from Session " + sessionId;
    }

    @Override
    public String getSubfolder() {
        return "session/" + sessionId;
    }

    @Override
    public String getFilename() {
        return "info_" + getDocumentType().getName().toLowerCase() + "_" + sessionId + ".txt";
    }

    @Override
    public String getOverview() {
        return getFirstLineOverview() + Humanizer.newLine()
                + getDisplayLanguage() + " - " + getDisplayCountry() + Humanizer.newLine()
                + InstalledAppsUtils.getCount() + " installed apps";
    }

    public String getFirstLineOverview() {
        return getOneLineOverview() +
                (deviceHelper.isDeviceRooted() ? " [Rooted]" : "");
    }

    public String getOneLineOverview() {
        return "Android " + getAndroidVersionFull();
    }

    @Override
    public DocumentData getData() {
        return new DocumentData.Builder(getTitle())
                .setOverview(getOverview())
                .add(getAndroidGroup(deviceHelper))
                .add(getConfigGroup(configHelper, deviceHelper))
                .add(getMemoryGroupGroup(configHelper, memoryHelper))
                .add(getInstalledApps())
                .build();
    }


    public String getDisplayLanguage(){
        String langCode = deviceHelper.getLanguage();
        Locale loc = new Locale(langCode);
        String name = loc.getDisplayLanguage(loc);
        return TextUtils.isEmpty(name) ? langCode : name;
    }

    public String getDisplayCountry(){
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String code = tm.getNetworkCountryIso();
        Locale loc = new Locale("", code);
        return loc.getDisplayCountry();
    }

    protected DocumentSectionData getAndroidGroup(EasyDeviceMod deviceHelper) {
        return new DocumentSectionData.Builder("Android OS_INFO")
                .setIcon(R.string.gmd_android)
                .setOverview(getAndroidVersionFull())
                .add("Version", getAndroidVersionFull())
                .add("SDK version",  getVersionCodeName()+ " (" + Build.VERSION.SDK_INT + ")")
                .add("isRooted", deviceHelper.isDeviceRooted())
                    .build();
    }

    private String getAndroidVersionFull() {
        if (getOsCodename() != null){
            return getOsCodename() + " (" + Build.VERSION.RELEASE + ")";
        }
        else{
            return Build.VERSION.RELEASE;
        }
    }

    private String getOsCodename() {
        //Parsing modern values not included in DeviceInfo
        if (Build.VERSION.SDK_INT >= 10){
            return null;
        }
        else if (getVersionCodeName().equals("O")){
            return "Oreo";
        }else if (getVersionCodeName().equals("P")){
            return "Pie";
        }else{
            return deviceHelper.getOSCodename();
        }
    }

    protected DocumentSectionData getConfigGroup(EasyConfigMod configHelper, EasyDeviceMod deviceHelper) {
        return new DocumentSectionData.Builder("Status")
                    .setIcon(R.string.gmd_settings)
                    .add("Local time", configHelper.getFormattedTime()
                            + " - " + configHelper.getFormattedDate())
                    .add("Up time", configHelper.getFormattedUpTime())
                    .add("Language", deviceHelper.getLanguage())
                    .add("Ringer mode", parseRingerMode(configHelper.getDeviceRingerMode()))
                    .build();
    }

    protected DocumentSectionData getMemoryGroupGroup(EasyConfigMod configHelper, EasyMemoryMod memoryHelper) {
        return new DocumentSectionData.Builder("Memory & Storage")
                    .setIcon(R.string.gmd_disc_full)
                    .add("RAM", Humanizer.parseByte(memoryHelper.getTotalRAM()))
                    .add("Internal",  Humanizer.parseByte(memoryHelper.getAvailableInternalMemorySize())
                            + "/" + Humanizer.parseByte(memoryHelper.getTotalInternalMemorySize()))
                    .add("External", Humanizer.parseByte(memoryHelper.getAvailableExternalMemorySize())
                            + "/" + Humanizer.parseByte(memoryHelper.getTotalExternalMemorySize()))
                    .add("hasSDCard", configHelper.hasSdCard())
                    .build();
    }

    protected DocumentSectionData getInstalledApps() {
        return new DocumentSectionData.Builder("Installed Apps")
                .setIcon(R.string.gmd_apps)
                .setOverview(InstalledAppsUtils.getCount() + "")
                .add(InstalledAppsUtils.getString())
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
}
