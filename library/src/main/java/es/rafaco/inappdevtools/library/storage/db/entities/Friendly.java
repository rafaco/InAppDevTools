package es.rafaco.inappdevtools.library.storage.db.entities;

//#ifdef MODERN
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
//#else
//@import android.arch.persistence.room.ColumnInfo;
//@import android.arch.persistence.room.Entity;
//@import android.arch.persistence.room.PrimaryKey;
//#endif

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
        if (type != null ? !type.equals(friendly.type) : friendly.type != null) return false;
        if (message != null ? !message.equals(friendly.message) : friendly.message != null)
            return false;
        return extra != null ? extra.equals(friendly.extra) : friendly.extra == null;
    }
}
