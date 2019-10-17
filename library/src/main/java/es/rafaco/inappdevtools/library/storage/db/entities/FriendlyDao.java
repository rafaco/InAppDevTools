/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.storage.db.entities;

//#ifdef ANDROIDX
//@import androidx.room.Dao;
//@import androidx.room.Delete;
//@import androidx.room.Insert;
//@import androidx.room.Query;
//@import androidx.room.RawQuery;
//@import androidx.room.Update;
//@import androidx.paging.DataSource;
//@import androidx.sqlite.db.SupportSQLiteQuery;
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

    @Query("SELECT * FROM friendly WHERE date = :date ORDER BY uid ASC")
    List<Friendly> filterByDate(long date);

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

    @Query("SELECT uid AS name,"
            + " 1 AS count,"
            + " (100.0 / (select count(*) from session)) AS percentage"
            + " FROM session"
            + " ORDER BY date DESC")
    List<AnalysisItem> analiseSession();

    @Query("SELECT *"
            + " FROM friendly"
            + " WHERE extra LIKE :extraContent"
            + " AND date < :date"
            + " AND category IN ('Logcat')"
            + " ORDER BY date ASC LIMIT 1")
    Friendly getFirstSessionLog(String extraContent, long date);

    @Query("SELECT *"
            + " FROM friendly"
            + " WHERE message LIKE :message"
            + " AND category IN ('Iadt')"
            + " AND subcategory IN ('Init')"
            + " ORDER BY date ASC LIMIT 1")
    Friendly getNewSessionLog(String message);

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
