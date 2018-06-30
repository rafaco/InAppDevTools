package es.rafaco.devtools.view.overlay.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import es.rafaco.devtools.view.overlay.OverlayToolsManager;

public abstract class OverlayTool {
    private final OverlayToolsManager manager;
    private ViewGroup toolView;

    public abstract String getTitle();
    public abstract int getLayoutId();
    protected abstract void onInit();
    protected abstract void onStart(View toolView);
    protected abstract void onStop();
    protected abstract void onDestroy();

    public OverlayTool(OverlayToolsManager manager) {
        this.manager = manager;
        onInit();
    }

    public void start(){
        toolView = (ViewGroup) getInflater().inflate(getLayoutId(), getContainer(), false);
        toolView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        getContainer().addView(toolView);
        onStart(toolView);
    }

    public void stop(){
        getContainer().removeView(toolView);
        onStop();
    }

    public void destroy(){
        onDestroy();
        getContainer().removeAllViews();
    }


    public LayoutInflater getInflater() {
        return manager.getInflater();
    }
    public ViewGroup getView() {
        return toolView;
    }
    public OverlayToolsManager getManager() {
        return manager;
    }
    public ViewGroup getContainer() {
        return manager.getContainer();
    }
    public Context getContext() {
        return manager.getContainer().getContext();
    }

    public DecoratedToolInfo getHomeInfo() {
        return null;
    }
    public DecoratedToolInfo getReportInfo() {
        return null;
    }
    public Object getReport() {
        return null;
    }

    protected String getFullTitle(){
        return getTitle() + " OverlayTool";
    }
}
