package es.rafaco.inappdevtools.library.view.overlay.navigation;

import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;

public class NavigationStep{
    private final Class<? extends Screen> className;
    private final String param;

    public NavigationStep(Class<? extends Screen> className, String param) {
        this.className = className;
        this.param = param;
    }

    public Class<? extends Screen> getClassName() {
        return className;
    }

    public String getStringClassName() {
        return getClassName().getSimpleName();
    }

    public String getParam() {
        return param;
    }

    @Override
    public String toString(){
       return "NavigationStep [" + getStringClassName()+ "] : " + getParam();
    }
}
