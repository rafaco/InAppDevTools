package es.rafaco.inappdevtools.library.logic.log.filter;

public class LogUiFilter {

    private String text;
    private int sessionInt;
    private int severityInt;
    private int typeInt;
    private int categoryInt;
    private String categoryName;
    private int tagInt;
    private String tagName;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getSessionInt() {
        return sessionInt;
    }

    public void setSessionInt(int position) {
        this.sessionInt = position;
    }

    public int getSeverityInt() {
        return severityInt;
    }

    public void setSeverityInt(int selectedSeverity)   {
        this.severityInt = selectedSeverity;
    }

    public int getTypeInt() {
        return typeInt;
    }

    public void setTypeInt(int typeInt) {
        this.typeInt = typeInt;
    }

    public int getCategoryInt() {
        return categoryInt;
    }

    public void setCategoryInt(int categoryInt) {
        this.categoryInt = categoryInt;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getTagInt() {
        return tagInt;
    }

    public void setTagInt(int tagInt) {
        this.tagInt = tagInt;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
