package es.rafaco.inappdevtools.storage.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.util.Log;

import es.rafaco.inappdevtools.DevTools;
import es.rafaco.inappdevtools.storage.db.entities.Anr;
import es.rafaco.inappdevtools.storage.db.entities.AnrDao;
import es.rafaco.inappdevtools.storage.db.entities.Crash;
import es.rafaco.inappdevtools.storage.db.entities.CrashDao;
import es.rafaco.inappdevtools.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.storage.db.entities.FriendlyDao;
import es.rafaco.inappdevtools.storage.db.entities.Logcat;
import es.rafaco.inappdevtools.storage.db.entities.LogcatDao;
import es.rafaco.inappdevtools.storage.db.entities.Screen;
import es.rafaco.inappdevtools.storage.db.entities.ScreenDao;

@Database(version = 16, exportSchema = true,
        entities = {Crash.class,
                    Anr.class,
                    Screen.class,
                    Logcat.class,
                    Friendly.class,
        })
public abstract class DevToolsDatabase extends RoomDatabase {

    public static final String DB_NAME = "DevToolsDB";
    private static DevToolsDatabase INSTANCE;

    public static DevToolsDatabase getInstance() {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(DevTools.getAppContext(), DevToolsDatabase.class, DB_NAME)
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


    //region [ DAOs ]
    public abstract CrashDao crashDao();
    public abstract AnrDao anrDao();
    public abstract ScreenDao screenDao();
    public abstract LogcatDao logcatDao();
    public abstract FriendlyDao friendlyDao();
    //endregion

    public void printOverview(){
        Log.d(DevTools.TAG, getOverview());
    }

    public String getOverview(){
        String overview = "";
        String jump = "\n\t";
        overview +="DevTools DB overview: " + jump;
        overview +="  FriendlyLog: " + friendlyDao().count() + jump;
        overview +="  Logcat: " + logcatDao().count() + jump;
        overview +="  Screen: " + screenDao().count() + jump;
        overview +="  Anr: " + anrDao().count() + jump;
        overview +="  Crash: " + crashDao().count() + jump;

        return overview;
    }
}