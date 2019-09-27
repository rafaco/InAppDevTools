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
public interface ScreenshotDao {

    @Query("SELECT * FROM screenshot ORDER BY date DESC")
    List<Screenshot> getAll();

    /*@Query("SELECT * FROM screenshots ORDER BY date DESC")
    LiveData<List<Screenshot>> getAllLive();*/

    @Query("SELECT * FROM screenshot where uid LIKE :uid")
    Screenshot findById(long uid);

    @Query("SELECT * FROM screenshot ORDER BY uid DESC LIMIT 1")
    Screenshot getLast();

    @Query("SELECT COUNT(*) from screenshot")
    int count();

    @Insert
    long insert(Screenshot screenshot);

    @Insert
    long[] insertAll(Screenshot... screenshots);

    @Delete
    void delete(Screenshot screenshot);

    @Query("DELETE FROM screenshot")
    void deleteAll();
}
