/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import es.rafaco.inappdevtools.library.storage.files.IadtPath;
import es.rafaco.inappdevtools.library.storage.files.utils.AssetFileReader;
import es.rafaco.inappdevtools.library.storage.files.utils.JsonHelper;
import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

/**
 * This class initialize InAppDevTools library automatically.
 * Being a ContentProvider registered in Manifest, following onCreate method get called even before
 * the creation of the host app process, removing the requirement of override Application class
 * to start our library manually. Google Firebase libraries get initialized in the same way.
 */
public class IadtLauncher extends ContentProvider {

    public IadtLauncher() {
    }

    @Override
    public boolean onCreate() {
        if (!isLibraryEnabled()){
            Log.d(Iadt.TAG, "Library DISABLED by configuration. Nothing started");
            return false;
        }
        Log.d(Iadt.TAG, "Library ENABLED, starting...");

        return startLibrary();
    }

    private boolean startLibrary() {
        try {
            new IadtController(getContext());
            return true;
        }
        catch (Exception e){
            Log.e(Iadt.TAG, "IadtLauncher: exception starting library."
                + "\n" + e.getMessage() + " - " + Log.getStackTraceString(e));
            return false;
        }
    }


    /**
     * Hardcoded way to read isEnabled configuration without IadtController and ConfigManager initialized.
     * It reproduce IadtController.get().getConfig().get(BuildConfig.ENABLED);
     *
     * @return true if enabled
     */
    private boolean isLibraryEnabled(){
        try {
            String enableKey = BuildConfig.ENABLED.getKey();
            SharedPreferences iadtSharedPrefs = getContext()
                    .getSharedPreferences(DevToolsPrefs.SHARED_PREFS_KEY, Context.MODE_PRIVATE);
            String buildConfigContent = new AssetFileReader(getContext())
                    .getFileContents(IadtPath.BUILD_CONFIG);
            JsonHelper iadtBuildConfig = new JsonHelper(buildConfigContent);

            if (iadtSharedPrefs.contains(enableKey)){
                return iadtSharedPrefs.getBoolean(enableKey, false);
            }
            else if (iadtBuildConfig.contains(enableKey)){
                return iadtBuildConfig.getBoolean(enableKey);
            }
            else{
                return (boolean) BuildConfig.ENABLED.getDefaultValue();
            }
        }
        catch (Exception e){
            Log.e(Iadt.TAG, "IadtLauncher: exception checking if enabled."
                    + "\n" + e.getMessage() + " - " + Log.getStackTraceString(e));
            return false;
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
