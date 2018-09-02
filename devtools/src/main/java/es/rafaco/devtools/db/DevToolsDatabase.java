package es.rafaco.devtools.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.util.Log;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.db.errors.Anr;
import es.rafaco.devtools.db.errors.AnrDao;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.db.errors.CrashDao;
import es.rafaco.devtools.db.errors.Logcat;
import es.rafaco.devtools.db.errors.LogcatDao;
import es.rafaco.devtools.db.errors.Screen;
import es.rafaco.devtools.db.errors.ScreenDao;

@Database(version = 9, exportSchema = true,
        entities = {User.class,
                    Crash.class,
                    Anr.class,
                    Screen.class,
                    Logcat.class})
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
    public abstract UserDao userDao();
    public abstract CrashDao crashDao();
    public abstract AnrDao anrDao();
    public abstract ScreenDao screenDao();
    public abstract LogcatDao logcatDao();
    //endregion

    public void printOverview(){
        //Log.d(DevTools.TAG, "User db size is: " + userDao().countUsers());
        Log.d(DevTools.TAG, "Internal db: ");
        Log.d(DevTools.TAG, "  Crash: " + crashDao().count());
        Log.d(DevTools.TAG, "  Anr: " + anrDao().count());
        Log.d(DevTools.TAG, "  Screen: " + screenDao().count());
        Log.d(DevTools.TAG, "  Logcat: " + logcatDao().count());
    }
}