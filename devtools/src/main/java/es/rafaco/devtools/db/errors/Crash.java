package es.rafaco.devtools.db.errors;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "crash")
public class Crash implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "exception")
    private String exception;

    @ColumnInfo(name = "message")
    private String message;

    //@ColumnInfo(name = "where")
    //private String where;

    @ColumnInfo(name = "stacktrace")
    private String stacktrace;

    @ColumnInfo(name = "rawScreen")
    private byte[] rawScreen;

    @ColumnInfo(name = "rawLogcat")
    private String rawLogcat;

    @ColumnInfo(name = "screenId")
    private long screenId;

    @ColumnInfo(name = "logcatId")
    private long logcatId;

    @ColumnInfo(name = "threadId")
    private long threadId;

    @ColumnInfo(name = "isMainThread")
    private boolean isMainThread;

    @ColumnInfo(name = "threadName")
    private String threadName;

    @ColumnInfo(name = "threadGroupName")
    private String threadGroupName;


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

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public byte[] getRawScreen() {
        return rawScreen;
    }

    public void setRawScreen(byte[] rawScreen) {
        this.rawScreen = rawScreen;
    }

    public String getRawLogcat() {
        return rawLogcat;
    }

    public void setRawLogcat(String rawLogcat) {
        this.rawLogcat = rawLogcat;
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
}
