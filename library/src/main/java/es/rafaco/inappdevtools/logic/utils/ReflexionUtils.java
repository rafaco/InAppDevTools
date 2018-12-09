package es.rafaco.inappdevtools.logic.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.R;

public class ReflexionUtils {

    private <T> T castObject(Class<T> clazz, Object object) {
        return (T) object;
    }

    public <T> T castFromString(String className, Object object) {
        try {
            Class<T> theClass = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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