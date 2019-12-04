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

package es.rafaco.inappdevtools.library.storage.db.entities;

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

@Entity(tableName = "session")
public class Session implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "pid")
    private long pid;

    @ColumnInfo(name = "detectionDate")
    private long detectionDate;

    @ColumnInfo(name = "finishDate")
    private long finishDate;

    @ColumnInfo(name = "appVersion")
    private String appVersion;

    @ColumnInfo(name = "compileConfig")
    private String compileConfig;

    @ColumnInfo(name = "isFirstStart")
    private boolean isFirstStart;

    @ColumnInfo(name = "isNewBuild")
    private boolean isNewBuild;

    @ColumnInfo(name = "isPendingCrash")
    private boolean isPendingCrash;

    @ColumnInfo(name = "crashId")
    private long crashId;

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

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public long getDetectionDate() {
        return detectionDate;
    }

    public void setDetectionDate(long detectionDate) {
        this.detectionDate = detectionDate;
    }

    public long getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(long finishDate) {
        this.finishDate = finishDate;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getCompileConfig() {
        return compileConfig;
    }

    public void setCompileConfig(String compileConfig) {
        this.compileConfig = compileConfig;
    }

    public boolean isFirstStart() {
        return isFirstStart;
    }

    public void setFirstStart(boolean firstStart) {
        isFirstStart = firstStart;
    }

    public boolean isNewBuild() {
        return isNewBuild;
    }

    public void setNewBuild(boolean newBuild) {
        isNewBuild = newBuild;
    }

    public boolean isPendingCrash() {
        return isPendingCrash;
    }

    public void setPendingCrash(boolean pendingCrash) {
        isPendingCrash = pendingCrash;
    }

    public long getCrashId() {
        return crashId;
    }

    public void setCrashId(long crashId) {
        this.crashId = crashId;
    }
}
