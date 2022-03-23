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

package org.inappdevtools.library.logic.events.detectors.app;

import android.os.AsyncTask;

import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;

import org.inappdevtools.library.storage.db.IadtDatabase;
import org.inappdevtools.library.storage.db.entities.Anr;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.events.EventDetector;
import org.inappdevtools.library.logic.events.EventManager;
import org.inappdevtools.library.logic.log.FriendlyLog;

public class ErrorAnrEventDetector extends EventDetector {

    private ANRWatchDog watchDog;

    public ErrorAnrEventDetector(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void start() {
        watchDog = new ANRWatchDog(2*1000)
                .setANRListener(new ANRWatchDog.ANRListener() {
                    @Override
                    public void onAppNotResponding(ANRError error) {
                        Anr anr = parseAnr(error);
                        storeAnr(anr);
                        eventManager.fire(Event.ERROR_ANR, anr);
                    }
                })
                .setIgnoreDebugger(false)
                .setReportMainThreadOnly();
        watchDog.start();
    }

    @Override
    public void stop() {
        //TODO:
        //if (watchDog.isAlive())
        watchDog.interrupt();
    }

    private Anr parseAnr(ANRError error) {
        String errorString;
        errorString = String.format("ANR ERROR: %s - %s", error.getMessage(), error.getCause());
        Iadt.buildMessage(errorString).isError().fire();

        Anr anr = new Anr();
        anr.setDate(new Date().getTime());
        anr.setMessage(error.getMessage());
        anr.setCause(error.getCause().toString());

        StringWriter sw = new StringWriter();
        error.printStackTrace(new PrintWriter(sw));
        String stackTraceString = sw.toString();
        anr.setStacktrace(stackTraceString);

        return anr;
    }

    private void storeAnr(final Anr anr) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                long anrId = IadtDatabase.get().anrDao().insert(anr);
                FriendlyLog.logAnr(anrId, anr);
            }
        });
    }
}
