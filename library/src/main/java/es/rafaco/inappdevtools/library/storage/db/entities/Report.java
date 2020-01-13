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

import com.google.gson.Gson;

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
import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.logic.reports.ReportType;

@Entity(tableName = "report")
public class Report implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "dateSent")
    private long dateSent;

    @ColumnInfo(name = "typeInt")
    private int typeInt;

    @ColumnInfo(name = "sessionId")
    private long sessionId;

    @ColumnInfo(name = "crashId")
    private long crashId;

    @ColumnInfo(name = "screenIds")
    private String screenIds;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "reason")
    private String reason;

    @ColumnInfo(name = "reasonInt")
    private int reasonInt;

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

    public long getDateSent() {
        return dateSent;
    }

    public void setDateSent(long dateSent) {
        this.dateSent = dateSent;
    }

    public int getTypeInt() {
        return typeInt;
    }

    public void setTypeInt(int typeInt) {
        this.typeInt = typeInt;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public long getCrashId() {
        return crashId;
    }

    public void setCrashId(long crashId) {
        this.crashId = crashId;
    }

    public void setScreenIds(String screenIds) {
        this.screenIds = screenIds;
    }

    public String getScreenIds() {
        return screenIds;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public int getReasonInt() {
        return reasonInt;
    }

    public void setReasonInt(int reasonInt) {
        this.reasonInt = reasonInt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public boolean isSent() {
        return dateSent > 0;
    }

    public boolean isDraft() {
        return dateSent < 1;
    }

    public ReportType getReportType() {
        return ReportType.getByCode(typeInt);
    }

    public void setReportType(ReportType reportType) {
        this.typeInt = reportType.getCode();
    }

    public List<Long> getScreenIdList() {
        Gson gson = new Gson();
        return gson.fromJson(screenIds, ArrayList.class);
    }

    public void setScreenIdList(List<Long> screenIds) {
        Gson gson = new Gson();
        this.screenIds = gson.toJson(screenIds);
    }
}
