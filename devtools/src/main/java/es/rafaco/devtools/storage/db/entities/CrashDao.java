package es.rafaco.devtools.storage.db.entities;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CrashDao {

    @Query("SELECT * FROM crash ORDER BY date DESC")
    List<Crash> getAll();

    @Query("SELECT * FROM crash where uid LIKE :uid")
    Crash findById(long uid);

    @Query("SELECT * FROM crash ORDER BY uid DESC LIMIT 1")
    Crash getLast();

    @Query("SELECT COUNT(*) from crash")
    int count();

    @Insert
    long[] insertAll(Crash... crashes);

    @Insert
    long insert(Crash crash);

    @Update
    void update(Crash crash);

    @Delete
    void delete(Crash crash);

    @Query("DELETE FROM crash")
    void deleteAll();
}