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

package org.inappdevtools.library.logic.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.inappdevtools.library.logic.log.FriendlyLog;

public class AppBuildConfig {

    private AppBuildConfig() { throw new IllegalStateException("Utility class"); }

    /**
     * Gets a field from the project's BuildConfig. This is useful when, for example, flavors
     * are used at the project level to set custom fields.
     * @param context       Used to find the correct file
     * @param fieldName     The name of the field-to-access
     * @return              The value of the field, or {@code null} if the field is not found.
     */
    public static Object getObjectValue(Context context, String fieldName) {
        try {
            Class<?> clazz = Class.forName(getNamespace(context) + ".BuildConfig");
            Field field = clazz.getField(fieldName);
            return field.get(null);
        }
        catch (ClassNotFoundException e) {
            FriendlyLog.logException("Exception", e);
        }
        catch (NoSuchFieldException e) {
            FriendlyLog.logException("Exception", e);
        }
        catch (IllegalAccessException e) {
            FriendlyLog.logException("Exception", e);
        }
        return null;
    }

    public static String toJson(Context context) {
        try {
            Class<?> clazz = Class.forName(getNamespace(context) + ".BuildConfig");
            Field[] fields = clazz.getFields();
            Map<String, Object> map = new HashMap<>();
            for (Field field : fields) {
                map.put(field.getName(), field.get(field.getType()));
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(map);
        }
        catch (ClassNotFoundException e) {
            FriendlyLog.logException("Exception", e);
        }
        catch (IllegalAccessException e) {
            FriendlyLog.logException("Exception", e);
        }
        return null;
    }

    public static String getNamespace(Context context) {
        int resId = context.getResources().getIdentifier("internal_package",
                "string", context.getPackageName());
        if (resId != 0) {
            return context.getString(resId);
        }else{
            return context.getPackageName();
        }
    }

    public static Boolean getBooleanValue(Context context, String fieldName) {
        Object o = getObjectValue(context, fieldName);
        if (o != null && o instanceof Boolean) {
            return (Boolean) o;
        } else {
            return null;
        }
    }

    public static Integer getIntValue(Context context, String fieldName) {
        Object o = getObjectValue(context, fieldName);
        if (o != null && o instanceof Integer) {
            return (Integer) o;
        } else {
            return null;
        }
    }

    public static String getStringValue(Context context, String fieldName) {
        Object o = getObjectValue(context, fieldName);
        if (o != null && o instanceof String) {
            return (String) o;
        } else {
            return null;
        }
    }
}
