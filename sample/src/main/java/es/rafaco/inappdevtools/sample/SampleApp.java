package es.rafaco.inappdevtools.sample;

import android.app.Application;

import es.rafaco.inappdevtools.sample.R;
import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.DevToolsConfig;
import es.rafaco.inappdevtools.library.logic.integrations.RunnableConfig;


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
                new Runnable() {
                    @Override
                    public void run() {
                        DevTools.showMessage("Mostrando mensaje");
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        DevTools.showMessage("Mensaje mostrado");
                    }
                }));

        DevTools.addCustomRunnable(new RunnableConfig("Select API...",
                R.drawable.ic_settings_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        DevTools.showMessage("Not already implemented");
                    }
                }));
    }
}
