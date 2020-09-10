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
public interface CrashDao {

    @Query("SELECT * FROM crash ORDER BY date DESC")
    List<Crash> getAll();

    @Query("SELECT * FROM crash where uid LIKE :uid")
    Crash findById(long uid);

    @Query("SELECT * FROM crash ORDER BY uid DESC LIMIT 1")
    Crash getLast();

    @Query("SELECT uid FROM crash ORDER BY uid DESC LIMIT 1")
    long getLastId();

    @Query("SELECT COUNT(*) from crash")
    int count();

    @Insert
    long[] insertAll(Crash... crashes);

    @Insert
    long insert(Crash crash);

    @Update
    void update(Crash crash);

    @Delete
    void delete(Crash crash);

    @Query("DELETE FROM crash")
    void deleteAll();
}
