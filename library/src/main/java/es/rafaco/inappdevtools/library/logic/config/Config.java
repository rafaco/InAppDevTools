package es.rafaco.inappdevtools.library.logic.config;

public enum Config {

    BUILD_TIME("BUILD_TIME", long.class, null),
    BUILD_TIME_UTC("BUILD_TIME_UTC", String.class, null),
    ENABLED("enabled", boolean.class, false),
    DEBUG("debug", boolean.class, false),
    EMAIL("email", String.class, ""),
    OVERLAY_ENABLED("overlayEnabled", boolean.class, true),
    INVOCATION_BY_SHAKE("invocationByShake", boolean.class, true),
    INVOCATION_BY_ICON("invocationByIcon", boolean.class, false),
    INVOCATION_BY_NOTIFICATION("invocationByNotification", boolean.class, true),
    CALL_DEFAULT_CRASH_HANDLER("callDefaultCrashHandler", boolean.class, false),
    STICKY_SERVICE("stickyService", boolean.class, false);

    private final String key;
    private final Class<?> valueType;
    private final Object defaultValue;

    Config(String key, Class<?> valueType, Object defaultValue) {
        this.key = key;
        this.valueType = valueType;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
