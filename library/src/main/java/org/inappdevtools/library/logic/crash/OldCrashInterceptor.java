/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

package org.inappdevtools.library.logic.crash;

import android.content.Context;
import android.util.Log;

import org.inappdevtools.library.logic.utils.ThreadUtils;
import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.IadtController;

//TODO: remove
public class OldCrashInterceptor {

    public static void initialise(Context context) {
        ThreadUtils.printOverview("startCrashHandler");

        Thread.UncaughtExceptionHandler globalDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (globalDefaultHandler != null && !globalDefaultHandler.getClass().isInstance(CrashHandler.class)) {
            Log.d(Iadt.TAG, "CurrentHandler global: " + globalDefaultHandler.toString());
            Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(context, globalDefaultHandler));
            if (IadtController.get().isDebug())
                Log.d(Iadt.TAG, "Exception handler added globally");
        }else{
            Log.w(Iadt.TAG, "Exception handler already attach on thread");
        }

        Thread.UncaughtExceptionHandler mainDefaultHandler = Thread.currentThread().getUncaughtExceptionHandler();
        Log.d(Iadt.TAG, "CurrentHandler global: " + mainDefaultHandler.toString());
        Thread.currentThread().setUncaughtExceptionHandler(new CrashHandler(context, mainDefaultHandler));
    }
}
