package es.rafaco.devtools.storage.db.entities;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


import java.util.List;

@Dao
public interface FriendlyDao {

    @Query("SELECT * FROM friendly ORDER BY date ASC")
    List<Friendly> getAll();

    @Query("SELECT * FROM friendly ORDER BY date ASC")
    DataSource.Factory<Integer, Friendly> getAllProvider();

    @Query("SELECT * FROM friendly WHERE message LIKE :filter "
            + "OR category LIKE :filter "
            + "OR type LIKE :filter "
            + "OR extra LIKE :filter "
            + "AND severity IN (:acceptedLevels)")
    DataSource.Factory<Integer, Friendly> filter(List<String> acceptedLevels, String filter);

    @Query("SELECT * FROM friendly where uid LIKE :uid")
    Friendly findById(long uid);

    @Query("SELECT * FROM friendly ORDER BY uid DESC LIMIT 1")
    Friendly getLast();

    @Query("SELECT COUNT(*) from Friendly")
    int count();

    @Insert
    long insert(Friendly log);

    @Insert
    long[] insertAll(Friendly... logs);

    @Delete
    void delete(Friendly anr);

    @Query("DELETE FROM friendly")
    void deleteAll();
}