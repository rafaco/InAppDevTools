package es.rafaco.inappdevtools.demo;

import android.app.Application;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;

public class DemoApp extends Application {

    public static String TAG = "DemoApp";
    private int threadCounter = 0;

    public void onCreate() {
        super.onCreate();

        Iadt.addRunButton(new RunButton("Show message",
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

        Iadt.addRunButton(new RunButton("Select API...",
                R.drawable.ic_settings_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        Iadt.showMessage("Not already implemented");
                    }
                }));

        Iadt.addRunButton(new RunButton("Add dummy thread",
                R.drawable.ic_application_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        threadCounter++;
                        ThreadUtils.addDummy("DemoDummy " + threadCounter, 60000, new Runnable() {
                            @Override
                            public void run() {
                                Iadt.showMessage("Finished DemoDummy " + threadCounter);
                            }
                        });
                    }
                }));

        Iadt.addRunButton(new RunButton("Add dummy async",
                R.drawable.ic_application_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        threadCounter++;
                        ThreadUtils.addDummyAsync("DemoDummy " + threadCounter, 60000, new Runnable() {
                            @Override
                            public void run() {
                                Iadt.showMessage("Finished DemoDummy " + threadCounter);
                            }
                        });
                    }
                }));
    }
}
