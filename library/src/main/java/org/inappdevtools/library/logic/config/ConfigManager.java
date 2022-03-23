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

package org.inappdevtools.library.logic.config;

import android.content.Context;

import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.events.EventManager;
import org.inappdevtools.library.storage.files.IadtPath;
import org.inappdevtools.library.storage.files.utils.AssetFileReader;
import org.inappdevtools.library.storage.files.utils.JsonHelper;
import org.inappdevtools.library.storage.prefs.IadtPrefs;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.logic.log.FriendlyLog;

public class ConfigManager {

    private final JsonHelper compileConfig;

    public ConfigManager(Context context) {
        //Always read buildConfig directly from asset
        String fileContents = new AssetFileReader(context).getFileContents(IadtPath.BUILD_CONFIG);
        compileConfig = new JsonHelper(fileContents);
    }

    public Object get(BuildConfigField config) {
        if (config.getValueType() == boolean.class){
            return getBoolean(config);
        }
        else if (config.getValueType() == String.class){
            return getString(config);
        }
        else if (config.getValueType() == long.class){
            return getLong(config);
        }
        else{
            return null;
        }
    }

    public void set(BuildConfigField config, Object value) {
        if (config.getValueType() == boolean.class){
            setBoolean(config, (Boolean) value);
        }
        else if (config.getValueType() == String.class){
            setString(config, (String) value);
        }
        else if (config.getValueType() == long.class){
            setLong(config, (Long) value);
        }
    }

    public boolean getBoolean(BuildConfigField config) {
        if (IadtPrefs.contains(config.getKey())){
            return IadtPrefs.getBoolean(config.getKey(), false);
        }
        else if (compileConfig.contains(config.getKey())){
            return compileConfig.getBoolean(config.getKey());
        }
        else{
            return (boolean) config.getDefaultValue();
        }
    }

    public void setBoolean(BuildConfigField config, boolean value) {
        IadtPrefs.setBoolean(config.getKey(), value);
        EventManager eventManager = IadtController.get().getEventManager();
        if (eventManager != null) { //Safe for disabling things on startup
            eventManager.fire(Event.CONFIG_CHANGED, config);
        }
    }

    public String getString(BuildConfigField config) {
        if (IadtPrefs.contains(config.getKey())){
            return IadtPrefs.getString(config.getKey(), "");
        }
        else if (compileConfig.contains(config.getKey())){
            return compileConfig.getString(config.getKey());
        }
        else{
            return (String) config.getDefaultValue();
        }
    }

    public void setString(BuildConfigField config, String value) {
        IadtPrefs.setString(config.getKey(), value);
        IadtController.get().getEventManager().fire(Event.CONFIG_CHANGED, config);
    }

    public long getLong(BuildConfigField config) {
        if (IadtPrefs.contains(config.getKey())){
            return IadtPrefs.getLong(config.getKey(), 0);
        }
        else if (compileConfig.contains(config.getKey())){
            return compileConfig.getLong(config.getKey());
        }
        else{
            return (long) config.getDefaultValue();
        }
    }

    public void setLong(BuildConfigField config, long value) {
        IadtPrefs.setLong(config.getKey(), value);
        IadtController.get().getEventManager().fire(Event.CONFIG_CHANGED, config);
    }

    public Map getMap(BuildConfigField config) {
        if (IadtPrefs.contains(config.getKey())){
            //TODO: not tested
            String stringMap = IadtPrefs.getString(config.getKey(), "");
            Map<String, Object> stringObjectMap = new HashMap<String, Object>();
            try {
                JSONObject jsonObject = new JSONObject(stringMap);
                stringObjectMap = JsonHelper.toMap(jsonObject);
            } catch (JSONException e) {
                FriendlyLog.logException("Error getting map from configuration", e);
            }
            return stringObjectMap;
        }
        else if (compileConfig.contains(config.getKey())){
            return compileConfig.getMap(config.getKey());
        }
        else{
            return new HashMap<String, Object>();
        }
    }

    public void setMap(BuildConfigField config, Map value) {
        //TODO: not tested and it only seems to work with Map<String, String>
        JSONObject jsonObject = new JSONObject(value);
        String stringMap = jsonObject.toString();
        IadtPrefs.setString(config.getKey(), stringMap);
        IadtController.get().getEventManager().fire(Event.CONFIG_CHANGED, config);
    }
}
