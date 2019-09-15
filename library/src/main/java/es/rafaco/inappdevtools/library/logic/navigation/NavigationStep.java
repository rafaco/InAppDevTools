package es.rafaco.inappdevtools.library.logic.navigation;

import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;

public class NavigationStep{
    private final Class<? extends Screen> className;
    private String params;

    public NavigationStep(Class<? extends Screen> className, String params) {
        this.className = className;
        this.params = params;
    }

    public Class<? extends Screen> getClassName() {
        return className;
    }

    public String getStringClassName() {
        return getClassName().getSimpleName();
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public String toString(){
       return "NavigationStep [" + getStringClassName()+ "] : " + getParams();
    }
}
