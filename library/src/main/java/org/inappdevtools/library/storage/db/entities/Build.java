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

@Entity(tableName = "build")
public class Build implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    @ColumnInfo(name = "date")
    private long date;

    //TODO: Remove Build.firstSession
    @Deprecated
    @ColumnInfo(name = "firstSession")
    private long firstSession;

    @ColumnInfo(name = "documentPath")
    private String documentPath;

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

    //TODO: Remove Build.firstSession
    @Deprecated
    public long getFirstSession() {
        return firstSession;
    }

    //TODO: Remove Build.firstSession
    @Deprecated
    public void setFirstSession(long firstSession) {
        this.firstSession = firstSession;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }
}
