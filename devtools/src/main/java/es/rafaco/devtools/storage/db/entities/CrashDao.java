package es.rafaco.devtools.storage.db.entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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