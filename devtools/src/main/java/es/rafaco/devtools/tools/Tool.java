package es.rafaco.devtools.tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public abstract class Tool {
    private final ToolsManager manager;
    private ViewGroup toolView;
    private boolean isInitialized = false;

    public abstract String getTitle();
    public abstract String getLayoutId();
    protected abstract void onInit();
    protected abstract void onStart(View toolView);
    protected abstract void onStop();
    protected abstract void onDestroy();

    public Tool(ToolsManager manager) {
        this.manager = manager;
        onInit();
    }

    public void start(){
        //init(LayoutInflater.from(containerView.getContext()));
        int layoutId = getResourceId(getContainer(), "layout", getLayoutId());
        toolView = (ViewGroup) getInflater().inflate(layoutId, getContainer(), false);
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

    public ViewGroup getContainer() {
        return manager.getContainer();
    }
    public LayoutInflater getInflater() {
        return manager.getInflater();
    }
    public ViewGroup getView() {
        return toolView;
    }

    protected int getResourceId(View view, String resourceType, String identifier){
        return view.getResources().getIdentifier(
                identifier,
                resourceType,
                view.getContext().getPackageName());
    }
}
