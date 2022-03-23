/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.inappdevtools.library.view.overlay.screens;

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


import org.inappdevtools.library.logic.log.FriendlyLog;
import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.R;

import org.inappdevtools.library.view.overlay.ScreenManager;

public abstract class Screen implements Toolbar.OnMenuItemClickListener {

    private final ScreenManager manager;
    private final ViewGroup bodyContainer2;
    public ViewGroup headView;
    public ViewGroup bodyView;
    private ViewGroup headContainer;
    private ViewGroup bodyContainer;
    private boolean isStarted = false;
    private boolean isPaused = false;

    //Lifecycle callback methods
    protected abstract void onCreate();
    protected abstract void onStart(ViewGroup bodyView);
    protected void onResume() {}
    public void onConfigurationChanged(Configuration newConfiguration) { }
    protected void onPause() {}
    protected abstract void onStop();
    protected abstract void onDestroy();

    //Configuration methods
    public abstract String getTitle();
    public abstract int getBodyLayoutId();
    public int getToolbarLayoutId() {
        return -1;
    }
    public int getHeadLayoutId() { return -1;}
    public boolean needNestedScroll() {
        return true;
    }
    public boolean isMain() {
        return true;
    }
    public boolean canGoBack() { return true; }
    protected Class<? extends Screen> getMasterScreenClass(){
        return null;
    }

    public Screen(ScreenManager manager) {
        this.manager = manager;
        headContainer = getView().findViewById(R.id.tool_head_container);
        bodyContainer = getView().findViewById(R.id.tool_body_container);
        bodyContainer2 = getView().findViewById(R.id.tool_body_container2);

        create();
    }

    private void create(){
        if (IadtController.get().isDebug())
            FriendlyLog.log("D", "Iadt", "Screen",
                    this.getClass().getSimpleName() + " create");
        onCreate();
    }

    public void start(){
        if (IadtController.get().isDebug())
            FriendlyLog.log("D", "Iadt", "Screen",
                    this.getClass().getSimpleName() + " start");
        
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
        //container.getChildAt(container.getChildCount()-1);
        isStarted = true;
        onStart(bodyView);
        resume();
    }

    public void resume() {
        if (isStarted){
            if (IadtController.get().isDebug())
                FriendlyLog.log("D", "Iadt", "Screen",
                        this.getClass().getSimpleName() + " resume");

            onResume();
            isPaused = false;
        }
    }

    public void pause() {
        if (isStarted && !isPaused){
            if (IadtController.get().isDebug())
                FriendlyLog.log("D", "Iadt", "Screen",
                        this.getClass().getSimpleName() + " pause");
            
            onPause();
            isPaused = true;
        }
    }

    public void stop(){
        pause();

        if (isStarted){
            if (IadtController.get().isDebug())
                FriendlyLog.log("D", "Iadt", "Screen",
                        this.getClass().getSimpleName() + " stop");
            if (headView !=null) headContainer.removeView(headView);
            if (bodyView !=null) bodyContainer.removeView(bodyView);
            if (bodyView !=null) bodyContainer2.removeView(bodyView);

            onStop();
            isStarted = false;
        }
    }

    public void destroy(){
        pause();
        stop();
        if (IadtController.get().isDebug())
            FriendlyLog.log("D", "Iadt", "Screen",
                    this.getClass().getSimpleName() + " destroy");

        onDestroy();
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

    public void showProgress(boolean isVisible){
        getScreenManager().getScreenLayer().showProgress(isVisible);
    }

    public boolean haveHead() {
        return getHeadLayoutId() != -1 && headView !=null;
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    public boolean isDebug() {
        return IadtController.get().isDebug();
    }
}
