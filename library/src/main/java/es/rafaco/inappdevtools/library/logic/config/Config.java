package es.rafaco.inappdevtools.library.logic.config;

public enum Config {

    BUILD_TIME("BUILD_TIME", long.class, null),
    BUILD_TIME_UTC("BUILD_TIME_UTC", String.class, null),
    ENABLED("ENABLED", boolean.class, false),
    DEBUG("DEBUG", boolean.class, false),
    EMAIL("EMAIL", String.class, ""),
    OVERLAY_ENABLED("OVERLAY_ENABLED", boolean.class, true),
    INVOCATION_BY_SHAKE("INVOCATION_BY_SHAKE", boolean.class, true),
    INVOCATION_BY_ICON("INVOCATION_BY_ICON", boolean.class, false),
    INVOCATION_BY_NOTIFICATION("INVOCATION_BY_NOTIFICATION", boolean.class, true),
    CALL_DEFAULT_CRASH_HANDLER("CALL_DEFAULT_CRASH_HANDLER", boolean.class, false),
    STICKY_SERVICE("STICKY_SERVICE", boolean.class, false);

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
