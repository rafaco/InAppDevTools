package es.rafaco.devtools.view;

import es.rafaco.devtools.view.overlay.screens.OverlayScreen;

public class DecoratedToolInfo {
    String title;
    String message;
    int color;
    Long order;
    Class<? extends OverlayScreen> screenClass;

    public DecoratedToolInfo(Class<? extends OverlayScreen> screenClass, String title, String message, long order, int color) {
        this.screenClass = screenClass;
        this.title = title;
        this.message = message;
        this.order = order;
        this.color = color;
    }

    public Class<? extends OverlayScreen> getScreenClass() {
        return screenClass;
    }

    public void setScreenClass(Class<? extends OverlayScreen> screenClass) {
        this.screenClass = screenClass;
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
}
