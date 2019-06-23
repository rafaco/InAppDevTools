package es.rafaco.inappdevtools.library.logic.utils;

import java.lang.reflect.InvocationTargetException;

import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class ClassHelper<T> {

    public T createClass(Class<? extends T> targetClass, Class fromClass, Object ... initargs) {
        try {
            T screenObject = targetClass
                    .getConstructor(fromClass)
                    .newInstance(initargs);
            return screenObject;
        } catch (InstantiationException e) {
            FriendlyLog.logException("Exception", e);
        } catch (IllegalAccessException e) {
            FriendlyLog.logException("Exception", e);
        } catch (InvocationTargetException e) {
            FriendlyLog.logException("Exception", e);
        } catch (NoSuchMethodException e) {
            FriendlyLog.logException("Exception", e);
        }
        return null;
    }
}
