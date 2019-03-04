package es.rafaco.inappdevtools.library.logic.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class CompileConfig {

    private static final String ASSETS_PATH = "inappdevtools/compile_config.json";
    private final Context context;
    private JSONObject json;

    public CompileConfig(Context context) {
        this.context = context;
        init();
    }

    public String getString(String key) {
        return json.optString(key);
    }

    public boolean getBoolean(String key) {
        return json.optBoolean(key);
    }

    public int getInt(String key) {
        return json.optInt(key);
    }

    public String getAll(){
        try {
            return json.toString(2);
        } catch (JSONException e) {
            return json.toString();
        }
    }

    private void init() {
        if (json == null) {
            try {
                json = new JSONObject(getFileContents());
            } catch (JSONException e) {
                FriendlyLog.log("E", "DevTools", "CompileConfig",
                        "Invalid data at '" + ASSETS_PATH + "'", Log.getStackTraceString(e));
            }
        }
    }

    private String getFileContents() {
        StringBuilder builder = null;

        try {
            InputStream stream = context.getAssets().open(ASSETS_PATH);
            BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            builder = new StringBuilder();
            String str;

            while ((str = in.readLine()) != null) {
                builder.append(str);
            }

            in.close();

        } catch (IOException e) {
            FriendlyLog.log("E", "DevTools", "Config",
                    "Unable to read '" + ASSETS_PATH + "'", Log.getStackTraceString(e));
        }

        return (builder != null) ? builder.toString() : null;
    }
}