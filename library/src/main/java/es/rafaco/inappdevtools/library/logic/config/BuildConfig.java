package es.rafaco.inappdevtools.library.logic.config;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;

public enum BuildConfig {

    EMAIL("email", R.string.config_email, String.class, ""),

    ENABLED("enabled", R.string.config_enabled, boolean.class, false),
    ENABLED_ON_RELEASE("enabledOnRelease", R.string.config_enabled_on_release, boolean.class, false),
    DEBUG("debug", R.string.config_debug, boolean.class, false),
    SOURCE_INCLUSION("sourceInclusion", R.string.config_source_inclusion, boolean.class, true),
    SOURCE_INSPECTION("sourceInspection", R.string.config_source_inspection, boolean.class, true),

    OVERLAY_ENABLED("overlayEnabled", R.string.config_overlay_enable, boolean.class, true),
    INVOCATION_BY_SHAKE("invocationByShake", R.string.config_invocation_by_shake, boolean.class, true),
    INVOCATION_BY_ICON("invocationByIcon", R.string.config_invocation_by_icon, boolean.class, true),
    INVOCATION_BY_NOTIFICATION("invocationByNotification", R.string.config_invocation_by_notification, boolean.class, true),
    CALL_DEFAULT_CRASH_HANDLER("callDefaultCrashHandler", R.string.config_call_default_crash_handler, boolean.class, false);

    private final String key;
    private final int desc;
    private final Class<?> valueType;
    private final Object defaultValue;

    BuildConfig(String key, int desc, Class<?> valueType, Object defaultValue) {
        this.key = key;
        this.desc = desc;
        this.valueType = valueType;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public int getDesc() {
        return desc;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }


    private static final List<BuildConfig> ITEMS = new ArrayList<>();

    public static List<BuildConfig> getAll() {
        if (ITEMS.isEmpty()){
            for (BuildConfig item : values()) {
                ITEMS.add(item);
            }
        }
        return ITEMS;
    }
}
