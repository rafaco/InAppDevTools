/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.demo;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.crash.CrashGenerator;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;

public class DemoApp extends Application {

    public static String TAG = "DemoApp";
    private int threadCounter = 0;

    public void onCreate() {
        super.onCreate();

        Iadt.addRunButton(new RunButton("IndexOutOfBounds",
                R.drawable.ic_bug_report_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        CrashGenerator.indexOutOfBounds();
                    }
                }));

        Iadt.addRunButton(new RunButton("NullPointer",
                R.drawable.ic_bug_report_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        CrashGenerator.nullPointer();
                    }
                }));

        Iadt.addRunButton(new RunButton("ZeroDivision",
                R.drawable.ic_bug_report_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        CrashGenerator.arithmeticException();
                    }
                }));

        Iadt.addRunButton(new RunButton("StackOverflow",
                R.drawable.ic_bug_report_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        CrashGenerator.stackOverflow();
                    }
                }));


        Iadt.addRunButton(new RunButton("Show message",
                R.drawable.ic_run_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        Iadt.showMessage("Mostrando mensaje...");
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        Iadt.showMessage("Mensaje mostrado!");
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
