package es.rafaco.inappdevtools.library.view.components.flex;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.Config;

public class ConfigItem {

    private final Config config;
    private Object initialValue;
    private Object newValue;

    public ConfigItem(Config config) {
        this.config = config;
        this.initialValue = IadtController.get().getConfig().get(config);
    }

    public Config getConfig() {
        return config;
    }

    public Object getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(Object initialValue) {
        this.initialValue = initialValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }
}
