/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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
public interface NetSummaryDao {

    @Query("SELECT * FROM net_summary ORDER BY uid ASC")
    List<NetSummary> getAll();

    @Query("SELECT * FROM net_summary where uid LIKE :uid")
    NetSummary findById(long uid);

    @Query("SELECT * FROM net_summary where sessionId LIKE :sessionId " +
            "AND pandoraId LIKE :pandoraId ORDER BY uid DESC LIMIT 1")
    NetSummary findByCompositeId(long sessionId, long pandoraId);

    @Query("SELECT * FROM net_summary WHERE sessionId LIKE :sessionId ORDER BY pandoraId ASC")
    List<NetSummary> filterBySessionId(long sessionId);

    @Query("SELECT * FROM net_summary ORDER BY uid DESC LIMIT 1")
    NetSummary getLast();

    @Query("SELECT COUNT(*) FROM net_summary")
    int count();

    @Insert
    long insert(NetSummary netsummary);

    @Insert
    long[] insertAll(NetSummary... netsummaries);

    @Update
    void update(NetSummary netsummary);

    @Delete
    void delete(NetSummary netsummary);

    @Query("DELETE FROM net_summary")
    void deleteAll();
}
