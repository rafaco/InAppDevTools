package es.rafaco.inappdevtools.library.logic.events;

public abstract class EventDetector extends EventSubscriber {

    public EventDetector(EventManager eventManager) {
        super(eventManager);
    }

    /**
     * Called after creation
     */
    public abstract void subscribe();

    /**
     * Called by EventDetectorsManager before all initializations.
     * Allow to start the watcher manually. Useful after calling {@link #stop()}.
     */
    public abstract void start();

    /**
     * Called by EventDetectorsManager on destroy.
     * Allow to stop the watcher manually. Use {@link #start()} to restart it.
     */
    public abstract void stop();

}
