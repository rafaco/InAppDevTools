package es.rafaco.inappdevtools.library.logic.log.filter;

import java.util.ArrayList;
import java.util.List;

public class LogBackFilter {

    String text = "";
    List<String> severities;
    List<String> categories;
    List<String> notCategories;
    List<String> subcategories;
    long fromDate;
    long toDate;

    public LogBackFilter() {
        this.severities = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.notCategories = new ArrayList<>();
        this.subcategories = new ArrayList<>();
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setSeverities(List<String> severities) {
        this.severities = severities;
    }

    public List<String> getSeverities(){
        return severities;
    }


    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public void setNotCategories(List<String> notCategories) {
        this.notCategories = notCategories;
    }

    public void setSubcategories(List<String> subcategories) {
        this.subcategories = subcategories;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<String> getNotCategories() {
        return notCategories;
    }

    public List<String> getSubcategories() {
        return subcategories;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(long toDate) {
        this.toDate = toDate;
    }

    public long getFromDate() {
        return fromDate;
    }

    public long getToDate() {
        return toDate;
    }
}
