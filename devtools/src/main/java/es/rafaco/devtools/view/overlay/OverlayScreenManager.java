package es.rafaco.devtools.view.overlay;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.view.OverlayUIService;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayer;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.home.HomeScreen;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class OverlayScreenManager {

    protected Context context;
    private final MainOverlayLayer mainLayer;
    private final LayoutInflater inflater;

    private List<Class<? extends OverlayScreen>> screens;
    private OverlayScreen currentScreen = null;

    public OverlayScreenManager(Context context, MainOverlayLayer mainLayer) {
        this.context = context;
        this.mainLayer = mainLayer;
        this.inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.screens = new ArrayList<>();
    }

    public void registerScreen(Class<? extends OverlayScreen> screenClass){

        //TODO: if ((isClass(screenClass)){

        screens.add(screenClass);
    }

    public OverlayScreen getCurrentScreen(){
        return currentScreen;
    }

    public void loadScreen(String screenName, Object params){
        Log.d(DevTools.TAG, "Requested new overlay screen: " + screenName);

        //Ignore if already selected
        if (getCurrentScreen() != null && screenName.equals(getCurrentScreen().getName())) {
            return;
        }

        //Destroy previous screen
        if (getCurrentScreen() != null) {
            getCurrentScreen().stop();
            getCurrentScreen().destroy();
        }

        //Load next screen
        Class<? extends OverlayScreen> newScreen = getScreenClass(screenName);
        if (newScreen != null){
            currentScreen = createClass(newScreen, this);
            currentScreen.start(); //TODO: use params
        }
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
        return mainLayer.getScreenWrapper();
    }


    public Class<? extends OverlayScreen> getScreenClass(String name){
        for (Class<? extends OverlayScreen> screen : screens){
            if (screen.getSimpleName().equals(name)){
                return screen;
            }
        }
        return null;
    }

    public Class<? extends OverlayScreen> getScreenClass(Class<?> screenClass){
        for (Class<? extends OverlayScreen> screen : screens) {
            if (screenClass.isInstance(screen)){
                return screen;
            }
        }
        return null;
    }

    public void destroy() {
        if (currentScreen != null){
            currentScreen.destroy();
            currentScreen = null;
        }
    }


    //TODO: review/eliminate
    public void updateHomeInfoContent(Class<?> toolClass, String content){
        //((HomeScreen) getScreenClass(HomeScreen.class)).updateContent(toolClass, content);
    }

    //TODO: remove this??
    private void stopService() {
        ((OverlayUIService)context).stopSelf();
    }






    public boolean isClass(String className) {
        try
        {
            Class.forName(className);
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public OverlayScreen createClass(Class<? extends OverlayScreen> screenClass, Object ... initargs) {
        try {
            OverlayScreen screenObject = screenClass.getConstructor(OverlayScreenManager.class).newInstance(initargs);
            return screenObject;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }



    public List<String> getMainScreens(){
        List<String> mainScreens = new ArrayList<>();
        for (Class<? extends OverlayScreen> screen : screens) {
            mainScreens.add(screen.getSimpleName());
        }
        return mainScreens;
    }
}
