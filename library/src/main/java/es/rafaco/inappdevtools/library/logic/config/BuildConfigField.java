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

package es.rafaco.inappdevtools.library.logic.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.rafaco.inappdevtools.library.R;

public enum BuildConfigField {

    // Build flags (affect plugin behaviour)
    ENABLED("enabled", R.string.config_enabled, boolean.class, false),
    EXCLUDE("useNoop", R.string.config_exclude, String[].class, new String[0]),
    USE_NOOP("useNoop", R.string.config_use_noop, boolean.class, false),
    DEBUG("debug", R.string.config_debug, boolean.class, false),

    // Build feature switches (affect plugin behaviour)
    SOURCE_INCLUSION("sourceInclusion", R.string.config_source_inclusion, boolean.class, true),
    SOURCE_INSPECTION("sourceInspection", R.string.config_source_inspection, boolean.class, true),
    NETWORK_INTERCEPTOR("networkInterceptor", R.string.config_network_interceptor, boolean.class, true),

    // Runtime feature switches
    VIEW_INSPECTION("viewInspection", R.string.config_view_inspection, boolean.class, true),
    STORAGE_INSPECTION("storageInspection", R.string.config_storage_inspection, boolean.class, true),
    OVERLAY_ENABLED("overlayEnabled", R.string.config_overlay_enable, boolean.class, true),
    INVOCATION_BY_SHAKE("invocationByShake", R.string.config_invocation_by_shake, boolean.class, true),
    INVOCATION_BY_ICON("invocationByIcon", R.string.config_invocation_by_icon, boolean.class, true),
    CALL_DEFAULT_CRASH_HANDLER("callDefaultCrashHandler", R.string.config_call_default_crash_handler, boolean.class, false),
    INJECT_EVENTS_ON_LOGCAT("injectEventsOnLogcat", R.string.config_inject_events_on_logcat, boolean.class, true),

    // Runtime customizations
    NOTES("notes", R.string.config_notes, String.class, ""),
    TEAM_NAME("teamName", R.string.config_team_name, String.class, ""),
    TEAM_EMAIL("teamEmail", R.string.config_team_email, String.class, ""),
    TEAM_DESC("teamDesc", R.string.config_team_desc, String.class, ""),
    TEAM_LINKS("teamLinks", R.string.config_team_links, Map.class, null);

    private final String key;
    private final int desc;
    private final Class<?> valueType;
    private final Object defaultValue;

    BuildConfigField(String key, int desc, Class<?> valueType, Object defaultValue) {
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

    private static final List<BuildConfigField> ITEMS = new ArrayList<>();

    public static List<BuildConfigField> getAll() {
        if (ITEMS.isEmpty()){
            for (BuildConfigField item : values()) {
                ITEMS.add(item);
            }
        }
        return ITEMS;
    }
}
