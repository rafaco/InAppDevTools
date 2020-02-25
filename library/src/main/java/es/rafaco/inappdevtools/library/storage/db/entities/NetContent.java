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

import java.io.Serializable;

//#ifdef ANDROIDX
//@import androidx.room.ColumnInfo;
//@import androidx.room.Entity;
//@import androidx.room.PrimaryKey;
//#else
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
//#endif

@Entity(tableName = "net_content")
public class NetContent implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public long uid;
    @ColumnInfo(name = "sessionId")
    public long sessionId;
    @ColumnInfo(name = "pandoraId")
    public long pandoraId;

    @ColumnInfo(name = "requestBody")
    public String requestBody;
    @ColumnInfo(name = "responseBody")
    public String responseBody;
}
