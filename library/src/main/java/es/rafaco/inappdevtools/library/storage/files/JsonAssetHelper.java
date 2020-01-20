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

package es.rafaco.inappdevtools.library.storage.files;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import es.rafaco.inappdevtools.library.Iadt;

public class JsonAssetHelper {

    private final Context context;
    private final String target;
    private JSONObject json;

    public JsonAssetHelper(Context context, String target) {
        this.context = context;
        this.target = target;
        init();
    }

    public boolean contains(String key){
        return json.has(key);
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

    public long getLong(String key) {
        return json.optLong(key);
    }

    public String getAll(){
        try {
            return json.toString(2);
        } catch (JSONException e) {
            return json.toString();
        }
    }

    private void init() {
        String fileContents = getFileContents();
        try {
            json = new JSONObject(fileContents);
        } catch (Exception e) {
            Log.e(Iadt.TAG,"Invalid config data at '" + target + "' " + Log.getStackTraceString(e));
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
            Log.e(Iadt.TAG, "Unable to read config at '" + target + "'" + Log.getStackTraceString(e));
            return null;
        }

        return (builder != null) ? builder.toString() : null;
    }
}
