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

package org.inappdevtools.library.storage.db.entities;

//#ifdef ANDROIDX
//@import androidx.room.ColumnInfo;
//@import androidx.room.Entity;
//@import androidx.room.PrimaryKey;
//@import androidx.room.Ignore;
//@import androidx.annotation.IntDef;
//#else
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.IntDef;
//#endif

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Entity(tableName = "net_summary")
public class NetSummary implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public long uid;
    @ColumnInfo(name = "sessionId")
    public long sessionId;
    @ColumnInfo(name = "pandoraId")
    public long pandoraId;

    @ColumnInfo(name = "status")
    public int status;
    @ColumnInfo(name = "code")
    public int code;
    @ColumnInfo(name = "url")
    public String url;
    @ColumnInfo(name = "query")
    public String query;
    @ColumnInfo(name = "host")
    public String host;
    @ColumnInfo(name = "method")
    public String method;
    @ColumnInfo(name = "protocol")
    public String protocol;
    @ColumnInfo(name = "ssl")
    public boolean ssl;
    @ColumnInfo(name = "start_time")
    public long start_time;
    @ColumnInfo(name = "end_time")
    public long end_time;
    @ColumnInfo(name = "request_content_type")
    public String request_content_type;
    @ColumnInfo(name = "response_content_type")
    public String response_content_type;
    @ColumnInfo(name = "request_size")
    public long request_size;
    @ColumnInfo(name = "response_size")
    public long response_size;
    @ColumnInfo(name = "request_header")
    public String request_header;
    @ColumnInfo(name = "response_header")
    public String response_header;

    @IntDef({
            Status.REQUESTING,
            Status.ERROR,
            Status.COMPLETE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
        int REQUESTING = 0x00;
        int ERROR = 0x01;
        int COMPLETE = 0x02;
    }

    public boolean equalContent(Object o) {
        if (this == o) return true;
        if (!(o instanceof NetSummary)) return false;
        NetSummary summary = (NetSummary) o;

        if (start_time != summary.start_time) return false;
        if (end_time != summary.end_time) return false;
        if (status != summary.status) return false;
        return code == summary.code;
    }
}
