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

import android.util.Log;

import org.inappdevtools.library.logic.utils.ThreadUtils;

import java.util.ArrayList;

import org.inappdevtools.library.Iadt;

public class CrashGenerator {

    public static void stackOverflow() {
        logAdvise("StackOverflow");
        generateStackOverflow();
    }

    @SuppressWarnings("InfiniteRecursion")
    private static void generateStackOverflow() {
        generateStackOverflow();
    }

    public static void indexOutOfBounds() {
        logAdvise("IndexOutOfBounds");
        generateIndexOutOfBounds();
    }

    private static void generateIndexOutOfBounds() {
        new ArrayList().get(1);
    }

    public static void arithmeticException() {
        logAdvise("ArithmeticException");
        generateArithmeticException();
    }

    @SuppressWarnings("NumericOverflow")
    private static void generateArithmeticException() {
        int i = 1/0;
    }

    public static void nullPointer() {
        logAdvise("ZeroDivision");
        generateNullPointer();
    }

    private static void generateNullPointer() {
        throw null;
    }

    private static void logAdvise(String exceptionType) {
        String message = String.format("Generating a %s exception", exceptionType);
        ThreadUtils.printOverview(message);
    }

    public static void generateAnr() {
        Log.i(Iadt.TAG, "ANR requested, sleeping main thread for a while...");
        ThreadUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep((long) 10 * 1000);
                } catch (InterruptedException e) {
                    Log.e(Iadt.TAG, "Something wrong happen", e);
                    Thread.currentThread().interrupt();
                }
            }
        });
    }
}

