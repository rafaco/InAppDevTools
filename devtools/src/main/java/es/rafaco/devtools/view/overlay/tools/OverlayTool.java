package es.rafaco.devtools.view.overlay.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.OverlayToolsManager;

public abstract class OverlayTool {

    private final OverlayToolsManager manager;
    private ViewGroup toolBody;
    private ViewGroup toolHead;

    public abstract String getTitle();
    public int getHeadLayoutId() { return -1;}
    public abstract int getBodyLayoutId();

    protected abstract void onCreate();
    protected abstract void onStart(ViewGroup toolHead);
    protected abstract void onStop();
    protected abstract void onDestroy();

    public OverlayTool(OverlayToolsManager manager) {
        this.manager = manager;
        onCreate();
    }

    public void start(){
        ViewGroup targetContainer;

        if (getHeadLayoutId() != -1){
            targetContainer = getView().findViewById(R.id.tool_head_container);
            toolHead = (ViewGroup) getInflater().inflate(getHeadLayoutId(), targetContainer, false);
            toolHead.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            targetContainer.addView(toolHead);
        }

        targetContainer = getView().findViewById(R.id.tool_body_container);
        toolBody = (ViewGroup) getInflater().inflate(getBodyLayoutId(), targetContainer, false);
        toolBody.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        targetContainer.addView(toolBody);

        onStart(getView());
    }

    public void stop(){
        onStop();
        if (toolHead!=null)
            toolHead.removeAllViews();
        toolBody.removeAllViews();
    }

    public void destroy(){
        onDestroy();
        getView().removeAllViews();
    }


    public Context getContext() {
        return manager.getToolWrapper().getContext();
    }
    public OverlayToolsManager getManager() {
        return manager;
    }
    public LayoutInflater getInflater() {
        return manager.getInflater();
    }
    public ViewGroup getView() {
        return manager.getToolWrapper();
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
        return getTitle() + " Tool";
    }
}
