package es.rafaco.devtools.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import es.rafaco.devtools.DevTools;

@Database(version = 2, entities = {User.class, Crash.class}, exportSchema = true)
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

    //endregion
}