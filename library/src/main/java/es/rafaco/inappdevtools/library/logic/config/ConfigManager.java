package es.rafaco.inappdevtools.library.logic.config;

import android.content.Context;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.storage.files.JsonAsset;
import es.rafaco.inappdevtools.library.storage.files.JsonAssetHelper;
import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

public class ConfigManager {

    private final JsonAssetHelper compileConfig;

    public ConfigManager(Context context) {
        compileConfig = new JsonAssetHelper(context, JsonAsset.COMPILE_CONFIG);
    }

    public boolean getBoolean(Config config) {
        if (DevToolsPrefs.contains(config.getKey())){
            return DevToolsPrefs.getBoolean(config.getKey(), false);
        }
        else if (compileConfig.contains(config.getKey())){
            return compileConfig.getBoolean(config.getKey());
        }
        else{
            return (boolean) config.getDefaultValue();
        }
    }

    public void setBoolean(Config config, boolean value) {
        DevToolsPrefs.setBoolean(config.getKey(), value);
        DevTools.getEventManager().fire(Event.CONFIG_CHANGED, config.getKey());
    }

    public String getString(Config config) {
        if (DevToolsPrefs.contains(config.getKey())){
            return DevToolsPrefs.getString(config.getKey(), "");
        }
        else if (compileConfig.contains(config.getKey())){
            return compileConfig.getString(config.getKey());
        }
        else{
            return (String) config.getDefaultValue();
        }
    }

    public void setString(Config config, String value) {
        DevToolsPrefs.setString(config.getKey(), value);
        DevTools.getEventManager().fire(Event.CONFIG_CHANGED, config.getKey());
    }

    public long getLong(Config config) {
        if (DevToolsPrefs.contains(config.getKey())){
            return DevToolsPrefs.getLong(config.getKey(), 0);
        }
        else if (compileConfig.contains(config.getKey())){
            return compileConfig.getLong(config.getKey());
        }
        else{
            return (long) config.getDefaultValue();
        }
    }

    public void setLong(Config config, long value) {
        DevToolsPrefs.setLong(config.getKey(), value);
        DevTools.getEventManager().fire(Event.CONFIG_CHANGED, config.getKey());
    }
}
