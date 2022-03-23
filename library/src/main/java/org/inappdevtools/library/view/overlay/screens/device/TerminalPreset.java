/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.view.overlay.screens.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TerminalPreset {

    LOGCAT_LAST("Logcat: last 10 logs", "logcat -d -t 9 *:V"),
    LOGCAT_SINCE("Logcat: since date", "logcat -t '06-27 16:06:00.000' *:V"),
    LOGCAT_CLEAR("Logcat: clear buffer", "logcat -c"),
    LINUX_STAT("Linux: stat", "cat /proc/stat"),
    LINUX_TOP("Linux: top -n 1", "top -n 1"),
    LINUX_MEMINFO("Linux: meminfo", "cat /proc/meminfo"),
    LINUX_VERSION("Linux: version", "cat /proc/version");

    private final String label;
    private final String command;

    TerminalPreset(String label, String command) {
        this.label = label;
        this.command = command;
    }

    public String getLabel() {
        return label;
    }

    public String getCommand() {
        return command;
    }



    private static final Map<String, TerminalPreset> ITEMS = new HashMap<>();

    public static TerminalPreset getByLabel(String name) {
        return ITEMS.get(name);
    }

    public static List<TerminalPreset> getAll() {
        return (List<TerminalPreset>) ITEMS.values();
    }

    public static List<TerminalPreset> getAllLabels() {
        return (List<TerminalPreset>) ITEMS.values();
    }

    static {
        for (TerminalPreset item : values()) {
            ITEMS.put(item.label, item);
        }
    }
}
