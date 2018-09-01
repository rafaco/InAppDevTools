package es.rafaco.devtools.utils;

import java.lang.reflect.InvocationTargetException;

public class ClassHelper<T> {

    public T createClass(Class<? extends T> targetClass, Class fromClass, Object ... initargs) {
        try {
            T screenObject = targetClass
                    .getConstructor(fromClass)
                    .newInstance(initargs);
            return screenObject;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
