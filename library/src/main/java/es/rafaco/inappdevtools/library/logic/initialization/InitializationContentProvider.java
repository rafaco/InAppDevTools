package es.rafaco.inappdevtools.library.logic.initialization;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

//#ifdef MODERN
//@import androidx.annotation.Nullable;
//#else
import android.support.annotation.Nullable;
//#endif

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class InitializationContentProvider extends ContentProvider {

    private static final String META_DATA_KEY = "devtools_auto_init_enabled";

    public InitializationContentProvider() {
    }

    @Override
    public boolean onCreate() {

        DevTools.logCreatedInitProvider(getContext());

        //TODO: conditional intialization
        //boolean enabled = getMetadata().getBoolean(META_DATA_KEY, true);
        if(true){
            DevTools.install(getContext());
        }

        return true;
    }

    public Bundle getMetadata() {
        ApplicationInfo appInfo = null;
        try {
            appInfo = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            FriendlyLog.logException("Exception at getMetadata", e);
        }

        if (appInfo == null){
            return null;
        }

        return appInfo.metaData;
    }


    //region [ LEGACY METHODS ]

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
