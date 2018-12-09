package es.rafaco.inappdevtools.storage.db.entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScreenDao {

    @Query("SELECT * FROM screen ORDER BY date DESC")
    List<Screen> getAll();

    /*@Query("SELECT * FROM screen ORDER BY date DESC")
    LiveData<List<Screen>> getAllLive();*/

    @Query("SELECT * FROM screen where uid LIKE :uid")
    Screen findById(long uid);

    @Query("SELECT * FROM screen ORDER BY uid DESC LIMIT 1")
    Screen getLast();

    @Query("SELECT COUNT(*) from screen")
    int count();

    @Insert
    long insert(Screen screen);

    @Insert
    long[] insertAll(Screen... screens);

    @Delete
    void delete(Screen screen);

    @Query("DELETE FROM screen")
    void deleteAll();
}