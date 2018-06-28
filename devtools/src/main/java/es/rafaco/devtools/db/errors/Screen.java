package es.rafaco.devtools.db.errors;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "screen")
public class Screen implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "session")
    private int session;

    @ColumnInfo(name = "absolutePath")
    private String absolutePath;

    @ColumnInfo(name = "activityName")
    private String activityName;

    @ColumnInfo(name = "rootViewName")
    private String rootViewName;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
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

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
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
