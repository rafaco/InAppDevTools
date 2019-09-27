package es.rafaco.inappdevtools.library.view.overlay.screens;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

//#ifdef ANDROIDX
//@import androidx.appcompat.widget.Toolbar;
//#else
import android.support.v7.widget.Toolbar;
//#endif


import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;

public abstract class Screen implements Toolbar.OnMenuItemClickListener {

    private final ScreenManager manager;
    private final ViewGroup bodyContainer2;
    public ViewGroup headView;
    public ViewGroup bodyView;
    private ViewGroup headContainer;
    private ViewGroup bodyContainer;

    //Abstract method, have to be define by implementation
    public abstract String getTitle();
    public abstract int getBodyLayoutId();
    protected abstract void onCreate();
    protected abstract void onStart(ViewGroup toolHead);
    protected abstract void onStop();
    protected abstract void onDestroy();

    //Default methods, can be redefine by implementation
    public boolean isMain() {
        return true;
    }
    public boolean needNestedScroll() {
        return true;
    }
    public int getToolbarLayoutId() {
        return -1;
    }
    public int getHeadLayoutId() { return -1;}
    public boolean canGoBack() { return true; }

    public Screen(ScreenManager manager) {
        this.manager = manager;
        headContainer = getView().findViewById(R.id.tool_head_container);
        bodyContainer = getView().findViewById(R.id.tool_body_container);
        bodyContainer2 = getView().findViewById(R.id.tool_body_container2);
        onCreate();
    }

    public void start(){
        if (getHeadLayoutId() != -1){
            headView = (ViewGroup) getInflater().inflate(getHeadLayoutId(), headContainer, false);
            headView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            toggleHeadVisibility(false);
            headContainer.addView(headView);
        }

        ViewGroup container = needNestedScroll() ? bodyContainer : bodyContainer2;
        bodyView = (ViewGroup) getInflater().inflate(getBodyLayoutId(), container, false);
        bodyView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        toggleBodyVisibility(false);
        container.addView(bodyView);

        onStart(getView());
    }

    public void stop(){
        if (headView !=null) headContainer.removeView(headView);
        if (bodyView !=null) bodyContainer.removeView(bodyView);
        if (bodyView !=null) bodyContainer2.removeView(bodyView);
        onStop();
    }

    public void toggleVisibility(boolean show){
        toggleHeadVisibility(show);
        toggleBodyVisibility(show);
        if (bodyView !=null) bodyView.setVisibility(View.VISIBLE);
    }

    public void toggleHeadVisibility(boolean show){
        int visibility = show ? View.VISIBLE : View.GONE;
        if (headView !=null) {
            headView.setVisibility(visibility);
            onHeadVisibilityChanged(visibility);
        }
    }

    protected void onHeadVisibilityChanged(int visibility) {

    }

    public void toggleBodyVisibility(boolean show){
        int visibility = show ? View.VISIBLE : View.GONE;
        if (bodyView !=null) bodyView.setVisibility(visibility);
    }

    public boolean haveHead() {
        return getHeadLayoutId() != -1 && headView !=null;
    }

    public void destroy(){
        onDestroy();
    }

    public String getParam() {
        return getScreenManager().getCurrentStepParams();
    }

    public void updateParams(String newParams) {
        getScreenManager().updateCurrentStepParams(newParams);
    }

    public ScreenManager getScreenManager() {
        return manager;
    }
    public Context getContext() {
        return getScreenManager().getContext();
    }
    public LayoutInflater getInflater() {
        return getScreenManager().getInflater();
    }
    public ViewGroup getView() {
        return getScreenManager().getView();
    }
    protected Toolbar getToolbar() {
        return getScreenManager().getScreenToolbar();
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}