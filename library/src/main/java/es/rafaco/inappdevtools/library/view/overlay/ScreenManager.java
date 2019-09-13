package es.rafaco.inappdevtools.library.view.overlay;

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

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.utils.ClassHelper;
import es.rafaco.inappdevtools.library.view.overlay.layers.ScreenLayer;
import es.rafaco.inappdevtools.library.view.overlay.navigation.NavigationStep;
import es.rafaco.inappdevtools.library.view.overlay.screens.console.ConsoleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.AnrDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.ConfigScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.InspectViewScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.MoreScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.RunScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.AnalysisScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetworkScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.detail.NetworkDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreenshotsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourcesScreen;
import es.rafaco.inappdevtools.library.view.utils.ExpandCollapseUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.HomeScreen;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ScreenManager {

    protected Context context;
    private final ScreenLayer screenLayer;
    private static Screen currentScreen = null;
    private final LayoutInflater inflater;
    private List<Class<? extends Screen>> registeredScreens;
    private List<NavigationStep> navigationHistory;
    private Screen loadedScreen = null;
    private Toolbar screenToolbar;

    public ScreenManager(Context context, ScreenLayer screenLayer) {
        this.context = context;
        this.screenLayer = screenLayer;
        this.inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.registeredScreens = new ArrayList<>();
        this.navigationHistory = new ArrayList<>();
        this.screenToolbar = getView().findViewById(R.id.tool_toolbar);

        registerAllScreens();
    }

    //region [ SCREENS MANAGER ]

    private void registerAllScreens() {
        //Load screenshots definitions
        //TODO: think a better way to avoid this
        registerScreen(HomeScreen.class);
        registerScreen(InfoScreen.class);
        registerScreen(NetworkScreen.class);
        registerScreen(ErrorsScreen.class);
        registerScreen(LogScreen.class);
        registerScreen(LogcatScreen.class);
        registerScreen(ConsoleScreen.class);
        registerScreen(ScreenshotsScreen.class);
        registerScreen(ReportScreen.class);
        registerScreen(CrashDetailScreen.class);
        registerScreen(AnrDetailScreen.class);
        registerScreen(NetworkDetailScreen.class);
        registerScreen(RunScreen.class);
        registerScreen(MoreScreen.class);
        registerScreen(InspectViewScreen.class);
        registerScreen(SourcesScreen.class);
        registerScreen(SourceDetailScreen.class);
        registerScreen(AnalysisScreen.class);
        registerScreen(ConfigScreen.class);
    }

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

    private static void setCurrentScreen(Screen screen) {
        currentScreen = screen;
    }

    public static Screen getCurrentScreen(){
        return currentScreen;
    }

    public static String getCurrentScreenString(){
        if (getCurrentScreen() == null)
            return null;
        return currentScreen.getClass().getSimpleName();
    }

    //endregion

    //region [ NAVIGATION ]

    public void goTo(String screenName, String param){
        Class<? extends Screen> screenClass = getScreenClass(screenName);
        goTo(screenClass, param);
    }

    public void goTo(final Class<? extends Screen> screenClass, final String params){

        NavigationStep newStep = new NavigationStep(screenClass, params);
        addNavigationStep(newStep);

        IadtController.get().getEventManager().fire(Event.OVERLAY_NAVIGATION, newStep);

        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "Requested overlay screen: " + screenClass.getSimpleName() + ": " + params);
        
        loadedScreen = new ClassHelper<Screen>().createClass(screenClass,
                ScreenManager.class, this);

        updateBackButton();
        updateToolbarTitle();
        startScreen();

        if (getCurrentScreen() == null) {
            loadedScreen.toggleHeadVisibility(true);
            ExpandCollapseUtils.expand(loadedScreen.bodyView, null);
            setCurrentScreen(loadedScreen);
        }
        else {

            //No animation
            getCurrentScreen().toggleVisibility(false);
            destroyPreviousScreen();
            loadedScreen.toggleVisibility(true);
            setCurrentScreen(loadedScreen);

            /*
            // Animations playground
            ExpandCollapseUtils.collapse(screenLayer.getFullContainer(),
                    new AnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            currentScreen.toggleVisibility(false);
                            destroyPreviousScreen();
                            loadedScreen.toggleVisibility(true);
                            ExpandCollapseUtils.expand(screenLayer.getFullContainer(), null);
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
                    ThreadUtils.runOnMain(new Runnable() {
                        @Override
                        public void run() {
                            loadedScreen.toggleHeadVisibility(true);
                        }
                    });
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ThreadUtils.runOnMain(new Runnable() {
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

    /*
    private abstract class AnimationEndListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }*/

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

    private void addNavigationStep(NavigationStep newStep) {
        //Ensure home screen is always at the bottom. It get complete navigability using back button
        if (navigationHistory.isEmpty() &&
                !HomeScreen.class.equals(newStep.getClassName())){
            NavigationStep homeStep = new NavigationStep(HomeScreen.class, null);
            navigationHistory.add(homeStep);
        }

        navigationHistory.add(newStep);
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

    public String getCurrentStepParams(){
        return navigationHistory.get(navigationHistory.size() - 1).getParam();
    }

    public void updateCurrentStepParams(String newParams){
        NavigationStep currentStep = navigationHistory.remove(navigationHistory.size() - 1);
        navigationHistory.add(new NavigationStep(currentStep.getClassName(), newParams));
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
        getScreenLayer().toggleBackButton(navigationHistory.size()>1);
    }

    public void hide() {
        //TODO: refactor, it doesn't seems like the best way
        IadtController.get().showIcon();
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
