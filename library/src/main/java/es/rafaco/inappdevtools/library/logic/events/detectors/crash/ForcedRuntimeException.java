package es.rafaco.inappdevtools.library.logic.events.detectors.crash;

import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;

public class ForcedRuntimeException extends RuntimeException {

    public ForcedRuntimeException() {
        super(getDefaultMessage());
    }

    public ForcedRuntimeException(Exception cause) {
        super(getDefaultMessage(), cause);
    }

    public ForcedRuntimeException(String message) {
        super(message);
    }

    public ForcedRuntimeException(String message, Exception cause) {
        super(message, cause);
    }

    private static String getDefaultMessage(){
        return String.format("Forced crash on %s thread.", ThreadUtils.formatCurrentName());
    }
}
