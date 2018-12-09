package es.rafaco.inappdevtools.storage.db.entities;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;

@Dao
public interface FriendlyDao {

    @Query("SELECT * FROM friendly ORDER BY date ASC")
    List<Friendly> getAll();

    @Query("SELECT * FROM friendly ORDER BY date ASC")
    DataSource.Factory<Integer, Friendly> getAllProvider();

    @Query("SELECT * FROM friendly WHERE severity IN (:acceptedLevels) "
            + "AND ( message LIKE :filter OR category LIKE :filter OR type LIKE :filter OR extra LIKE :filter ) ORDER BY date ASC")
    DataSource.Factory<Integer, Friendly> filter(List<String> acceptedLevels, String filter);

    @Query("SELECT * FROM friendly where uid LIKE :uid")
    Friendly findById(long uid);

    @Query("SELECT * FROM friendly where linkedId LIKE :linkedId")
    Friendly findByLinkedId(long linkedId);

    @Query("SELECT * FROM friendly ORDER BY uid DESC LIMIT 1")
    Friendly getLast();

    @Query("SELECT COUNT(*) from Friendly")
    int count();

    @Insert
    long insert(Friendly log);

    @Insert
    long[] insertAll(Friendly... logs);

    @Update
    void update(Friendly log);

    @Delete
    void delete(Friendly anr);

    @Query("DELETE FROM friendly")
    void deleteAll();
}