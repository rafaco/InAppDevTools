package es.rafaco.devtools.storage.db;

import android.arch.persistence.room.InvalidationTracker;
import android.support.annotation.NonNull;

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
