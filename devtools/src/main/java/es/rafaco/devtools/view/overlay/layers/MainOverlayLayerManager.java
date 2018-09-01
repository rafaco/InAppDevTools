package es.rafaco.devtools.view.overlay.layers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.utils.ClassHelper;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.home.HomeScreen;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MainOverlayLayerManager {

    protected Context context;
    private final MainOverlayLayer mainLayer;
    private final LayoutInflater inflater;

    private List<Class<? extends OverlayScreen>> registredScreens;
    private List<NavigationStep> navigationHistory;
    private OverlayScreen currentScreen = null;

    public MainOverlayLayerManager(Context context, MainOverlayLayer mainLayer) {
        this.context = context;
        this.mainLayer = mainLayer;
        this.inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.registredScreens = new ArrayList<>();
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

    public class NavigationStep{
        public final String screenName;
        public final Class<? extends OverlayScreen> screenClass;
        public final Object params;

        public NavigationStep(String screenName, Class<? extends OverlayScreen> screenClass, Object params) {
            this.screenName = screenName;
            this.screenClass = screenClass;
            this.params = params;
        }
    }

    public void goTo(String screenName, Object params){
        Log.d(DevTools.TAG, "Requested new overlay screen: " + screenName);

        //Ignore if already selected
        /*if (getCurrentScreen() != null && screenName.equals(getCurrentScreen().getName())) {
            return;
        }*/

        //Destroy previous screen
        if (getCurrentScreen() != null) {
            getCurrentScreen().stop();
            getCurrentScreen().destroy();
        }

        //Load next screen
        Class<? extends OverlayScreen> screenClass = getScreenClass(screenName);
        if (screenClass != null){
            currentScreen = new ClassHelper<OverlayScreen>().createClass(screenClass,
                    MainOverlayLayerManager.class, this);
            currentScreen.start(); //TODO: use params
        }

        //Store loaded in history
        NavigationStep newStep = new NavigationStep(screenName, screenClass, params);
        navigationHistory.add(newStep);

        updateBackbutton();
        updateToolbarTitle();
    }

    private void updateToolbarTitle() {
        getMainLayer().setToolbarTitle(currentScreen.getTitle());
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
            goTo(previousStep.screenName, previousStep.params);
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
        for (Class<? extends OverlayScreen> screen : registredScreens) {
            mainScreens.add(screen.getSimpleName());
        }
        return mainScreens;
    }

    public void registerScreen(Class<? extends OverlayScreen> screenClass){

        //TODO: if ((isClass(screenClass)){
        registredScreens.add(screenClass);
    }

    public Class<? extends OverlayScreen> getScreenClass(String name){
        for (Class<? extends OverlayScreen> screen : registredScreens){
            if (screen.getSimpleName().equals(name)){
                return screen;
            }
        }
        return null;
    }
}
