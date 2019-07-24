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

    public String extractPath() {
        String name = getFileName().substring(0, getFileName().indexOf("."));
        int pathEnd = getClassName().indexOf(name) + name.length();
        String path = getClassName().substring(0, pathEnd);
        path = path.replace(".", "/");

        //TODO: only works wth java and rename kotlin!!!!
        return path + ".java";
    }
}
