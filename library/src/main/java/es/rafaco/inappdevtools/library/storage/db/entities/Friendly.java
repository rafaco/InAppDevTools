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

@Entity(tableName = "friendly")
public class Friendly {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "severity")
    private String severity = "V";

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "subcategory")
    private String subcategory;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "extra")
    private String extra;

    @ColumnInfo(name = "linkedId")
    private long linkedId;

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

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public long getLinkedId() {
        return linkedId;
    }

    public void setLinkedId(long linkedId) {
        this.linkedId = linkedId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendly)) return false;

        Friendly friendly = (Friendly) o;

        if (uid != friendly.uid) return false;
        if (date != friendly.date) return false;
        if (linkedId != friendly.linkedId) return false;
        if (severity != null ? !severity.equals(friendly.severity) : friendly.severity != null)
            return false;
        if (category != null ? !category.equals(friendly.category) : friendly.category != null)
            return false;
        if (subcategory != null ? !subcategory.equals(friendly.subcategory) : friendly.subcategory != null) return false;
        if (message != null ? !message.equals(friendly.message) : friendly.message != null)
            return false;
        return extra != null ? extra.equals(friendly.extra) : friendly.extra == null;
    }

    public boolean equalContent(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendly)) return false;
        Friendly friendly = (Friendly) o;

        if (date != friendly.date) return false;
        if (linkedId != friendly.linkedId) return false;
        if (severity != null ? !severity.equals(friendly.severity) : friendly.severity != null)
            return false;
        if (category != null ? !category.equals(friendly.category) : friendly.category != null)
            return false;
        if (subcategory != null ? !subcategory.equals(friendly.subcategory) : friendly.subcategory != null) return false;
        if (message != null ? !message.equals(friendly.message) : friendly.message != null)
            return false;
        return extra != null ? extra.equals(friendly.extra) : friendly.extra == null;
    }

    public boolean equalContentForCollapsing(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendly)) return false;
        Friendly previous = (Friendly) o;

        //Date approx. and no message check
        if (!((previous.date <= date) && (date < previous.date + 5L))) return false;

        if (linkedId != previous.linkedId) return false;
        if (severity != null ? !severity.equals(previous.severity) : previous.severity != null)
            return false;
        if (category != null ? !category.equals(previous.category) : previous.category != null)
            return false;
        if (subcategory != null ? !subcategory.equals(previous.subcategory) : previous.subcategory != null) return false;
        return extra != null ? extra.equals(previous.extra) : previous.extra == null;
    }

    public boolean isLogcat() {
        return getCategory().equals("Logcat");
    }
}
