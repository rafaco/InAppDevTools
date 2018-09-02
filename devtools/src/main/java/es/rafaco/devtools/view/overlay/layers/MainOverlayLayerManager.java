package es.rafaco.devtools.view.overlay.layers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.utils.ClassHelper;
import es.rafaco.devtools.utils.ExpandCollapseUtils;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.home.HomeScreen;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MainOverlayLayerManager {

    protected Context context;
    private final MainOverlayLayer mainLayer;
    private final LayoutInflater inflater;

    private List<Class<? extends OverlayScreen>> registeredScreens;
    private List<NavigationStep> navigationHistory;
    private OverlayScreen loadedScreen = null;
    private OverlayScreen currentScreen = null;

    public MainOverlayLayerManager(Context context, MainOverlayLayer mainLayer) {
        this.context = context;
        this.mainLayer = mainLayer;
        this.inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.registeredScreens = new ArrayList<>();
        this.navigationHistory = new ArrayList<>();
    }

    public Context getContext() {
        return context;
    }
    public LayoutInflater getInflater() {
        return inflater;
    }
    public MainOverlayLayer getMainLayer() {
        return mainLayer;
    }
    public ViewGroup getView() {
        return (ViewGroup) mainLayer.getView();
    }
    public OverlayScreen getCurrentScreen(){
        return currentScreen;
    }

    public void destroy() {
        if (currentScreen != null){
            currentScreen.destroy();
            currentScreen = null;
        }
    }

    public void goTo(Class<? extends OverlayScreen> screenClass) {
        goTo(screenClass.getSimpleName(), null);
    }


    //region [ NAVIGATION ]

    public void goTo(String screenName, String param){
        Class<? extends OverlayScreen> screenClass = getScreenClass(screenName);
        goTo(screenClass, param);
    }

    public void goTo(final Class<? extends OverlayScreen> screenClass, final String param){

        //TODO: use params
        Log.d(DevTools.TAG, "Requested overlay screen: " + screenClass.getSimpleName() + ": " + param);

        //Ignore if already selected
        /*if (getCurrentScreen() != null && screenName.equals(getCurrentScreen().getName())) {
            return;
        }*/

        if (screenClass == null){
            return;
        }


        final ViewGroup loadedViewGroup = loadScreen(screenClass, param);
        if (getCurrentScreen() == null) {
            loadedScreen.show();
            ExpandCollapseUtils.expand(loadedScreen.bodyView, null);
            currentScreen = loadedScreen;
        }else{
            ExpandCollapseUtils.collapse(currentScreen.bodyView,
                    new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            destroyPreviousScreen();
                            ExpandCollapseUtils.expand(loadedScreen.bodyView, null);
                            currentScreen = loadedScreen;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
        }

/*
        ExpandCollapseUtils.start(getView(),
                new Runnable() {
                    @Override
                    public void run() {
                        destroyPreviousScreen();
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        loadScreen(screenClass, param);
                    }
                });*/
    }

    private void destroyPreviousScreen() {
        //Destroy previous screen
        if (getCurrentScreen() != null) {
            getCurrentScreen().stop();
            getCurrentScreen().destroy();
        }
    }

    private ViewGroup loadScreen(final Class<? extends OverlayScreen> screenClass, final String param) {
        loadedScreen = new ClassHelper<OverlayScreen>().createClass(screenClass,
                MainOverlayLayerManager.class, this);

        if (loadedScreen != null){
            loadedScreen.start(param);
            NavigationStep newStep = new NavigationStep(screenClass, param);
            addNavigationStep(newStep);
        }
        return loadedScreen.getView();
    }

    private void addNavigationStep(NavigationStep newStep) {
        navigationHistory.add(newStep);

        //Update toolbar
        updateBackbutton();
        updateToolbarTitle();
    }

    private void updateToolbarTitle() {
        getMainLayer().setToolbarTitle(loadedScreen.getTitle());
    }

    private void updateBackbutton() {
        getMainLayer().toogleBackButton(navigationHistory.size()>1);
    }

    public void goBack(){
        if (navigationHistory.size()>1){
            //Remove current step
            navigationHistory.remove(navigationHistory.size()-1);
            //Restore the previous one
            NavigationStep previousStep = navigationHistory.remove(navigationHistory.size()-1);
            goTo(previousStep.getClassName(), previousStep.getParam());
        }
    }

    public void goHome(){
        navigationHistory.clear();
        goTo(HomeScreen.class.getSimpleName(), null);
    }

    //endregion



    //TODO: remove ALL related registered screens????
    //
    //
    public List<String> getRegisteredScreens(){
        List<String> mainScreens = new ArrayList<>();
        for (Class<? extends OverlayScreen> screen : registeredScreens) {
            mainScreens.add(screen.getSimpleName());
        }
        return mainScreens;
    }

    public void registerScreen(Class<? extends OverlayScreen> screenClass){

        //TODO: if ((isClass(className)){
        registeredScreens.add(screenClass);
    }

    public Class<? extends OverlayScreen> getScreenClass(String name){
        for (Class<? extends OverlayScreen> screen : registeredScreens){
            if (screen.getSimpleName().equals(name)){
                return screen;
            }
        }
        return null;
    }
}
