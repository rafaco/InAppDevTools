package es.rafaco.devtools.db.errors;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CrashDao {

    @Query("SELECT * FROM crash ORDER BY date DESC")
    List<Crash> getAll();

    @Query("SELECT * FROM crash where uid LIKE :uid")
    Crash findById(int uid);

    @Query("SELECT COUNT(*) from crash")
    int count();

    @Insert
    void insertAll(Crash... crashes);

    @Delete
    void delete(Crash crash);

    @Query("DELETE FROM crash")
    void deleteAll();
}