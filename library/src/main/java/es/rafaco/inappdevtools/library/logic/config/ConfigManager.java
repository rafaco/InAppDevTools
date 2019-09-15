package es.rafaco.inappdevtools.library.logic.config;

import android.content.Context;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.storage.files.JsonAsset;
import es.rafaco.inappdevtools.library.storage.files.JsonAssetHelper;
import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

public class ConfigManager {

    private final JsonAssetHelper compileConfig;

    public ConfigManager(Context context) {
        compileConfig = new JsonAssetHelper(context, JsonAsset.COMPILE_CONFIG);
    }

    public Object get(Config config) {
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

    public void set(Config config, Object value) {
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
        IadtController.get().getEventManager().fire(Event.CONFIG_CHANGED, config);
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
        IadtController.get().getEventManager().fire(Event.CONFIG_CHANGED, config);
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
        IadtController.get().getEventManager().fire(Event.CONFIG_CHANGED, config);
    }
}
