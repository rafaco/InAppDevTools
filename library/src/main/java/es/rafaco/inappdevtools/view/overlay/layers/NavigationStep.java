package es.rafaco.inappdevtools.view.overlay.layers;

import es.rafaco.inappdevtools.view.overlay.screens.OverlayScreen;

public class NavigationStep{
    private final Class<? extends OverlayScreen> className;
    private final String param;

    public NavigationStep(Class<? extends OverlayScreen> className, String param) {
        this.className = className;
        this.param = param;
    }

    public Class<? extends OverlayScreen> getClassName() {
        return className;
    }

    public String getStringClassName() {
        return getClassName().getSimpleName();
    }

    public String getParam() {
        return param;
    }
}