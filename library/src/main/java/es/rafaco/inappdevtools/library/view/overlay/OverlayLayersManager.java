package es.rafaco.inappdevtools.library.view.overlay;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.config.Config;
import es.rafaco.inappdevtools.library.view.overlay.layers.IconOverlayLayer;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayer;
import es.rafaco.inappdevtools.library.view.overlay.layers.OverlayLayer;
import es.rafaco.inappdevtools.library.view.overlay.layers.RemoveOverlayLayer;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

public class OverlayLayersManager {

    private Context context;
    private WindowManager windowManager;
    private LayoutInflater inflater;
    private List<OverlayLayer> overlayLayers;
    private boolean isVisible;
    private boolean isMainVisible;

    public OverlayLayersManager(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        this.inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.overlayLayers = new ArrayList<>();

        initLayers();
    }

    //region [ LAYERS MANAGER ]

    private void initLayers() {
        if (isOverlayIconEnabled()){
            addLayer(new RemoveOverlayLayer(this));
            addLayer(new IconOverlayLayer(this));
        }
        addLayer(new MainOverlayLayer(this));
    }

    public void addLayer(OverlayLayer overlayLayer){
        overlayLayers.add(overlayLayer);
        overlayLayer.addView();
    }

    public View getView(OverlayLayer.Type widgetType){
        OverlayLayer overlayLayer = getLayer(widgetType);
        if (overlayLayer != null)
            return overlayLayer.getView();
        return null;
    }

    public OverlayLayer getLayer(OverlayLayer.Type widgetType){
        for (OverlayLayer overlayLayer : overlayLayers) {
            if (overlayLayer.getType().equals(widgetType)){
                return overlayLayer;
            }
        }
        return null;
    }

    public MainOverlayLayer getMainLayer(){
        return (MainOverlayLayer) getLayer(OverlayLayer.Type.MAIN);
    }

    public IconOverlayLayer getIconLayer(){
        return (IconOverlayLayer) getLayer(OverlayLayer.Type.ICON);
    }

    public RemoveOverlayLayer getRemoveLayer(){
        return (RemoveOverlayLayer) getLayer(OverlayLayer.Type.REMOVE);
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public void destroy() {
        for (OverlayLayer overlayLayer : overlayLayers) {
            overlayLayer.destroy();
        }
    }

    //endregion

    //region [ TOGGLE LAYER VISIBILITY ]

    public void toggleMainIconVisibility(Boolean isMainVisible) {
        if (isMainVisible == null){
            isMainVisible = !this.isMainVisible;
        }

        if (isMainVisible) {
            getView(OverlayLayer.Type.MAIN).setVisibility(View.VISIBLE);
            if (isOverlayIconEnabled())
                getView(OverlayLayer.Type.ICON).setVisibility(View.GONE);
        }
        else {
            getView(OverlayLayer.Type.MAIN).setVisibility(View.GONE);
            if (isOverlayIconEnabled())
                getView(OverlayLayer.Type.ICON).setVisibility(View.VISIBLE);
        }
        this.isMainVisible = isMainVisible;
    }

    public void toggleVisibility(boolean isVisible) {
        if (isVisible) {
            toggleMainIconVisibility(isMainVisible);
        } 
        else {
            getView(OverlayLayer.Type.MAIN).setVisibility(View.GONE);
            if (isOverlayIconEnabled())
                getView(OverlayLayer.Type.ICON).setVisibility(View.GONE);
        }
        this.isVisible = isVisible;
    }

    //endregion

    public void onConfigurationChanged(Configuration newConfig) {
        for (OverlayLayer overlayLayer : overlayLayers) {
            overlayLayer.onConfigurationChange(newConfig);
        }
    }

    private boolean isOverlayIconEnabled() {
        return Iadt.getConfig().getBoolean(Config.INVOCATION_BY_ICON);
    }

    public Context getContext(){
        return context;
    }
}
