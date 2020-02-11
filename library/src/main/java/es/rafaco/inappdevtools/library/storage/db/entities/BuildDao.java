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
public interface BuildDao {

    @Query("SELECT * FROM build ORDER BY uid DESC")
    List<Build> getAll();

    @Query("SELECT * FROM build where uid LIKE :uid")
    Build findById(long uid);

    @Query("SELECT * FROM build WHERE firstSession >= :sessionId ORDER BY uid ASC LIMIT 1")
    Build findBySessionId(long sessionId);

    @Query("SELECT * FROM build ORDER BY uid DESC LIMIT 1")
    Build getLast();

    @Query("SELECT COUNT(*) FROM build")
    int count();

    @Insert
    long insert(Build build);

    @Insert
    long[] insertAll(Build... builds);

    @Update
    void update(Build build);

    @Delete
    void delete(Build build);

    @Query("DELETE FROM build")
    void deleteAll();
}
