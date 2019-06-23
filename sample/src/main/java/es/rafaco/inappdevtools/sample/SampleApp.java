package es.rafaco.inappdevtools.sample;

import android.app.Application;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.runnables.RunnableItem;


public class SampleApp extends Application {

    public static String TAG = "SampleApp";

    public void onCreate() {
        super.onCreate();

        //Iadt.codePoint(this);

        Iadt.addCustomRunnable(new RunnableItem("Show message",
                R.drawable.ic_run_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        Iadt.showMessage("Mostrando mensaje");
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        Iadt.showMessage("Mensaje mostrado");
                    }
                }));

        Iadt.addCustomRunnable(new RunnableItem("Select API...",
                R.drawable.ic_settings_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        Iadt.showMessage("Not already implemented");
                    }
                }));
    }
}
