package es.rafaco.inappdevtools.library.storage.db;

//#ifdef ANDROIDX
//@import androidx.room.Room;
//@import androidx.room.Database;
//@import androidx.room.RoomDatabase;
//#else
import android.arch.persistence.room.Room;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
//#endif

import android.util.Log;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.storage.db.entities.Anr;
import es.rafaco.inappdevtools.library.storage.db.entities.AnrDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.CrashDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.FriendlyDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Logcat;
import es.rafaco.inappdevtools.library.storage.db.entities.LogcatDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Screen;
import es.rafaco.inappdevtools.library.storage.db.entities.ScreenDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.db.entities.SessionDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.storage.db.entities.SourcetraceDao;

@Database(version = 23, exportSchema = true,
        entities = {Crash.class,
                Anr.class,
                Screen.class,
                Logcat.class,
                Friendly.class,
                Sourcetrace.class,
                Session.class
        })
public abstract class DevToolsDatabase extends RoomDatabase {

    public static final String DB_NAME = "inappdevtools.db";
    private static DevToolsDatabase INSTANCE;

    public static DevToolsDatabase getInstance() {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(Iadt.getAppContext(), DevToolsDatabase.class, DB_NAME)
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
    public abstract SourcetraceDao sourcetraceDao();
    public abstract SessionDao sessionDao();
    //endregion

    public void printOverview(){
        Log.d(Iadt.TAG, getOverview());
    }

    public String getOverview(){
        String overview = "";
        String jump = "\n\t";
        overview +="Iadt DB overview: " + jump;
        overview +="  Session: " + sessionDao().count() + jump;
        overview +="  FriendlyLog: " + friendlyDao().count() + jump;
        overview +="  Logcat: " + logcatDao().count() + jump;
        overview +="  Screen: " + screenDao().count() + jump;
        overview +="  Anr: " + anrDao().count() + jump;
        overview +="  Crash: " + crashDao().count() + jump;
        overview +="  Sourcetrace: " + sourcetraceDao().count() + jump;


        return overview;
    }
}
