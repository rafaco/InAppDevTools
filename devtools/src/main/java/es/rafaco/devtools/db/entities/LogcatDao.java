package es.rafaco.devtools.db.entities;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

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