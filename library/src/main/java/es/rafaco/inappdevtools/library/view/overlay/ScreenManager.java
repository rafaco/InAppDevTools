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
import es.rafaco.inappdevtools.library.logic.navigation.NavigationManager;
import es.rafaco.inappdevtools.library.logic.utils.ClassHelper;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.view.overlay.layers.ScreenLayer;
import es.rafaco.inappdevtools.library.logic.navigation.NavigationStep;
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
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.HomeScreen;

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

        registerAllScreens();

        ThreadUtils.printOverview("ScreenManager");
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

    //endregion

    //region [ NAVIGATION ]

    public void goTo(String screenName, String param){
        Class<? extends Screen> screenClass = getScreenClass(screenName);
        goTo(screenClass, param);
    }

    public void goTo(final Class<? extends Screen> screenClass, final String params){

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
        goTo(HomeScreen.class.getSimpleName(), null);
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
