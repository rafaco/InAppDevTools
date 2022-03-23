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

package org.inappdevtools.library.storage.db.entities;

//#ifdef ANDROIDX
//@import androidx.room.ColumnInfo;
//@import androidx.room.Entity;
//@import androidx.room.PrimaryKey;
//#else
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
//#endif

import java.io.Serializable;

@Entity(tableName = "crash")
public class Crash implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "sessionId")
    private long sessionId;

    @ColumnInfo(name = "reportPath")
    private String reportPath;

    @ColumnInfo(name = "screenId")
    private long screenId;

    @ColumnInfo(name = "logcatId")
    private long logcatId;

    @ColumnInfo(name = "exception")
    private String exception;

    @ColumnInfo(name = "exceptionAt")
    private String exceptionAt;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "causeException")
    private String causeException;

    @ColumnInfo(name = "causeMessage")
    private String causeMessage;

    @ColumnInfo(name = "causeExceptionAt")
    private String causeExceptionAt;

    @ColumnInfo(name = "stacktrace")
    private String stacktrace;

    @ColumnInfo(name = "threadId")
    private long threadId;

    @ColumnInfo(name = "isMainThread")
    private boolean isMainThread;

    @ColumnInfo(name = "threadName")
    private String threadName;

    @ColumnInfo(name = "threadGroupName")
    private String threadGroupName;

    @ColumnInfo(name = "isForeground")
    private boolean isForeground;

    @ColumnInfo(name = "lastActivity")
    private String lastActivity;

    @ColumnInfo(name = "isReported")
    private boolean isReported;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getExceptionAt() {
        return exceptionAt;
    }

    public void setExceptionAt(String exceptionAt) {
        this.exceptionAt = exceptionAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCauseException() {
        return causeException;
    }

    public void setCauseException(String causeException) {
        this.causeException = causeException;
    }

    public String getCauseExceptionAt() {
        return causeExceptionAt;
    }

    public void setCauseExceptionAt(String causeExceptionAt) {
        this.causeExceptionAt = causeExceptionAt;
    }

    public String getCauseMessage() {
        return causeMessage;
    }

    public void setCauseMessage(String causeMessage) {
        this.causeMessage = causeMessage;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public long getScreenId() {
        return screenId;
    }

    public void setScreenId(long screenId) {
        this.screenId = screenId;
    }

    public long getLogcatId() {
        return logcatId;
    }

    public void setLogcatId(long logcatId) {
        this.logcatId = logcatId;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public boolean isMainThread() {
        return isMainThread;
    }

    public void setMainThread(boolean mainThread) {
        isMainThread = mainThread;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getThreadGroupName() {
        return threadGroupName;
    }

    public void setThreadGroupName(String threadGroupName) {
        this.threadGroupName = threadGroupName;
    }

    public boolean isForeground() {
        return isForeground;
    }

    public void setForeground(boolean foreground) {
        isForeground = foreground;
    }

    public String getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }

    public boolean isReported() {
        return isReported;
    }

    public void setReported(boolean reported) {
        isReported = reported;
    }


    //region [ RELOCATE: CAUSE FORMATTER ]

    public static final String CAUSED_BY = "Caused by:";

    public String getCaused() {
        String[] split = getStacktrace().split("\n\t");
        for (int i=0; i<split.length; i++){
            String line = split[i];
            if (line.contains(CAUSED_BY)){
                return line.substring(line.indexOf(CAUSED_BY));
            }
        }
        return null;
    }

    public String getMessageWithoutCause() {
        String cause = getCaused();
        if (cause != null && message != null && message.contains(cause)) {
            return message.replace(cause, "(...)");
        }
        return message;
    }

    //endregion
}
