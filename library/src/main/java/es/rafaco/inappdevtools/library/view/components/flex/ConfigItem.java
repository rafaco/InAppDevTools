package es.rafaco.inappdevtools.library.view.components.flex;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.BuildConfig;

public class ConfigItem {

    private final BuildConfig config;
    private Object initialValue;
    private Object newValue;

    public ConfigItem(BuildConfig config) {
        this.config = config;
        this.initialValue = IadtController.get().getConfig().get(config);
    }

    public BuildConfig getConfig() {
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
