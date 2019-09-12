package es.rafaco.inappdevtools.library.logic.config;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;

public enum Config {

    BUILD_TIME("BUILD_TIME", R.string.config_build_time, long.class, null),
    BUILD_TIME_UTC("BUILD_TIME_UTC", R.string.config_build_time_utc, String.class, null),
    ENABLED("enabled", R.string.config_enable, boolean.class, false),
    OVERLAY_ENABLED("overlayEnabled", R.string.config_overlay_enable, boolean.class, true),
    INVOCATION_BY_SHAKE("invocationByShake", R.string.config_invocation_by_shake, boolean.class, true),
    INVOCATION_BY_ICON("invocationByIcon", R.string.config_invocation_by_icon, boolean.class, true),
    INVOCATION_BY_NOTIFICATION("invocationByNotification", R.string.config_invocation_by_notification, boolean.class, true),
    CALL_DEFAULT_CRASH_HANDLER("callDefaultCrashHandler", R.string.config_call_default_crash_handler, boolean.class, false),
    DEBUG("debug", R.string.config_debug, boolean.class, false),
    EMAIL("email", R.string.config_email, String.class, "");

    private final String key;
    private final int desc;
    private final Class<?> valueType;
    private final Object defaultValue;

    Config(String key, int desc, Class<?> valueType, Object defaultValue) {
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


    private static final List<Config> ITEMS = new ArrayList<>();

    public static List<Config> getAll() {
        if (ITEMS.isEmpty()){
            for (Config item : values()) {
                ITEMS.add(item);
            }
        }
        return ITEMS;
    }
}
