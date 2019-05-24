package es.rafaco.inappdevtools.library.storage.db;

//#ifdef MODERN
import androidx.room.InvalidationTracker;
import androidx.annotation.NonNull;
//#else
//@import android.arch.persistence.room.InvalidationTracker;
//@import android.support.annotation.NonNull;
//#endif

import java.util.Set;


public class DevToolsTableObserver extends InvalidationTracker.Observer {

    protected DevToolsTableObserver(@NonNull String firstTable, String... rest) {
        super(firstTable, rest);
    }

    public DevToolsTableObserver(@NonNull String[] tables) {
        super(tables);
    }

    @Override
    public void onInvalidated(@NonNull Set<String> tables) {

    }
}
