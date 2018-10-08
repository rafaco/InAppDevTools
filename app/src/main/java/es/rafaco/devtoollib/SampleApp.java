package es.rafaco.devtoollib;

import android.app.Application;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.DevToolsConfig;


public class SampleApp extends Application {

    public static String TAG = "SampleApp";

    public void onCreate() {
        super.onCreate();

        DevTools.install(this, DevToolsConfig.newBuilder()
            .addEmail("rafaco@gmail.com")
            .build()
        );
    }
}
