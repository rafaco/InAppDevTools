package es.rafaco.devtools.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface AnrDao {

    @Query("SELECT * FROM anr")
    List<Anr> getAll();

    @Query("SELECT * FROM anr where uid LIKE :uid")
    Anr findById(int uid);

    @Query("SELECT COUNT(*) from anr")
    int count();

    @Insert
    void insertAll(Anr... anrs);

    @Delete
    void delete(Anr anr);
}