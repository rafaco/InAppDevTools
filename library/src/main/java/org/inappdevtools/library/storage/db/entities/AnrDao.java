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
public interface AnrDao {

    @Query("SELECT * FROM anr ORDER BY date DESC")
    List<Anr> getAll();

    @Query("SELECT * FROM anr where uid LIKE :uid")
    Anr findById(long uid);

    @Query("SELECT * FROM anr ORDER BY uid DESC LIMIT 1")
    Anr getLast();

    @Query("SELECT COUNT(*) from anr")
    int count();

    @Insert
    long insert(Anr anr);

    @Insert
    long[] insertAll(Anr... anrs);

    @Delete
    void delete(Anr anr);

    @Query("DELETE FROM anr")
    void deleteAll();
}
