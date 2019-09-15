package es.rafaco.inappdevtools.library.view.components.deco;

import es.rafaco.inappdevtools.library.logic.navigation.NavigationStep;

public class DecoratedToolInfo {
    String title;
    String message;
    int color;
    int icon;
    Long order;
    private final NavigationStep navigationStep;
    private final Runnable runnable;


    public DecoratedToolInfo(String title, String message, int color, long order, NavigationStep navigationStep) {
        this.title = title;
        this.message = message;
        this.color = color;
        this.order = order;
        this.navigationStep = navigationStep;
        this.runnable = null;
        this.icon = -1;
    }

    public DecoratedToolInfo(String title, String message, int color, long order, Runnable runnable) {
        this.title = title;
        this.message = message;
        this.color = color;
        this.order = order;
        this.navigationStep = null;
        this.runnable = runnable;
        this.icon = -1;
    }

    public DecoratedToolInfo(String title, String message, int color, int icon, long order, NavigationStep navigationStep) {
        this(title, message, color, order, navigationStep);
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public NavigationStep getNavigationStep() {
        return navigationStep;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
