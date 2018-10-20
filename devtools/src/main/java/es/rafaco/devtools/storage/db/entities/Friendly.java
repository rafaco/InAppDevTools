package es.rafaco.devtools.storage.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "friendly")
public class Friendly {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "severity")
    private String severity;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "type")
    private String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
