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

package es.rafaco.inappdevtools.library.logic.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class ReflexionUtils {

    private <T> T castObject(Class<T> clazz, Object object) {
        return (T) object;
    }

    public <T> T castFromString(String className, Object object) {
        try {
            Class<T> theClass = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            FriendlyLog.logException("Exception", e);
        }
        //Object obj = theClass.cast(object);
        return (T) object;
    }

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
            FriendlyLog.logException("Exception", e);
        } catch (NoSuchFieldException e) {
            result = false;
            FriendlyLog.logException("Exception", e);
        } catch (IllegalArgumentException e) {
            result = false;
            FriendlyLog.logException("Exception", e);
        } catch (IllegalAccessException e) {
            result = false;
            FriendlyLog.logException("Exception", e);
        }

        return result;
    }

    /**
     * Finds the resource ID for the current application's resources.
     * @param Rclass Resource class to find resource in.
     * Example: R.string.class, R.layout.class, R.drawable.class
     * @param name Name of the resource to search for.
     * @return The id of the resource or -1 if not found.
     */
    public static int getResourceByName(Class<?> Rclass, String name) {
        int id = -1;
        try {
            if (Rclass != null) {
                final Field field = Rclass.getField(name);
                if (field != null)
                    id = field.getInt(null);
            }
        } catch (final Exception e) {
            FriendlyLog.logException("Exception", e);
        }
        return id;
    }

    public static Field[] getResources(Class<?> Rclass) {
        Field[] fields = new Field[0];
        try {
            if (Rclass != null) {
                fields = Rclass.getFields();
            }
        } catch (Exception e) {
            FriendlyLog.logException("Exception", e);
        }
        return fields;
    }

    public static List<String> getResourceNames(Class<?> Rclass) {
        List<String> result = new ArrayList<>();
        final Field[] fields = getResources(Rclass);
        try {
            if (fields != null && fields.length>0){
                for (Field field : fields) {
                    result.add(field.getName());
                }
            }
        }
        catch (Exception e) {
            FriendlyLog.logException("Exception", e);
        }
        return result;
    }

    public static List<String> getFewResourceNames() {
        List<String> result = new ArrayList<>();
        result.addAll(getResourceNames(R.string.class));
        result.addAll(getResourceNames(R.layout.class));
        result.addAll(getResourceNames(R.drawable.class));
        return result;
    }

    public static void getAllDeclaredClasses(Class<?> Rclass) {
        Class[] nested = Rclass.getDeclaredClasses();

        for (int i=0; i < nested.length; i++) {
            System.out.print(nested[i]);
            if(java.lang.reflect.Modifier.isStatic(nested[i].getModifiers())) {
                System.out.println(" is nested");
            }
            else {
                System.out.println(" is nested inner");
            }
        }
    }
}
