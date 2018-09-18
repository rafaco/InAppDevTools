package es.rafaco.devtools.view.utils;

import es.rafaco.devtools.view.overlay.layers.NavigationStep;

public class DecoratedToolInfo {
    String title;
    String message;
    int color;
    Long order;
    private final NavigationStep navigationStep;

    public DecoratedToolInfo(String title, String message, int color, long order, NavigationStep navigationStep) {
        this.title = title;
        this.message = message;
        this.color = color;
        this.order = order;
        this.navigationStep = navigationStep;
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
}
