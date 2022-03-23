/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.view.overlay;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.appcompat.widget.Toolbar;
//#else
import android.support.v7.widget.Toolbar;
//#endif

import org.inappdevtools.library.logic.log.FriendlyLog;
import org.inappdevtools.library.logic.navigation.NavigationManager;
import org.inappdevtools.library.logic.navigation.NavigationStep;
import org.inappdevtools.library.logic.utils.ClassHelper;
import org.inappdevtools.library.logic.utils.ThreadUtils;
import org.inappdevtools.library.view.overlay.layers.ScreenLayer;
import org.inappdevtools.library.view.overlay.screens.Screen;
import org.inappdevtools.library.view.overlay.screens.home.HomeScreen;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.R;
import org.inappdevtools.library.IadtController;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ScreenManager {

    protected Context context;
    private final NavigationManager navigationManager;
    private final ScreenLayer screenLayer;
    private static Screen currentScreen = null;
    private final LayoutInflater inflater;
    private List<Class<? extends Screen>> registeredScreens;

    private Screen loadedScreen = null;
    private Toolbar screenToolbar;

    public ScreenManager(Context context, ScreenLayer screenLayer) {
        this.context = context;
        this.screenLayer = screenLayer;
        this.inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.registeredScreens = new ArrayList<>();
        this.screenToolbar = getView().findViewById(R.id.tool_toolbar);
        this.navigationManager = IadtController.get().getNavigationManager();

        ScreensLoader.registerAllScreens(this);

        if (IadtController.get().isDebug())
            ThreadUtils.printOverview("ScreenManager");
    }

    //region [ SCREENS MANAGER ]

    public void registerScreen(Class<? extends Screen> screenClass){
        registeredScreens.add(screenClass);
    }

    public Class<? extends Screen> getScreenClass(String name){
        for (Class<? extends Screen> screen : registeredScreens){
            if (screen.getSimpleName().equals(name)){
                return screen;
            }
        }
        return null;
    }

    private void setCurrentScreen(Screen screen) {
        currentScreen = screen;
    }

    public Screen getCurrentScreen(){
        return currentScreen;
    }

    public String getCurrentScreenString(){
        if (getCurrentScreen() == null)
            return null;
        return currentScreen.getClass().getSimpleName();
    }

    public static Class<? extends Screen> getHomeClass() {
        return HomeScreen.class;
    }

    //endregion

    //region [ NAVIGATION ]

    public void goTo(String screenName, String param){
        Class<? extends Screen> screenClass = getScreenClass(screenName);
        goTo(screenClass, param);
    }

    public void goTo(final Class<? extends Screen> screenClass, final String params){

        if (screenClass == null){
            Iadt.buildMessage("Unable to find the screen class, add it to our ScreenLoader")
                    .isError().fire();
            return;
        }

        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "ScreenManager goTo(" + screenClass.getSimpleName() + ", " + params + ")");

        navigationManager.addStep(screenClass, params);

        loadedScreen = new ClassHelper<Screen>().createClass(screenClass,
                ScreenManager.class, this);

        updateBackButton();
        updateToolbarTitle();
        startScreen();

        if (getCurrentScreen() == null) {
            loadedScreen.toggleVisibility(true);
            setCurrentScreen(loadedScreen);
        }
        else {
            getCurrentScreen().toggleVisibility(false);
            destroyPreviousScreen();
            loadedScreen.toggleVisibility(true);
            setCurrentScreen(loadedScreen);
        }
    }

    private void destroyPreviousScreen() {
        if (getCurrentScreen() != null) {
            getCurrentScreen().stop();
            getCurrentScreen().destroy();
        }
    }

    private ViewGroup startScreen() {
        screenLayer.scrollTop();
        loadScreenToolbar(loadedScreen);
        loadedScreen.start();
        return loadedScreen.getView();
    }

    public void goBack(){

        if (!currentScreen.canGoBack()){
            FriendlyLog.log("D","Iadt", "Back",
                    "Navigation back prevented by " + currentScreen.getTitle() + " screen");
            return;
        }

        // Discard current and retrieve previous
        navigationManager.removeStep();
        NavigationStep previousStep = navigationManager.removeStep();

        if (previousStep != null){
            goTo(previousStep.getClassName(), previousStep.getParams());
        }
        else{
            hide();
        }
    }

    public void goHome(){
        navigationManager.clearSteps();
        goTo(getHomeClass().getSimpleName(), null);
    }

    public String getCurrentStepParams(){
        return navigationManager.getCurrentParams();
    }

    public void updateCurrentStepParams(String newParams){
        navigationManager.updateCurrentParams(newParams);
    }

    //endregion

    //region [ UPDATE VIEW ]

    private void loadScreenToolbar(Screen loadedScreen) {
        screenToolbar.getMenu().clear();
        if (loadedScreen.getToolbarLayoutId() != -1){
            screenToolbar.setVisibility(View.VISIBLE);
            screenToolbar.inflateMenu(loadedScreen.getToolbarLayoutId());
            loadedScreen.onCreateOptionsMenu(screenToolbar.getMenu());
            screenToolbar.setOnMenuItemClickListener(loadedScreen);
        }else {
            screenToolbar.setVisibility(View.GONE);
        }
    }

    public Toolbar getScreenToolbar(){
        return screenToolbar;
    }

    public void setTitle(String title){
        getScreenLayer().setToolbarTitle(title);
    }

    private void updateToolbarTitle() {
        getScreenLayer().setToolbarTitle(loadedScreen.getTitle());
    }

    public void updateBackButton() {
        getScreenLayer().toggleBackButton(!navigationManager.isHome());
    }

    public void hide() {
        //TODO: refactor, it doesn't seems like the best way
        IadtController.get().getOverlayHelper().showIcon();
    }

    //endregion

    public Context getContext() {
        return context;
    }
    public LayoutInflater getInflater() {
        return inflater;
    }
    public ScreenLayer getScreenLayer() {
        return screenLayer;
    }
    public ViewGroup getView() {
        return (ViewGroup) screenLayer.getView();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (getCurrentScreen() != null)
            currentScreen.onConfigurationChanged(newConfig);
        if (screenToolbar != null){
            screenToolbar.requestLayout();
        }
    }

    public void destroy() {
        if (getCurrentScreen() != null){
            currentScreen.destroy();
            setCurrentScreen(null);
        }
    }
}
