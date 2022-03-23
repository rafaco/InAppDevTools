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

package org.inappdevtools.library.storage.db.entities;

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
public interface NetContentDao {

    @Query("SELECT * FROM net_content ORDER BY uid ASC")
    List<NetContent> getAll();

    @Query("SELECT * FROM net_content where uid LIKE :uid")
    NetContent findById(long uid);

    @Query("SELECT * FROM net_content where sessionId LIKE :sessionId " +
            "AND pandoraId LIKE :pandoraId ORDER BY uid DESC LIMIT 1")
    NetContent findByCompositeId(long sessionId, long pandoraId);

    @Query("SELECT * FROM net_content WHERE sessionId LIKE :sessionId ORDER BY sessionId ASC")
    List<NetContent> filterBySessionId(long sessionId);

    @Query("SELECT * FROM net_content ORDER BY uid DESC LIMIT 1")
    NetContent getLast();

    @Query("SELECT COUNT(*) FROM net_content")
    int count();

    @Insert
    long insert(NetContent net_content);

    @Insert
    long[] insertAll(NetContent... net_contents);

    @Update
    void update(NetContent net_content);

    @Delete
    void delete(NetContent net_content);

    @Query("DELETE FROM net_content")
    void deleteAll();
}
