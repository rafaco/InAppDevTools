package es.rafaco.inappdevtools.library.logic.integrations;

public class ThinItem {

    private final Runnable onClick;
    String title;
    int icon;
    int color;

    public ThinItem(String title, int icon, int color, Runnable onclick) {
        this.title = title;
        this.icon = icon;
        this.color = color;
        this.onClick = onclick;
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

    public void onClick(){
        if (onClick != null) onClick.run();
    }
}
