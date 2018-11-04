package es.rafaco.devtools.view.overlay.screens.friendlylog;

import android.arch.paging.DataSource;

import java.util.Arrays;
import java.util.List;

import es.rafaco.devtools.storage.db.entities.Friendly;
import es.rafaco.devtools.storage.db.entities.FriendlyDao;

public class FriendlyLogDataSourceFactory extends DataSource.Factory {

    private FriendlyDao dao;
    private String text = "";
    private String level = "I";

    public FriendlyLogDataSourceFactory(FriendlyDao dao) {
        this.dao = dao;
    }

    @Override
    public DataSource<Integer, Friendly> create() {
        return dao.filter(getAcceptedLevels(), text).create();
    }

    public void setText(String text){
        this.text = "%" + text + "%";
    }

    public void setLevelString(String level) {
        this.level = level;
    }

    private List<String> getAcceptedLevels() {

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