package es.rafaco.devtools.view.overlay.screens;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.OverlayScreenManager;
import es.rafaco.devtools.view.DecoratedToolInfo;

public abstract class OverlayScreen {

    private final OverlayScreenManager manager;
    private ViewGroup headView;
    private ViewGroup bodyView;

    //Abstract constants to define by implementation
    public abstract String getTitle();
    protected String getFullTitle(){
        return getTitle() + " Tool";
    }
    public String getName() { return getTitle(); }
    public boolean isMain() {
        return true;
    }
    public int getHeadLayoutId() { return -1;}
    public abstract int getBodyLayoutId();

    //Abstract logic to define by implementation
    protected abstract void onCreate();
    protected abstract void onStart(ViewGroup toolHead);
    protected abstract void onStop();
    protected abstract void onDestroy();

    public OverlayScreen(OverlayScreenManager manager) {
        this.manager = manager;
        onCreate();
    }

    public void start(){
        ViewGroup targetContainer;

        if (getHeadLayoutId() != -1){
            targetContainer = getView().findViewById(R.id.tool_head_container);
            headView = (ViewGroup) getInflater().inflate(getHeadLayoutId(), targetContainer, false);
            headView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            targetContainer.removeAllViews();
            targetContainer.addView(headView);
        }

        targetContainer = getView().findViewById(R.id.tool_body_container);
        bodyView = (ViewGroup) getInflater().inflate(getBodyLayoutId(), targetContainer, false);
        bodyView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        targetContainer.removeAllViews();
        targetContainer.addView(bodyView);

        onStart(getView());
    }

    public void stop(){
        onStop();
        //getView().removeAllViews();
        if (headView !=null) headView.removeAllViews();
        if (headView !=null) bodyView.removeAllViews();
    }

    public void destroy(){
        onDestroy();
    }


    public OverlayScreenManager getManager() {
        return manager;
    }
    public Context getContext() {
        return getManager().getContext();
    }
    public LayoutInflater getInflater() {
        return getManager().getInflater();
    }
    public ViewGroup getView() {
        return getManager().getView();
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
}
