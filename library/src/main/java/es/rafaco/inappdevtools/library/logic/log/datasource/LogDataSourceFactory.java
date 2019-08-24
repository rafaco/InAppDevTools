package es.rafaco.inappdevtools.library.logic.log.datasource;

//#ifdef ANDROIDX
//@import androidx.paging.DataSource;
//#else
import android.arch.paging.DataSource;
//#endif

import es.rafaco.inappdevtools.library.logic.log.filter.LogBackFilter;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.FriendlyDao;

public class LogDataSourceFactory extends DataSource.Factory {

    private LogBackFilter config;
    private FriendlyDao dao;

    public LogDataSourceFactory(FriendlyDao dao, LogBackFilter config) {
        this.dao = dao;
        this.config = (config != null) ? config : new LogBackFilter();
    }

    @Override
    public DataSource<Integer, Friendly> create() {
        //if (subcategories==null){
        //return dao.filter(text, getSeverities()).create();

        LogQueryHelper helper = new LogQueryHelper(getConfig());
        return dao.filterWithQuery(helper.getSelectedQuery()).create();
    }

    public LogBackFilter getConfig(){
        return config;
    }

    public void setConfig(LogBackFilter config){
        this.config = config;
    }
}
