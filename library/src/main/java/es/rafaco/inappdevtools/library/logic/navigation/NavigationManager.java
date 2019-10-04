package es.rafaco.inappdevtools.library.logic.navigation;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.HomeScreen;

public class NavigationManager {

    private List<NavigationStep> history;

    public NavigationManager() {
        this.history = new ArrayList<>();
    }

    public void addStep(Class<? extends Screen> screenClass, final String params) {
        NavigationStep newStep = new NavigationStep(screenClass, params);
        addStep(newStep);
    }

    public void addStep(NavigationStep newStep) {
        //Ensure home screen is always at the the first for backward navigation
        if (history.isEmpty() &&
                !newStep.getClassName().equals(HomeScreen.class)){
            NavigationStep homeStep = new NavigationStep(HomeScreen.class, null);
            history.add(homeStep);
        }

        history.add(newStep);
        IadtController.get().getEventManager().fire(Event.OVERLAY_NAVIGATION, newStep);
    }

    public void clearSteps(){
        history.clear();
    }

    public NavigationStep removeStep(){
        if (history.isEmpty()){
           return null;
        }
        return history.remove(history.size() - 1);
    }

    public NavigationStep getCurrent(){
        if (history.isEmpty()){
            return null;
        }
        return history.get(history.size() - 1);
    }

    public boolean isCurrentScreen(Class<? extends Screen> screenClass){
        if (getCurrent() != null
                && getCurrent().getClassName().equals(screenClass)){
            return true;
        }
        return false;
    }

    public String getCurrentParams(){
        return getCurrent().getParams();
    }

    public void updateCurrentParams(String newParams){
        getCurrent().setParams(newParams);
    }

    public boolean isHome() {
        return history.isEmpty() || history.size() == 1;
    }
}
