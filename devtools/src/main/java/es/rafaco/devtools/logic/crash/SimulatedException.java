package es.rafaco.devtools.logic.crash;

import es.rafaco.devtools.utils.ThreadUtils;

public class SimulatedException extends RuntimeException {

    public SimulatedException() {
        super(String.format("User simulate a crash on %s thread.", ThreadUtils.getFormattedThread()));
    }

    public SimulatedException(String message) {
        super(message);
    }

    public SimulatedException(String s, Exception cause) {
        super(s, cause);
    }
}
