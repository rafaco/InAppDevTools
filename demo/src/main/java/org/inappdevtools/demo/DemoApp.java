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

package org.inappdevtools.demo;

import android.app.Application;

import org.inappdevtools.demo.R;

import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.view.components.items.ButtonFlexData;
import org.inappdevtools.library.logic.utils.ThreadUtils;

public class DemoApp extends Application {

    public static String TAG = "DemoApp";
    private int threadCounter = 0;

    public void onCreate() {
        super.onCreate();

        Iadt.addTeamAction(new ButtonFlexData("Show message",
                R.drawable.ic_run_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        Iadt.buildMessage("Mostrando mensaje...").fire();
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        Iadt.buildMessage("Mensaje mostrado!").fire();
                    }
                }));

        Iadt.addTeamAction(new ButtonFlexData("Select API...",
                R.drawable.ic_settings_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        Iadt.buildMessage("Not already implemented").fire();
                    }
                }));

        Iadt.addTeamAction(new ButtonFlexData("Add dummy thread",
                R.drawable.ic_application_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        threadCounter++;
                        ThreadUtils.addDummy("DemoDummy " + threadCounter, 60000, new Runnable() {
                            @Override
                            public void run() {
                                Iadt.buildMessage("Finished DemoDummy " + threadCounter).fire();
                            }
                        });
                    }
                }));

        Iadt.addTeamAction(new ButtonFlexData("Add dummy async",
                R.drawable.ic_application_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        threadCounter++;
                        ThreadUtils.addDummyAsync("DemoDummy " + threadCounter, 60000, new Runnable() {
                            @Override
                            public void run() {
                                Iadt.buildMessage("Finished DemoDummy " + threadCounter).fire();
                            }
                        });
                    }
                }));
    }
}
