package org.inappdevtools.library;

import android.content.Context;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.inappdevtools.library.logic.utils.ReflexionUtils;
import org.inappdevtools.library.BuildConfig;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("org.inappdevtools.library", getLibraryPackageName());
    }

    private String getLibraryPackageName() {
        // Starting from Android Studio 4, Gradle 6 or Android Gradle Plugin 4:
        // APPLICATION_ID has been deprecated for libraries and replaced by LIBRARY_PACKAGE_NAME

        if (ReflexionUtils.doesClassContainField(BuildConfig.class,
                "LIBRARY_PACKAGE_NAME")) {
            return ReflexionUtils.getStringFieldFromClass(BuildConfig.class,
                    "LIBRARY_PACKAGE_NAME");
        }
        else {
            return ReflexionUtils.getStringFieldFromClass(BuildConfig.class,
                    "APPLICATION_ID");
        }
    }
}
