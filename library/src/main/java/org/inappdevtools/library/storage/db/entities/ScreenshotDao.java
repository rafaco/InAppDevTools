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

package org.inappdevtools.library.storage.db.entities;

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
//#endif

import java.util.List;

@Dao
public interface ScreenshotDao {

    @Query("SELECT * FROM screenshot ORDER BY date DESC")
    List<Screenshot> getAll();

    @Query("SELECT * FROM screenshot WHERE sessionId == :sessionId ORDER BY uid")
    List<Screenshot> getAllBySessionId(long sessionId);

    @Query("SELECT * FROM screenshot where uid LIKE :uid")
    Screenshot findById(long uid);

    @Query("SELECT * FROM screenshot ORDER BY uid DESC LIMIT 1")
    Screenshot getLast();

    @Query("SELECT COUNT(*) from screenshot")
    int count();

    @Insert
    long insert(Screenshot screenshot);

    @Insert
    long[] insertAll(Screenshot... screenshots);

    @Delete
    void delete(Screenshot screenshot);

    @Query("DELETE FROM screenshot")
    void deleteAll();
}
