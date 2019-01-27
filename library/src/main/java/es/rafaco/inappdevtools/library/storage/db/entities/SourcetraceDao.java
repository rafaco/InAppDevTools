package es.rafaco.inappdevtools.library.storage.db.entities;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SourcetraceDao {

    @Query("SELECT * FROM sourcetrace ORDER BY uid ASC")
    List<Sourcetrace> getAll();

    @Query("SELECT * FROM sourcetrace WHERE linkedType='crash' AND linkedId LIKE :crashId ORDER BY uid ASC")
    List<Sourcetrace> filterCrash(long crashId);

    @Query("SELECT * FROM Sourcetrace where uid LIKE :uid")
    Sourcetrace findById(long uid);

    @Query("SELECT * FROM sourcetrace where linkedId LIKE :linkedId")
    Sourcetrace findByLinkedId(long linkedId);

    @Query("SELECT * FROM sourcetrace ORDER BY uid DESC LIMIT 1")
    Sourcetrace getLast();

    @Query("SELECT COUNT(*) from sourcetrace")
    int count();

    @Insert
    long insert(Sourcetrace log);

    @Insert
    long[] insertAll(List<Sourcetrace> traces);

    @Update
    void update(Sourcetrace log);

    @Delete
    void delete(Sourcetrace anr);

    @Query("DELETE FROM sourcetrace")
    void deleteAll();
}