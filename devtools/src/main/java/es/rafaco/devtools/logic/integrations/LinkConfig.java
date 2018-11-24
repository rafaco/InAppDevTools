package es.rafaco.devtools.logic.integrations;

import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.commands.CommandsScreen;

public class LinkConfig {

    String title;
    int icon;
    int color;
    Class<? extends OverlayScreen> screenClass;
    String param;

    public LinkConfig(String title, int icon, int color, Class<? extends OverlayScreen> screenClass, String param) {
        this.title = title;
        this.icon = icon;
        this.color = color;
        this.screenClass = screenClass;
        this.param = param;
    }

    public Class<? extends OverlayScreen> getScreenClass() {
        return CommandsScreen.class;
    }

    public void setScreenClass(Class<? extends OverlayScreen> screenClass) {
        this.screenClass = screenClass;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void run(){
        OverlayUIService.performNavigation(screenClass, param);
    }
}