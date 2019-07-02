package es.rafaco.inappdevtools.library.storage.db.entities;

//#ifdef MODERN
//@import androidx.room.Dao;
//@import androidx.room.Delete;
//@import androidx.room.Insert;
//@import androidx.room.Query;
//@import androidx.room.RawQuery;;
//@import androidx.room.Update;
//@import androidx.paging.DataSource;
//#else
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;
import android.arch.paging.DataSource;
//#endif


import java.util.List;

@Dao
public interface FriendlyDao {

    @Query("SELECT * FROM friendly ORDER BY date ASC")
    List<Friendly> getAll();

    @Query("SELECT * FROM friendly ORDER BY date ASC")
    DataSource.Factory<Integer, Friendly> getAllProvider();

    @Query("SELECT * FROM friendly WHERE"
            + " ( message LIKE :filter OR category LIKE :filter OR subcategory LIKE :filter OR extra LIKE :filter )"
            + " AND severity IN (:severities)"
            + " ORDER BY date ASC")
    DataSource.Factory<Integer, Friendly> filter(String filter, List<String> severities);

    @RawQuery(observedEntities = Friendly.class)
    DataSource.Factory<Integer, Friendly> filterWithQuery(SupportSQLiteQuery query);

    @Query("SELECT severity AS name,"
            + " COUNT(*) AS count,"
            + " (count(*) * 100.0 / (select count(*) from friendly)) AS percentage"
            + " FROM friendly"
            + " GROUP BY severity"
            + " ORDER BY COUNT(*) DESC")
    List<AnalysisItem> analiseSeverity();

    @Query("SELECT category AS name,"
            + " COUNT(*) AS count,"
            + " (count(*) * 100.0 / (select count(*) from friendly WHERE category NOT IN ('Logcat'))) AS percentage"
            + " FROM friendly"
            + " WHERE category NOT IN ('Logcat')"
            + " GROUP BY category"
            + " ORDER BY COUNT(*) DESC")
    List<AnalysisItem> analiseEventCategory();

    @Query("SELECT subcategory AS name,"
            + " COUNT(*) AS count,"
            + " (count(*) * 100.0 / (select count(*) from friendly WHERE category IN ('Logcat'))) AS percentage"
            + " FROM friendly"
            + " WHERE category IN ('Logcat')"
            + " GROUP BY subcategory"
            + " ORDER BY COUNT(*) DESC")
    List<AnalysisItem> analiseLogcatTag();

    @RawQuery(observedEntities = Friendly.class)
    List<AnalysisItem> analiseWithQuery(SupportSQLiteQuery query);

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
    long[] insertAll(List<Friendly> logs);

    @Update
    void update(Friendly log);

    @Delete
    void delete(Friendly anr);

    @Query("DELETE FROM friendly")
    void deleteAll();
}
