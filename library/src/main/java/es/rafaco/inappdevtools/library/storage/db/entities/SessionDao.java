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
//@import androidx.room.Update;
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

    @Query("SELECT * FROM session ORDER BY uid DESC")
    List<Session> getAll();

    @Query("SELECT * FROM session where uid LIKE :uid")
    Session findById(long uid);

    @Query("SELECT * FROM session WHERE crashId LIKE :crashId ORDER BY uid DESC LIMIT 1")
    Session findByCrashId(long crashId);

    @Query("SELECT * FROM session WHERE buildId LIKE :buildId ORDER BY uid DESC")
    List<Session> filterByBuildId(long buildId);

    @Query("SELECT * FROM session ORDER BY uid DESC LIMIT 1")
    Session getLast();

    @Query("SELECT uid FROM session ORDER BY uid DESC LIMIT 1")
    long getLastId();

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


    //region [ SESSION ANALYSIS ]

    @Query("SELECT severity as severity, COUNT(*) as count"
            + " FROM friendly"
            + " WHERE category IN ('Logcat')"
            + " AND date >= :sessionStart"
            + " GROUP BY severity")
    List<SessionAnalysisRaw> analiseLiveSessionLogcat(long sessionStart);

    @Query("SELECT severity as severity, COUNT(*) as count"
            + " FROM friendly"
            + " WHERE category NOT IN ('Logcat')"
            + " AND date >= :sessionStart"
            + " GROUP BY severity")
    List<SessionAnalysisRaw> analiseLiveSessionEvents(long sessionStart);

    @Query("SELECT severity as severity, COUNT(*) as count"
            + " FROM friendly"
            + " WHERE category IN ('Logcat')"
            + " AND date >= :sessionStart"
            + " AND date <= :sessionEnd"
            + " GROUP BY severity")
    List<SessionAnalysisRaw> analiseFinishedSessionLogcat(long sessionStart, long sessionEnd);

    @Query("SELECT severity as severity, COUNT(*) as count"
            + " FROM friendly"
            + " WHERE category NOT IN ('Logcat')"
            + " AND date >= :sessionStart"
            + " AND date <= :sessionEnd"
            + " GROUP BY severity")
    List<SessionAnalysisRaw> analiseFinishedSessionEvents(long sessionStart, long sessionEnd);

    //endregion
}
