package es.rafaco.inappdevtools.library.logic.events.detectors.crash;

import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;

public class SimulatedException extends RuntimeException {

    public SimulatedException() {
        super(String.format("User simulate a crash on %s thread.", ThreadUtils.formatCurrentName()));
    }

    public SimulatedException(String message) {
        super(message);
    }

    public SimulatedException(String s, Exception cause) {
        super(s, cause);
    }
}
