package es.rafaco.inappdevtools.library.logic.utils;

import android.content.Context;

import java.lang.reflect.Field;

public class AppBuildConfig {

    /**
     * Gets a field from the project's BuildConfig. This is useful when, for example, flavors
     * are used at the project level to set custom fields.
     * @param context       Used to find the correct file
     * @param fieldName     The name of the field-to-access
     * @return              The value of the field, or {@code null} if the field is not found.
     */
    public static Object getObjectValue(Context context, String fieldName) {
        try {
            Class<?> clazz = Class.forName(context.getPackageName() + ".BuildConfig");
            Field field = clazz.getField(fieldName);
            return field.get(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean getBooleanValue(Context context, String fieldName) {
        Object o = getObjectValue(context, fieldName);
        if (o != null && o instanceof Boolean) {
            return (Boolean) o;
        } else {
            return false;
        }
    }

    public static int getIntValue(Context context, String fieldName) {
        Object o = getObjectValue(context, fieldName);
        if (o != null && o instanceof Integer) {
            return (Integer) o;
        } else {
            return Integer.MIN_VALUE;
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
