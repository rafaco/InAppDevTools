package es.rafaco.inappdevtools.library;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.Nullable;
//#else
import android.support.annotation.Nullable;
//#endif

import es.rafaco.inappdevtools.library.logic.config.BuildConfig;
import es.rafaco.inappdevtools.library.storage.files.JsonAsset;
import es.rafaco.inappdevtools.library.storage.files.JsonAssetHelper;
import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

public class IadtLauncher extends ContentProvider {

    public IadtLauncher() {
    }

    @Override
    public boolean onCreate() {
        if (!isLibraryEnabled()){
            Log.d(Iadt.TAG, "IadtLauncher: stopped, library DISABLED by configuration");
            return false;
        }

        Log.d(Iadt.TAG, "IadtLauncher: library is ENABLED, starting...");
        new IadtController(getContext());
        /*
        //TODO: Run controller in Background thread
        ThreadUtils.runOnBack(new Runnable() {
            @Override
            public void run() {
                new IadtController(getContext());
            }
        });*/
        return true;
    }

    private boolean isLibraryEnabled(){
        // Hardcoded way to read isEnabled configuration as IadtController is not already initialized.
        // It reproduce IadtController.get().getConfig().get(BuildConfig.ENABLED);
        String enableKey = BuildConfig.ENABLED.getKey();
        SharedPreferences iadtSharedPrefs = getContext().getSharedPreferences(DevToolsPrefs.SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        JsonAssetHelper iadtCompileConfig = new JsonAssetHelper(getContext(), JsonAsset.BUILD_CONFIG);

        if (iadtSharedPrefs.contains(enableKey)){
            return iadtSharedPrefs.getBoolean(enableKey, false);
        }
        else if (iadtCompileConfig.contains(enableKey)){
            return iadtCompileConfig.getBoolean(enableKey);
        }
        else{
            return (boolean) BuildConfig.ENABLED.getDefaultValue();
        }
    }

    //region [ LEGACY METHODS FROM CONTENT PROVIDER ]

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    //endregion
}
