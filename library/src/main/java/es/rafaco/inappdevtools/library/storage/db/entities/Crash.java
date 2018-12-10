package es.rafaco.inappdevtools.library.storage.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "crash")
public class Crash implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "reportPath")
    private String reportPath;

    @ColumnInfo(name = "screenId")
    private long screenId;

    @ColumnInfo(name = "logcatId")
    private long logcatId;

    @ColumnInfo(name = "exception")
    private String exception;

    @ColumnInfo(name = "exceptionAt")
    private String exceptionAt;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "causeException")
    private String causeException;

    @ColumnInfo(name = "causeMessage")
    private String causeMessage;

    @ColumnInfo(name = "causeExceptionAt")
    private String causeExceptionAt;

    //@ColumnInfo(name = "where")
    //private String where;

    @ColumnInfo(name = "stacktrace")
    private String stacktrace;

    @ColumnInfo(name = "rawScreen")
    private byte[] rawScreen;

    @ColumnInfo(name = "rawLogcat")
    private String rawLogcat;

    @ColumnInfo(name = "threadId")
    private long threadId;

    @ColumnInfo(name = "isMainThread")
    private boolean isMainThread;

    @ColumnInfo(name = "threadName")
    private String threadName;

    @ColumnInfo(name = "threadGroupName")
    private String threadGroupName;

    @ColumnInfo(name = "isForeground")
    private boolean isForeground;

    @ColumnInfo(name = "lastActivity")
    private String lastActivity;

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

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getExceptionAt() {
        return exceptionAt;
    }

    public void setExceptionAt(String exceptionAt) {
        this.exceptionAt = exceptionAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCauseException() {
        return causeException;
    }

    public void setCauseException(String causeException) {
        this.causeException = causeException;
    }

    public String getCauseExceptionAt() {
        return causeExceptionAt;
    }

    public void setCauseExceptionAt(String causeExceptionAt) {
        this.causeExceptionAt = causeExceptionAt;
    }

    public String getCauseMessage() {
        return causeMessage;
    }

    public void setCauseMessage(String causeMessage) {
        this.causeMessage = causeMessage;
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

    public boolean isForeground() {
        return isForeground;
    }

    public void setForeground(boolean foreground) {
        isForeground = foreground;
    }

    public String getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }
}
