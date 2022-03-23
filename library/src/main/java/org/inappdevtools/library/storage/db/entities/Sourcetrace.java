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

@Entity(tableName = "sourcetrace")
public class Sourcetrace {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    @ColumnInfo(name = "methodName")
    private String methodName;

    @ColumnInfo(name = "className")
    private String className;

    @ColumnInfo(name = "fileName")
    private String fileName;

    @ColumnInfo(name = "lineNumber")
    private int lineNumber;

    @ColumnInfo(name = "linkedType")
    private String linkedType;

    @ColumnInfo(name = "linkedId")
    private long linkedId;

    @ColumnInfo(name = "linkedIndex")
    private int linkedIndex;

    @ColumnInfo(name = "extra")
    private String extra;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getLinkedType() {
        return linkedType;
    }

    public void setLinkedType(String linkedType) {
        this.linkedType = linkedType;
    }

    public long getLinkedId() {
        return linkedId;
    }

    public void setLinkedId(long linkedId) {
        this.linkedId = linkedId;
    }

    public int getLinkedIndex() {
        return linkedIndex;
    }

    public void setLinkedIndex(int linkedIndex) {
        this.linkedIndex = linkedIndex;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getPackageName() {
        if(getClassName() == null) return null;
        return getClassName().substring(0, getClassName().lastIndexOf("."));
    }

    public String getShortClassName() {
        if(getClassName() == null) return null;
        return getClassName().substring(getClassName().lastIndexOf(".")+1);
    }

    public String formatClassAndMethod() {
        return getShortClassName() + "." + getMethodName() + "()";
    }

    public String formatFileAndLine() {
        return getFileName() + ":" + getLineNumber();
    }
}
