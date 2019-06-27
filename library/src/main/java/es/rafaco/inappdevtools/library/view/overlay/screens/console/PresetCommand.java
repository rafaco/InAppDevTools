package es.rafaco.inappdevtools.library.view.overlay.screens.console;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PresetCommand {

    LOGCAT_LAST("Logcat: last 10 logs", "logcat -d -t 9 *:V"),
    LOGCAT_SINCE("Logcat: since date", "logcat -t '06-27 16:06:00.000' *:V"),
    LOGCAT_CLEAR("Logcat: clear buffer", "logcat -c"),
    LINUX_STAT("Linux: stat", "cat /proc/stat"),
    LINUX_TOP("Linux: top -n 1", "top -n 1"),
    LINUX_MEMINFO("Linux: meminfo", "cat /proc/meminfo"),
    LINUX_VERSION("Linux: version", "cat /proc/version");

    private final String label;
    private final String command;

    PresetCommand(String label, String command) {
        this.label = label;
        this.command = command;
    }

    public String getLabel() {
        return label;
    }

    public String getCommand() {
        return command;
    }



    private static final Map<String, PresetCommand> ITEMS = new HashMap<>();

    public static PresetCommand getByLabel(String name) {
        return ITEMS.get(name);
    }

    public static List<PresetCommand> getAll() {
        return (List<PresetCommand>) ITEMS.values();
    }

    public static List<PresetCommand> getAllLabels() {
        return (List<PresetCommand>) ITEMS.values();
    }

    static {
        for (PresetCommand item : values()) {
            ITEMS.put(item.label, item);
        }
    }
}
