package es.rafaco.devtools.tools.home;

public class HomeInfo {
    String title;
    String message;
    String iconAction;
    int color;

    public HomeInfo(String title, String message, String iconAction, int color) {
        this.title = title;
        this.message = message;
        this.iconAction = iconAction;
        this.color = color;
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

    public String getIconAction() {
        return iconAction;
    }

    public void setIconAction(String iconAction) {
        this.iconAction = iconAction;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}
