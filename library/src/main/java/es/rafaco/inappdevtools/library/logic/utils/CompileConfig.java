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

    private static final String DEFAULT_PATH = "inappdevtools/compile_config.json";
    private final Context context;
    private final String target;
    private JSONObject json;

    public CompileConfig(Context context) {
        this.context = context;
        this.target = DEFAULT_PATH;
        init();
    }

    public CompileConfig(Context context, String target) {
        this.context = context;
        this.target = target;
        init();
    }

    public String getString(String key) {
        return json.optString(key);
    }

    public String getChildString(String parent, String key) {
        JSONObject jsonObject = json.optJSONObject(parent);
        if (jsonObject == null) {
            return "";
        }
        
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            return "";
        }
    }

    public boolean getBoolean(String key) {
        return json.optBoolean(key);
    }

    public boolean getChildBoolean(String parent, String key) {
        JSONObject jsonObject = json.optJSONObject(parent);
        if (jsonObject == null) {
            return false;
        }
        return jsonObject.optBoolean(key);
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
        try {
            json = new JSONObject(getFileContents());
        } catch (Exception e) {
            FriendlyLog.log("E", "DevTools", "CompileConfig",
                    "Invalid data at '" + target + "'", Log.getStackTraceString(e));
        }

        if (json == null) {
            json = new JSONObject();
        }
    }

    private String getFileContents() {
        StringBuilder builder = null;

        try {
            InputStream stream = context.getAssets().open(target);
            BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            builder = new StringBuilder();
            String str;

            while ((str = in.readLine()) != null) {
                builder.append(str);
            }

            in.close();

        } catch (IOException e) {
            FriendlyLog.log("E", "DevTools", "Config",
                    "Unable to read '" + target + "'", Log.getStackTraceString(e));
        }

        return (builder != null) ? builder.toString() : null;
    }
}
