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
import android.arch.persistence.room.Update;
//#endif
import java.util.List;

@Dao
public interface SessionDao {

    @Query("SELECT * FROM session ORDER BY date DESC")
    List<Session> getAll();

    @Query("SELECT * FROM session where uid LIKE :uid")
    Session findById(long uid);

    @Query("SELECT * FROM session ORDER BY uid DESC LIMIT 1")
    Session getLast();

    @Query("SELECT COUNT(*) FROM session")
    int count();

    @Insert
    long insert(Session session);

    @Insert
    long[] insertAll(Session... sessions);

    @Update
    void update(Session session);

    @Delete
    void delete(Session session);

    @Query("DELETE FROM session")
    void deleteAll();
}
