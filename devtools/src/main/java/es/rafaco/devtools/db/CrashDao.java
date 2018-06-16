package es.rafaco.devtools.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CrashDao {

    @Query("SELECT * FROM crash")
    List<Crash> getAll();

    @Query("SELECT * FROM crash where uid LIKE :uid")
    Crash findById(int uid);

    @Query("SELECT COUNT(*) from crash")
    int count();

    @Insert
    void insertAll(Crash... crashes);

    @Delete
    void delete(Crash crash);
}