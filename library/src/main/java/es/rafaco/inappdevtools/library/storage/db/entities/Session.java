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
}
