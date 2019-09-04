package es.rafaco.inappdevtools.library.logic.events.subscribers;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.Config;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.events.EventSubscriber;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class ConfigChangeSubscriber extends EventSubscriber {

    public ConfigChangeSubscriber(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {

        eventManager.subscribe(Event.CONFIG_CHANGED, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                Config target = (Config) param;

                FriendlyLog.log("D", "Iadt", "Config",
                        "Config changed: " + target.getKey() + " to " + IadtController.get().getConfig().get(target));

                /*if (target.getKey().equals(Config.ENABLED.getKey())){
                    //TODO: restart library instead of app
                    Iadt.showMessage("Restart needed");
                    IadtController.get().restartApp(false);
                }*/
            }
        });
    }
}
