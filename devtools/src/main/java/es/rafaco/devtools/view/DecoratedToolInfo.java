package es.rafaco.devtools.view;

import android.support.v4.content.ContextCompat;

import es.rafaco.devtools.view.overlay.screens.OverlayScreen;

public class DecoratedToolInfo {
    String title;
    String message;
    int color;
    Long order;
    String toolName;
    Class<? extends OverlayScreen> screenClass;

    public DecoratedToolInfo(Class<? extends OverlayScreen> toolClass, String title, String message, long order, int color) {
        //this.toolName = toolName;
        this.screenClass = toolClass;
        this.title = title;
        this.message = message;
        this.order = order;
        this.color = color;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
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
