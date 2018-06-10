package es.rafaco.devtools.tools.home;

import es.rafaco.devtools.tools.Tool;

public class HomeInfo {
    String title;
    String message;
    String toolName;
    Class<? extends Tool> toolClass;
    int color;

    public HomeInfo(Class<? extends Tool> toolClass, String title, String message, int color) {
        //this.toolName = toolName;
        this.toolClass = toolClass;
        this.title = title;
        this.message = message;
        this.color = color;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public Class<? extends Tool> getToolClass() {
        return toolClass;
    }

    public void setToolClass(Class<? extends Tool> toolClass) {
        this.toolClass = toolClass;
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

}
