package es.rafaco.inappdevtools.library.view.overlay.layers;

import android.content.Context;
import android.content.res.Configuration;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.ClassHelper;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.utils.ExpandCollapseUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.HomeScreen;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MainOverlayLayerManager {

    protected Context context;
    private final MainOverlayLayer mainLayer;
    private final LayoutInflater inflater;

    private List<Class<? extends OverlayScreen>> registeredScreens;
    private List<NavigationStep> navigationHistory;
    private OverlayScreen loadedScreen = null;
    private OverlayScreen currentScreen = null;
    private Toolbar screenToolbar;

    public MainOverlayLayerManager(Context context, MainOverlayLayer mainLayer) {
        this.context = context;
        this.mainLayer = mainLayer;
        this.inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.registeredScreens = new ArrayList<>();
        this.navigationHistory = new ArrayList<>();
        screenToolbar = getView().findViewById(R.id.tool_toolbar);
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

        loadScreen(screenClass, param);

        if (getCurrentScreen() == null)
        {
            loadedScreen.toggleHeadVisibility(true);
            ExpandCollapseUtils.expand(loadedScreen.bodyView, null);
            currentScreen = loadedScreen;
        }
        else {

            //No animation
            currentScreen.toggleVisibility(false);
            destroyPreviousScreen();
            loadedScreen.toggleVisibility(true);
            currentScreen = loadedScreen;

            /*
            ExpandCollapseUtils.collapse(mainLayer.getFullContainer(),
                    new AnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            currentScreen.toggleVisibility(false);
                            destroyPreviousScreen();
                            loadedScreen.toggleVisibility(true);
                            ExpandCollapseUtils.expand(mainLayer.getFullContainer(), null);
                            currentScreen = loadedScreen;
                        }
                    });
*/
            /*
            if (currentScreen.haveHead()){
                ExpandCollapseUtils.collapse(currentScreen.headView,
                        new AnimationEndListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                if (loadedScreen.haveHead())
                                    ExpandCollapseUtils.expand(loadedScreen.headView, null);
                            }
                        });
            }else{
                if (loadedScreen.haveHead())
                    ExpandCollapseUtils.expand(loadedScreen.headView, null);
            }

            ExpandCollapseUtils.collapse(currentScreen.bodyView,
                    new AnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            destroyPreviousScreen();
                            ExpandCollapseUtils.expand(loadedScreen.bodyView,
                                    new AnimationEndListener(){
                                        @Override
                                        public void onAnimationEnd(Animation animation) {

                                        }
                            });
                            currentScreen = loadedScreen;
                        }
                    });
*/
            /* TODO: research which animation is better
            // This one depend on animateLayoutChanges flags but seems to perform poorly

            currentScreen.toggleHeadVisibility(false);
            currentScreen.toggleBodyVisibility(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadedScreen.toggleHeadVisibility(true);
                        }
                    });
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadedScreen.toggleBodyVisibility(true);
                                }
                            });
                        }
                    }, 1 * 500);
                }
            }, 1 * 500);
            destroyPreviousScreen();
            currentScreen = loadedScreen;*/
        }
    }

    private void destroyPreviousScreen() {
        if (getCurrentScreen() != null) {
            getCurrentScreen().stop();
            getCurrentScreen().destroy();
        }
    }

    private ViewGroup loadScreen(final Class<? extends OverlayScreen> screenClass, final String param) {
        loadedScreen = new ClassHelper<OverlayScreen>().createClass(screenClass,
                MainOverlayLayerManager.class, this);

        if (loadedScreen != null){
            mainLayer.scrollTop();
            loadScreenToolbar(loadedScreen);
            loadedScreen.start(param);
            NavigationStep newStep = new NavigationStep(screenClass, param);
            addNavigationStep(newStep);

            return loadedScreen.getView();
        }
        return null;
    }

    private void addNavigationStep(NavigationStep newStep) {
        //Ensure home screen is always at the bottom. It get complete navigability using back button
        if (navigationHistory.isEmpty() &&
                !HomeScreen.class.equals(newStep.getClassName())){
            NavigationStep homeStep = new NavigationStep(HomeScreen.class, null);
            navigationHistory.add(homeStep);
        }

        navigationHistory.add(newStep);
        updateBackbutton();
        updateToolbarTitle();
    }

    public void setTitle(String title){
        getMainLayer().setToolbarTitle(title);
    }

    private void updateToolbarTitle() {
        getMainLayer().setToolbarTitle(loadedScreen.getTitle());
    }

    private void updateBackbutton() {
        getMainLayer().toogleBackButton(navigationHistory.size()>1);
    }

    public void goBack(){
        if (navigationHistory.size()>1){
            //Discard the current step and restore the previous one
            navigationHistory.remove(navigationHistory.size()-1);
            NavigationStep previousStep = navigationHistory.remove(navigationHistory.size()-1);
            goTo(previousStep.getClassName(), previousStep.getParam());
        }
    }

    public void goHome(){
        navigationHistory.clear();
        goTo(HomeScreen.class.getSimpleName(), null);
    }

    //endregion


    private void loadScreenToolbar(OverlayScreen loadedScreen) {
        screenToolbar.getMenu().clear();
        if (loadedScreen.getToolbarLayoutId() != -1){
            screenToolbar.setVisibility(View.VISIBLE);
            screenToolbar.inflateMenu(loadedScreen.getToolbarLayoutId());
            screenToolbar.setOnMenuItemClickListener(loadedScreen);
        }else {
            screenToolbar.setVisibility(View.GONE);
        }
    }

    public Toolbar getScreenToolbar(){
        return screenToolbar;
    }


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

    public void onConfigurationChanged(Configuration newConfig) {
        if (currentScreen != null)
            currentScreen.onConfigurationChanged(newConfig);
        if (screenToolbar != null){
            screenToolbar.requestLayout();
        }
    }

    public void hide() {
        //TODO: refactor, it doesn't seems like the best way
        OverlayUIService.runAction(OverlayUIService.IntentAction.HIDE,null);
    }

    private abstract class AnimationEndListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
}
