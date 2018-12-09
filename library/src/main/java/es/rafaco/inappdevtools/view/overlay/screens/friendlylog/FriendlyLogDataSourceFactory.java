package es.rafaco.inappdevtools.view.overlay.screens.friendlylog;

import android.util.Log;

import androidx.paging.DataSource;

import java.util.Arrays;
import java.util.List;

import es.rafaco.inappdevtools.DevTools;
import es.rafaco.inappdevtools.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.storage.db.entities.FriendlyDao;

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
        Log.v(DevTools.TAG, "Verbosity level changed to: " + level);
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