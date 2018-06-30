package es.rafaco.devtools.view.overlay.tools;

public class DecoratedToolInfo {
    String title;
    String message;
    int color;
    Long order;
    String toolName;
    Class<? extends OverlayTool> toolClass;

    public DecoratedToolInfo(Class<? extends OverlayTool> toolClass, String title, String message, long order, int color) {
        //this.toolName = toolName;
        this.toolClass = toolClass;
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

    public Class<? extends OverlayTool> getToolClass() {
        return toolClass;
    }

    public void setToolClass(Class<? extends OverlayTool> toolClass) {
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

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }
}
