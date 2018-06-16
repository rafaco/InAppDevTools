package es.rafaco.devtools;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.Serializable;

import es.rafaco.devtools.db.Crash;

public class DbService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DbService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Crash crash = (Crash) intent.getSerializableExtra("crash");
        Log.w("RAFA", "DbService recibio un crash: " + crash.getMessage());

    }

    public static Intent buildCrashIntent(Context context, Crash crash){
        Intent intent = new Intent(context, DbService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("crash", (Serializable) crash);
        return intent;
    }
}
