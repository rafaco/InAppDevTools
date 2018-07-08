package es.rafaco.devtools.db.errors;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ScreenDao {

    @Query("SELECT * FROM screen ORDER BY date DESC")
    List<Screen> getAll();

    /*@Query("SELECT * FROM screen ORDER BY date DESC")
    LiveData<List<Screen>> getAllLive();*/

    @Query("SELECT * FROM screen where uid LIKE :uid")
    Screen findById(int uid);

    @Query("SELECT * FROM screen ORDER BY uid DESC LIMIT 1")
    Screen getLast();

    @Query("SELECT COUNT(*) from screen")
    int count();

    @Insert
    void insertAll(Screen... screens);

    @Delete
    void delete(Screen screen);

    @Query("DELETE FROM screen")
    void deleteAll();
}