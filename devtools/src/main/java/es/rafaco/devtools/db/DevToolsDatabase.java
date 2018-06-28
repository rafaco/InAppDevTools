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
import es.rafaco.devtools.db.errors.Screen;
import es.rafaco.devtools.db.errors.ScreenDao;

@Database(version = 5, exportSchema = true,
        entities = {User.class,
                    Crash.class,
                    Anr.class,
                    Screen.class})
public abstract class DevToolsDatabase extends RoomDatabase {

    private static DevToolsDatabase INSTANCE;

    public static DevToolsDatabase getInstance() {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(DevTools.getAppContext(), DevToolsDatabase.class, "user-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            //.allowMainThreadQueries()
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
    //endregion

    public void printOverview(){
        //Log.d(DevTools.TAG, "User db size is: " + userDao().countUsers());
        Log.d(DevTools.TAG, "Internal db: ");
        Log.d(DevTools.TAG, "  Crash: " + crashDao().count());
        Log.d(DevTools.TAG, "  Anr: " + anrDao().count());
        Log.d(DevTools.TAG, "  Screen: " + screenDao().count());
    }
}