package es.rafaco.devtoollib;

import android.app.Application;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.DevToolsConfig;
import es.rafaco.devtools.logic.integrations.RunnableConfig;


public class SampleApp extends Application {

    public static String TAG = "SampleApp";

    public void onCreate() {
        super.onCreate();

        //TODO: we have currently a double install without and with configuration
        DevTools.install(this, DevToolsConfig.newBuilder()
            .addEmail("rafaco@gmail.com")
            .build()
        );

        //DevTools.breakpoint(this);

        DevTools.addCustomRunnable(new RunnableConfig("Show message",
                R.drawable.ic_run_white_24dp,
                () -> DevTools.showMessage("Mostrando mensaje"),
                () -> DevTools.showMessage("Mensaje mostrado")));

        DevTools.addCustomRunnable(new RunnableConfig("Select API...",
                R.drawable.ic_settings_white_24dp,
                () -> DevTools.showMessage("Not already implemented")));
    }
}
