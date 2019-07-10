package es.rafaco.inappdevtools.library.storage.db.entities;

//#ifdef ANDROIDX
//@import androidx.room.Dao;
//@import androidx.room.Delete;
//@import androidx.room.Insert;
//@import androidx.room.Query;
//#else
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
//#endif

import java.util.List;

@Dao
public interface LogcatDao {

    @Query("SELECT * FROM logcat ORDER BY date DESC")
    List<Logcat> getAll();

    @Query("SELECT * FROM logcat where uid LIKE :uid")
    Logcat findById(long uid);

    @Query("SELECT * FROM logcat ORDER BY uid DESC LIMIT 1")
    Logcat getLast();

    @Query("SELECT COUNT(*) from logcat")
    int count();

    @Insert
    long insert(Logcat logcat);

    @Insert
    long[] insertAll(Logcat... logcats);

    @Delete
    void delete(Logcat logcat);

    @Query("DELETE FROM logcat")
    void deleteAll();
}
