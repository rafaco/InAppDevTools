package es.rafaco.devtools.logic.utils;

import java.lang.reflect.Field;

public class ReflexionUtils {

    /**
     * Use to set the value of a field you don't have access to, using reflection, for unit testing.
     *
     * Returns true/false for success/failure.
     *
     * @param p_instance an object to set a private field on
     * @param p_fieldName the name of the field to set
     * @param p_fieldValue the value to set the field to
     * @return true/false for success/failure
     */
    public static boolean setPrivateField(final Object p_instance, final String p_fieldName, final Object p_fieldValue) {
        if (null == p_instance)
            throw new NullPointerException("p_instance can't be null!");
        if (null == p_fieldName)
            throw new NullPointerException("p_fieldName can't be null!");

        boolean result = true;

        Class<?> klass = p_instance.getClass();

        Field field = null;
        try {
            field = klass.getDeclaredField(p_fieldName);
            field.setAccessible(true);
            field.set(p_instance, p_fieldValue);

        } catch (SecurityException e) {
            result = false;
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            result = false;
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            result = false;
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            result = false;
            e.printStackTrace();
        }

        return result;
    }
}
