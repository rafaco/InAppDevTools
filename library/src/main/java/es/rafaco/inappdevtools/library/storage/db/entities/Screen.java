package es.rafaco.inappdevtools.library.storage.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "screen")
public class Screen implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "session")
    private int session;

    @ColumnInfo(name = "path")
    private String path;

    @ColumnInfo(name = "activityName")
    private String activityName;

    @ColumnInfo(name = "rootViewName")
    private String rootViewName;

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

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getRootViewName() {
        return rootViewName;
    }

    public void setRootViewName(String rootViewName) {
        this.rootViewName = rootViewName;
    }
}
