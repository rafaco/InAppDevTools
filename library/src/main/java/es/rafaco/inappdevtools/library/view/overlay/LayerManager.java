package es.rafaco.inappdevtools.library.view.overlay;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.BuildConfig;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.view.overlay.layers.IconLayer;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;
import es.rafaco.inappdevtools.library.view.overlay.layers.ScreenLayer;
import es.rafaco.inappdevtools.library.view.overlay.layers.RemoveLayer;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

public class LayerManager {

    private Context context;
    private WindowManager windowManager;
    private LayoutInflater inflater;
    private List<Layer> layers;
    private boolean isVisible;
    private boolean isMainVisible;

    public LayerManager(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        this.inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.layers = new ArrayList<>();

        initLayers();
    }

    //region [ LAYERS MANAGER ]

    private void initLayers() {
        if (isOverlayIconEnabled()){
            addLayer(new RemoveLayer(this));
            addLayer(new IconLayer(this));
        }
        addLayer(new ScreenLayer(this));
    }

    public void addLayer(Layer layer){
        layers.add(layer);
        layer.addView();
    }

    public View getView(Layer.Type widgetType){
        Layer layer = getLayer(widgetType);
        if (layer != null)
            return layer.getView();
        return null;
    }

    public Layer getLayer(Layer.Type widgetType){
        for (Layer layer : layers) {
            if (layer.getType().equals(widgetType)){
                return layer;
            }
        }
        return null;
    }

    public ScreenLayer getMainLayer(){
        return (ScreenLayer) getLayer(Layer.Type.SCREEN);
    }

    public IconLayer getIconLayer(){
        return (IconLayer) getLayer(Layer.Type.ICON);
    }

    public RemoveLayer getRemoveLayer(){
        return (RemoveLayer) getLayer(Layer.Type.REMOVE);
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public void destroy() {
        for (Layer layer : layers) {
            layer.destroy();
        }
    }

    //endregion

    //region [ TOGGLE LAYER VISIBILITY ]

    public void toggleMainLayerVisibility(Boolean showMain) {
        boolean previousShowMain = this.isMainVisible;

        if (showMain == null){
            showMain = !previousShowMain;
        }

        if (previousShowMain == showMain){
            return;
        }

        if (showMain) {
            getView(Layer.Type.SCREEN).setVisibility(View.VISIBLE);
            if (isOverlayIconEnabled()) {
                getView(Layer.Type.ICON).setVisibility(View.GONE);
                getView(Layer.Type.REMOVE).setVisibility(View.GONE);
            }
            IadtController.get().getEventManager().fire(Event.OVERLAY_FOREGROUND, null);
        }
        else {
            getView(Layer.Type.SCREEN).setVisibility(View.GONE);
            if (isOverlayIconEnabled())
                getView(Layer.Type.ICON).setVisibility(View.VISIBLE);
            IadtController.get().getEventManager().fire(Event.OVERLAY_BACKGROUND, null);
        }
        this.isMainVisible = showMain;
    }

    public void toggleAllLayerVisibility(boolean isVisible) {
        if (isVisible) {
            toggleMainLayerVisibility(isMainVisible);
        } 
        else {
            getView(Layer.Type.SCREEN).setVisibility(View.GONE);
            if (isOverlayIconEnabled()) {
                getView(Layer.Type.ICON).setVisibility(View.GONE);
                getView(Layer.Type.REMOVE).setVisibility(View.GONE);
            }
        }
        this.isVisible = isVisible;
    }

    //endregion

    public void onConfigurationChanged(Configuration newConfig) {
        for (Layer layer : layers) {
            layer.onConfigurationChange(newConfig);
        }
    }

    private boolean isOverlayIconEnabled() {
        return Iadt.getConfig().getBoolean(BuildConfig.INVOCATION_BY_ICON);
    }

    public Context getContext(){
        return context;
    }
}
