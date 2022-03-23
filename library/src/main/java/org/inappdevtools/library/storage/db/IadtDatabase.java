/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.storage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.room.Room;
//@import androidx.room.Database;
//@import androidx.room.RoomDatabase;
//@import androidx.sqlite.db.SupportSQLiteDatabase;
//#else
import android.arch.persistence.room.Room;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.db.SupportSQLiteDatabase;
//#endif

import org.inappdevtools.library.storage.db.entities.Anr;
import org.inappdevtools.library.storage.db.entities.AnrDao;
import org.inappdevtools.library.storage.db.entities.Build;
import org.inappdevtools.library.storage.db.entities.BuildDao;
import org.inappdevtools.library.storage.db.entities.Crash;
import org.inappdevtools.library.storage.db.entities.CrashDao;
import org.inappdevtools.library.storage.db.entities.Friendly;
import org.inappdevtools.library.storage.db.entities.FriendlyDao;
import org.inappdevtools.library.storage.db.entities.NetContent;
import org.inappdevtools.library.storage.db.entities.NetContentDao;
import org.inappdevtools.library.storage.db.entities.NetSummary;
import org.inappdevtools.library.storage.db.entities.NetSummaryDao;
import org.inappdevtools.library.storage.db.entities.Report;
import org.inappdevtools.library.storage.db.entities.ReportDao;
import org.inappdevtools.library.storage.db.entities.Screenshot;
import org.inappdevtools.library.storage.db.entities.ScreenshotDao;
import org.inappdevtools.library.storage.db.entities.Session;
import org.inappdevtools.library.storage.db.entities.SessionDao;
import org.inappdevtools.library.storage.db.entities.Sourcetrace;
import org.inappdevtools.library.storage.db.entities.SourcetraceDao;
import org.inappdevtools.library.storage.files.utils.FileProviderUtils;
import org.inappdevtools.library.storage.prefs.utils.DatabaseVersionPrefs;
import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.view.utils.Humanizer;

@Database(version = DatabaseVersion.VALUE,
        exportSchema = false,
        entities = {
                Build.class,
                Session.class,
                Friendly.class,
                Screenshot.class,
                Crash.class,
                Anr.class,
                Sourcetrace.class,
                Report.class,
                NetSummary.class,
                NetContent.class,
        })
public abstract class IadtDatabase extends RoomDatabase {

    public static final String DB_NAME = "inappdevtools.db";
    private static IadtDatabase INSTANCE;

    public static IadtDatabase get() {
        if (INSTANCE == null) {
            Context context = IadtController.get().getContext();
            INSTANCE = Room.databaseBuilder(context, IadtDatabase.class, DB_NAME)
                    //TODO: Research alternatives, on crash we can't create new threads
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public static void checkVersion() {
        int storedVersion = DatabaseVersionPrefs.get();
        if (storedVersion == DatabaseVersion.VALUE){
            return; //Up to date
        }

        DatabaseVersionPrefs.set(DatabaseVersion.VALUE);
        if (storedVersion > 0){
            Log.i(Iadt.TAG, "IadtDatabase schema changed. Cleaning internal files and database.");
            //Destructive migration of db will be performed by Room.databaseBuilder
            FileProviderUtils.deleteAll();
        }
    }


    //region [ DAOs ]
    public abstract SessionDao sessionDao();
    public abstract BuildDao buildDao();
    public abstract FriendlyDao friendlyDao();
    public abstract ScreenshotDao screenshotDao();
    public abstract CrashDao crashDao();
    public abstract AnrDao anrDao();
    public abstract SourcetraceDao sourcetraceDao();
    public abstract ReportDao reportDao();
    public abstract NetSummaryDao netSummaryDao();
    public abstract NetContentDao netContentDao();
    //endregion

    public void printOverview(){
        Log.d(Iadt.TAG, getOverview());
    }

    public String getOverview(){
        String overview = "";
        overview +="IadtDatabase overview: " + Humanizer.newLine();
        overview +="  Builds: " + buildDao().count() + Humanizer.newLine();
        overview +="  Sessions: " + sessionDao().count() + Humanizer.newLine();
        overview +="  FriendlyLogs: " + friendlyDao().count() + Humanizer.newLine();
        overview +="  Screenshots: " + screenshotDao().count() + Humanizer.newLine();
        overview +="  Crashs: " + crashDao().count() + Humanizer.newLine();
        overview +="  Anrs: " + anrDao().count() + Humanizer.newLine();
        overview +="  SourceTraces: " + sourcetraceDao().count() + Humanizer.newLine();
        overview +="  Reports: " + reportDao().count() + Humanizer.newLine();
        overview +="  NetSummary: " + netSummaryDao().count() + Humanizer.newLine();
        overview +="  NetContent: " + netContentDao().count();
        return overview;
    }

    public void deleteAll(){
        sessionDao().deleteAll();
        buildDao().deleteAll();
        friendlyDao().deleteAll();
        screenshotDao().deleteAll();
        crashDao().deleteAll();
        anrDao().deleteAll();
        sourcetraceDao().deleteAll();
        reportDao().deleteAll();
        netSummaryDao().deleteAll();
        netContentDao().deleteAll();

        //Reset all sequences
        SupportSQLiteDatabase supportDatabase = null;
        SQLiteDatabase database = null;
        try {
            supportDatabase = getOpenHelper().getWritableDatabase();
            database = SQLiteDatabase.openOrCreateDatabase(supportDatabase.getPath(), null, null);
            database.execSQL("delete from sqlite_sequence");
        }
        finally {
            //IOUtil.closeQuietly(supportDatabase, database);
            try {
                if (database!=null){
                    database.close();
                }

                if (supportDatabase!=null){
                    supportDatabase.close();
                }
            }
            catch (Exception ex) {
                //Intentionally empty
            }
        }
    }
}
