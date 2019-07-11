package es.rafaco.inappdevtools.sample;

import android.app.Application;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.runnables.RunnableItem;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;


public class SampleApp extends Application {

    public static String TAG = "SampleApp";
    private int threadCounter = 0;

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

        Iadt.addCustomRunnable(new RunnableItem("Add dummy thread",
                R.drawable.ic_application_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        threadCounter++;
                        ThreadUtils.addDummy("RAFA" + threadCounter, 60000, new Runnable() {
                            @Override
                            public void run() {
                                Iadt.showMessage("Finished RAFA" + threadCounter);
                            }
                        });
                    }
                }));

        Iadt.addCustomRunnable(new RunnableItem("Add dummy async",
                R.drawable.ic_application_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        threadCounter++;
                        ThreadUtils.addDummyAsync("RAFAA" + threadCounter, 60000, new Runnable() {
                            @Override
                            public void run() {
                                Iadt.showMessage("Finished RAFAA" + threadCounter);
                            }
                        });
                    }
                }));

        //Log.i(TAG, new LiveInfoHelper(getApplicationContext()).getRunningThreads());
    }
}
