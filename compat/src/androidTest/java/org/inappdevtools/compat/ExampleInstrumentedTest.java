package org.inappdevtools.compat;

import android.content.Context;

//#ifdef ANDROIDX
//@import androidx.test.InstrumentationRegistry;
//@import androidx.test.runner.AndroidJUnit4;
//#else
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
//#endif

import org.junit.Test;
import org.junit.runner.RunWith;

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

        assertEquals("org.inappdevtools.compat.test", appContext.getPackageName());
    }
}
