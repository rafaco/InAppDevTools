/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.Nullable;
//#else
import android.support.annotation.Nullable;
//#endif

import org.inappdevtools.library.logic.config.BuildConfigField;
import org.inappdevtools.library.logic.config.BuildInfo;
import org.inappdevtools.library.logic.crash.CrashInterceptor;
import org.inappdevtools.library.storage.files.IadtPath;
import org.inappdevtools.library.storage.files.utils.AssetFileReader;
import org.inappdevtools.library.storage.files.utils.JsonHelper;
import org.inappdevtools.library.storage.prefs.IadtPrefs;
import org.inappdevtools.library.BuildConfig;

/**
 * This class initialize InAppDevTools library automatically. It's start deciding if it should be
 * initialised and perform the initialisation of IadtController and our CrashInterceptor.
 *
 * This class extends ContentProvider because they get created before any activity and even before the creation
 * of the host app process. This technique is also used by Google Firebase libraries, removing the
 * need of forcing the host app to override Application class or to call any initialisation method
 * from their activities.
 */
public class IadtLauncher extends ContentProvider {

    public static final int MIN_SDK_INT = Build.VERSION_CODES.JELLY_BEAN;
    public static final String MIN_SDK_STRING = "Jelly Bean";

    AssetFileReader assetFileReader;
    private String extra;

    public IadtLauncher() {
    }

    @Override
    public boolean onCreate() {

        if (!shouldInitialise()){
            return false;
        }

        Log.i(Iadt.TAG, "Iadt v" + BuildConfig.VERSION_NAME + " ENABLED");

        CrashInterceptor.initialise(getContext());
        
        boolean libraryStarted = startLibrary();
        /*if (libraryStarted){
            CrashInterceptor.initialise(getContext());
        }*/
        return libraryStarted;
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

    //region [ INITIAL CHECKS ]

    private boolean shouldInitialise() {
        if (!isEnabledForSdk()) {
            Log.w(Iadt.TAG, String.format("Iadt DISABLED, sdk version %s is not supported. You have to use a device with Android %s (%s) at least.", Build.VERSION.SDK_INT, MIN_SDK_INT, MIN_SDK_STRING));
            return false;
        }

        assetFileReader = new AssetFileReader(getContext());

        if (!isPluginInstalled()) {
            Log.w(Iadt.TAG, "Iadt DISABLED, plugin not found. Add our plugin to your gradle files.");
            return false;
        }

        if (!isLibraryEnabled()) {
            Log.i(Iadt.TAG, "Iadt DISABLED by " + extra +". Nothing started");
            return false;
        }

        if (!isVersionMatch()) {
            Log.w(Iadt.TAG, "Iadt DISABLED, version mismatch (" + extra +
                    "). Update versions in your gradle files.");
            return false;
        }
        return true;
    }

    private boolean isEnabledForSdk() {
        return Build.VERSION.SDK_INT >= MIN_SDK_INT;
    }

    private boolean isPluginInstalled() {
        return assetFileReader.exists(IadtPath.BUILD_CONFIG);
    }

    /**
     * Hardcoded way to check match on plugin and library versions
     * without using IadtController and ConfigManager (not already initialized).
     *
     * @return true if enabled
     */
    private boolean isVersionMatch() {
        String fileContents = assetFileReader.getFileContents(IadtPath.BUILD_INFO);
        JsonHelper jsonHelper = new JsonHelper(fileContents);
        String pluginVersion = jsonHelper.getString(BuildInfo.IADT_PLUGIN_VERSION);
        String libraryVersion = BuildConfig.VERSION_NAME;
        if (!pluginVersion.equals(libraryVersion)){
            extra = String.format("Library is %s and plugin is %s.",
                    libraryVersion,
                    TextUtils.isEmpty(pluginVersion) ? "unknown" : pluginVersion);
            return false;
        }
        return true;
    }

    /**
     * Hardcoded way to read isEnabled configuration without IadtController and ConfigManager initialized.
     * It reproduce IadtController.get().getConfig().get(BuildConfig.ENABLED);
     *
     * @return true if enabled
     */
    private boolean isLibraryEnabled(){
        try {
            String enableKey = BuildConfigField.ENABLED.getKey();

            SharedPreferences iadtSharedPrefs = getContext()
                    .getSharedPreferences(IadtPrefs.SHARED_PREFS_KEY, Context.MODE_PRIVATE);

            String buildConfigContent = assetFileReader.getFileContents(IadtPath.BUILD_CONFIG);
            JsonHelper iadtBuildConfig = new JsonHelper(buildConfigContent);

            if (iadtSharedPrefs.contains(enableKey)){
                extra = "stored preference";
                return iadtSharedPrefs.getBoolean(enableKey, false);
            }
            else if (iadtBuildConfig.contains(enableKey)){
                extra = "build configuration";
                return iadtBuildConfig.getBoolean(enableKey);
            }
            else{
                extra = "default configuration";
                return (boolean) BuildConfigField.ENABLED.getDefaultValue();
            }
        }
        catch (Exception e){
            Log.e(Iadt.TAG, "IadtLauncher: exception checking if enabled."
                    + "\n" + e.getMessage() + " - " + Log.getStackTraceString(e));
            return false;
        }
    }

    //endregion

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
