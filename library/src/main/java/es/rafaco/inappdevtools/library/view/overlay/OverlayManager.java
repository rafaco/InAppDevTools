package es.rafaco.inappdevtools.library.view.overlay;

import android.content.Context;
import android.content.res.Configuration;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;

public class OverlayManager {

    private final Context context;
    private final LayerManager layerManager;
    private final ScreenManager screenManager;

    public OverlayManager(Context context) {
        this.context = context;
        this.layerManager = new LayerManager(context);
        this.screenManager = new ScreenManager(context, layerManager.getMainLayer());


        subscribeNewSyncWithAppImportance();
    }



    //region [ INTERNAL NAVIGATION ]

    protected void showToggle() {
        if (screenManager.getCurrentScreen() == null){
            screenManager.goHome();
        }

        layerManager.toggleMainLayerVisibility(null);
    }

    protected void showMain() {
        if (screenManager.getCurrentScreen() == null){
            screenManager.goHome();
        }

        layerManager.toggleMainLayerVisibility(true);
    }

    protected void showIcon() {
        layerManager.toggleMainLayerVisibility(false);
    }

    protected void navigateHome() {
        screenManager.goHome();
    }

    protected void navigateTo(String name) {
        navigateTo(name, null);
    }

    protected void navigateTo(String name, String param) {
        layerManager.toggleMainLayerVisibility(true);
        screenManager.goTo(name, param);
    }

    protected void navigateBack() {
        screenManager.goBack();
    }

    private void resume() {
        if (screenManager.getCurrentScreen() != null)
            screenManager.getCurrentScreen().resume();
        layerManager.toggleAllLayerVisibility(true);
    }

    private void pause() {
        if (screenManager.getCurrentScreen() != null)
            screenManager.getCurrentScreen().pause();
        layerManager.toggleAllLayerVisibility(false);
    }

    //endregion

    //region [ LIFECYCLE FROM SERVICE ]

    protected void onConfigurationChanged(Configuration newConfig) {
        if (layerManager != null){
            layerManager.onConfigurationChanged(newConfig);
        }

        if (screenManager != null){
            screenManager.onConfigurationChanged(newConfig);
        }
    }

    //endregion


    //region [ EVENTS SUBSCRIBERS ]

    private boolean isForeground = true;
    private boolean isSuspended = false;

    public void subscribeNewSyncWithAppImportance() {

        getController().getEventManager().subscribe(Event.IMPORTANCE_FOREGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (!isForeground && isSuspended) {
                    isForeground = true;
                    isSuspended = false;
                    resume();
                }
            }
        });

        getController().getEventManager().subscribe(Event.IMPORTANCE_BACKGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (isForeground) {
                    isForeground = false;
                    isSuspended = true;
                    pause();
                }
            }
        });
    }

    //endregion

    private Context getContext() {
        return context;
    }

    private IadtController getController() {
        return IadtController.get();
    }

    public void destroy() {
        if (screenManager != null) screenManager.destroy();
        if (layerManager != null) layerManager.destroy();
    }
}
