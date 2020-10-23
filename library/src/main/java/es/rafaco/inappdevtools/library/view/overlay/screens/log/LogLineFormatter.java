/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.view.overlay.screens.log;

import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.navigation.NavigationStep;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.view.overlay.screens.builds.BuildDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.crash.CrashScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.session.SessionDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.view.ZoomScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class LogLineFormatter {

    private Friendly data;

    public LogLineFormatter(Friendly data) {
        this.data = data;
    }

    public String getOneLineHeader(){
        String tag = data.isLogcat() ? data.getSubcategory() :
                data.getCategory() + "-" + data.getSubcategory();
        return String.format("%s %s/%s",
                getDateWithoutYear(), data.getSeverity(), tag);
    }

    public String getOneLine(){
        return getOneLineHeader() + ": " + getMessage();
    }

    public String getMultiLine(){
        return "Message: " + getMessage() + Humanizer.fullStop()
                + "Extra: " + getExtra() + Humanizer.fullStop()
                + "LogId: " + data.getUid() + Humanizer.newLine()
                + getDetails();
    }

    public String getMessage() {
        return data.getMessage();
    }

    public String getExtra() {
        return data.getExtra();
    }

    public String getDetails() {
        String details = "";
        if (data.isLogcat()){
            details += "Severity: " + getSeverity() + Humanizer.newLine();
            details += data.getExtra();
        }
        else{
            details += "Severity: " + getSeverity() + Humanizer.newLine();
            details += "Date: " + getDate() + Humanizer.newLine();
            details += "Source: " + "Iadt Event" + Humanizer.newLine();
            details += "Category: " + data.getCategory() + Humanizer.newLine();
            details += "Subcategory: " + data.getSubcategory();

            if (data.getLinkedId()>0){
                details += Humanizer.newLine() + "LinkedId: " + data.getLinkedId();
            }
        }
        return details;
    }

    public String getSeverity(){
        String severity = FriendlyLog.convertCharToLongString(data.getSeverity());
        return Humanizer.toCapitalCase(severity);
    }

    public String getDate() {
        return DateUtils.format(data.getDate());
    }

    public String getDateWithoutYear(){
        return DateUtils.formatLogcatDate(data.getDate());
    }

    public String getLinkName() {
        if (data.getLinkedId()<1){
            return null;
        }

        String objectName = null;
        if(data.getSubcategory().equals("Crash") ||
                data.getSubcategory().equals("Screenshot")){
            objectName = data.getSubcategory();
        }
        else if (data.getSubcategory().equals("Init")){
            objectName = "Session";
        }
        else if (data.getSubcategory().equals("NewBuild")){
            objectName = "Build";
        }
        else if(data.getCategory().equals("Network")){
            objectName = data.getCategory();
        }

        if (objectName!=null){
            return objectName + " " + data.getLinkedId();
        }
        return null;
    }

    public NavigationStep getLinkStep() {
        if (data.getLinkedId()<1){
            return null;
        }

        if(data.getSubcategory().equals("Crash")){
            return new NavigationStep(CrashScreen.class, String.valueOf(data.getLinkedId()));
        }
        else if (data.getSubcategory().equals("Screenshot")){
            return new NavigationStep(ZoomScreen.class, String.valueOf(data.getLinkedId()));
        }
        else if (data.getSubcategory().equals("Init")){
            return new NavigationStep(SessionDetailScreen.class, String.valueOf(data.getLinkedId()));
        }
        else if (data.getSubcategory().equals("NewBuild")){
            return new NavigationStep(BuildDetailScreen.class, String.valueOf(data.getLinkedId()));
        }
        else if(data.getCategory().equals("Network")){
            return new NavigationStep(NetDetailScreen.class, String.valueOf(data.getLinkedId()));
        }
        return null;
    }
}
