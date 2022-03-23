/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.storage.db.entities;

import android.util.Log;

import java.io.Serializable;
import java.util.List;

import org.inappdevtools.library.Iadt;

public class SessionAnalysis implements Serializable {

    private boolean isFinished;
    private int total;
    private int logcatTotal;
    private int logcatFatal;
    private int logcatError;
    private int logcatWarning;
    private int logcatInfo;
    private int logcatDebug;
    private int logcatVerbose;
    private int eventTotal;
    private int eventFatal;
    private int eventError;
    private int eventWarning;
    private int eventInfo;
    private int eventDebug;
    private int eventVerbose;

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLogcatTotal() {
        return logcatTotal;
    }

    public void setLogcatTotal(int logcatTotal) {
        this.logcatTotal = logcatTotal;
    }

    public int getLogcatFatal() {
        return logcatFatal;
    }

    public void setLogcatFatal(int logcatFatal) {
        this.logcatFatal = logcatFatal;
    }

    public int getLogcatError() {
        return logcatError;
    }

    public void setLogcatError(int logcatError) {
        this.logcatError = logcatError;
    }

    public int getLogcatWarning() {
        return logcatWarning;
    }

    public void setLogcatWarning(int logcatWarning) {
        this.logcatWarning = logcatWarning;
    }

    public int getLogcatInfo() {
        return logcatInfo;
    }

    public void setLogcatInfo(int logcatInfo) {
        this.logcatInfo = logcatInfo;
    }

    public int getLogcatDebug() {
        return logcatDebug;
    }

    public void setLogcatDebug(int logcatDebug) {
        this.logcatDebug = logcatDebug;
    }

    public int getLogcatVerbose() {
        return logcatVerbose;
    }

    public void setLogcatVerbose(int logcatVerbose) {
        this.logcatVerbose = logcatVerbose;
    }

    public int getEventTotal() {
        return eventTotal;
    }

    public void setEventTotal(int eventTotal) {
        this.eventTotal = eventTotal;
    }

    public int getEventFatal() {
        return eventFatal;
    }

    public void setEventFatal(int eventFatal) {
        this.eventFatal = eventFatal;
    }

    public int getEventError() {
        return eventError;
    }

    public void setEventError(int eventError) {
        this.eventError = eventError;
    }

    public int getEventWarning() {
        return eventWarning;
    }

    public void setEventWarning(int eventWarning) {
        this.eventWarning = eventWarning;
    }

    public int getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(int eventInfo) {
        this.eventInfo = eventInfo;
    }

    public int getEventDebug() {
        return eventDebug;
    }

    public void setEventDebug(int eventDebug) {
        this.eventDebug = eventDebug;
    }

    public int getEventVerbose() {
        return eventVerbose;
    }

    public void setEventVerbose(int eventVerbose) {
        this.eventVerbose = eventVerbose;
    }

    public static SessionAnalysis buildFromRaw(List<SessionAnalysisRaw> logcatRaw, List<SessionAnalysisRaw> eventRaw) {

        SessionAnalysis result = new SessionAnalysis();
        Integer logcatTotal = 0;
        Integer eventTotal = 0;

        for (SessionAnalysisRaw item: logcatRaw) {
            switch (item.getSeverity()){
                case "D":
                    result.logcatDebug = item.count;
                    break;
                case "I":
                    result.logcatInfo = item.count;
                    break;
                case "W":
                    result.logcatWarning = item.count;
                    break;
                case "E":
                    result.logcatError = item.count;
                    break;
                case "F":
                    result.logcatFatal = item.count;
                    break;
                case "V":
                    result.logcatVerbose = item.count;
                    break;
                default:
                    Log.w(Iadt.TAG, "Unable to parse session resume result");
                    break;
            }
            logcatTotal = logcatTotal + item.count;
        }

        for (SessionAnalysisRaw item: eventRaw) {
            switch (item.getSeverity()){
                case "D":
                    result.eventDebug = item.count;
                    break;
                case "I":
                    result.eventInfo = item.count;
                    break;
                case "W":
                    result.eventWarning = item.count;
                    break;
                case "E":
                    result.eventError = item.count;
                    break;
                case "F":
                    result.eventFatal = item.count;
                    break;
                case "V":
                    result.eventVerbose = item.count;
                    break;
                default:
                    Log.w(Iadt.TAG, "Unable to parse session resume result");
                    break;
            }
            eventTotal = eventTotal + item.count;
        }

        result.eventTotal = eventTotal;
        result.logcatTotal = logcatTotal;
        result.total = eventTotal + logcatTotal;

        return result;
    }
}
