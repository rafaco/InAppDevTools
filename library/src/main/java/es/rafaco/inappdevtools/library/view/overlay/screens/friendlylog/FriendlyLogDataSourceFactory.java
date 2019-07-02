package es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog;

import android.util.Log;

//#ifdef MODERN
//@import androidx.paging.DataSource;
//#else
import android.arch.paging.DataSource;
//#endif

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.FriendlyDao;

public class FriendlyLogDataSourceFactory extends DataSource.Factory {

    private FriendlyDao dao;
    private String text = "";
    private String level = "I";
    private List<String> categories = new ArrayList<>();
    private List<String> notCategories = new ArrayList<>();
    private List<String> subcategories = new ArrayList<>();
    private Date fromDate;
    private Date toDate;

    public FriendlyLogDataSourceFactory(FriendlyDao dao) {
        this.dao = dao;
    }

    @Override
    public DataSource<Integer, Friendly> create() {
        //if (subcategories==null){
        //return dao.filter(text, getSeverities()).create();

        FriendlyLogQueryHelper helper = new FriendlyLogQueryHelper(this);
        return dao.filterWithQuery(helper.getSelectedQuery()).create();
    }

    public void setText(String text){
        this.text = "%" + text + "%";
    }

    public void setLevelString(String level) {
        this.level = level;
        Log.v(Iadt.TAG, "Verbosity level changed to: " + level);
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
        Log.v(Iadt.TAG, "Log categories changed to: " + notCategories);
    }

    public void setNotCategories(List<String> notCategories) {
        this.notCategories = notCategories;
        Log.v(Iadt.TAG, "Log notCategories changed to: " + notCategories);
    }

    public void setSubcategories(List<String> subcategories) {
        this.subcategories = subcategories;
        Log.v(Iadt.TAG, "Log subcategories changed to: " + subcategories);
    }

    public String getText() {
        return text;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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

    protected List<String> getSeverities() {

        if (level.equals("V")){
            return Arrays.asList("V", "D", "I", "W", "E");
        }
        else if (level.equals("D")){
            return Arrays.asList("D", "I", "W", "E");
        }
        else if (level.equals("I")){
            return Arrays.asList("I", "W", "E");
        }
        else if (level.equals("W")){
            return Arrays.asList("W", "E");
        }
        else if (level.equals("E")){
            return Arrays.asList("E");
        }
        return null;
    }
}
