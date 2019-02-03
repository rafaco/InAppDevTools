package es.rafaco.inappdevtools.library.storage.db.entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AnrDao {

    @Query("SELECT * FROM anr ORDER BY date DESC")
    List<Anr> getAll();

    @Query("SELECT * FROM anr where uid LIKE :uid")
    Anr findById(long uid);

    @Query("SELECT * FROM anr ORDER BY uid DESC LIMIT 1")
    Anr getLast();

    @Query("SELECT COUNT(*) from anr")
    int count();

    @Insert
    long insert(Anr anr);

    @Insert
    long[] insertAll(Anr... anrs);

    @Delete
    void delete(Anr anr);

    @Query("DELETE FROM anr")
    void deleteAll();
}