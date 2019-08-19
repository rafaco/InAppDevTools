package es.rafaco.inappdevtools.library.logic.log.datasource;

//#ifdef ANDROIDX
//@import androidx.paging.DataSource;
//#else
import android.arch.paging.DataSource;
//#endif

import es.rafaco.inappdevtools.library.logic.log.filter.LogFilter;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.FriendlyDao;

public class LogDataSourceFactory extends DataSource.Factory {

    private LogFilter config;
    private FriendlyDao dao;

    public LogDataSourceFactory(FriendlyDao dao, LogFilter config) {
        this.dao = dao;
        this.config = (config != null) ? config : new LogFilter();
    }

    @Override
    public DataSource<Integer, Friendly> create() {
        //if (subcategories==null){
        //return dao.filter(text, getSeverities()).create();

        LogQueryHelper helper = new LogQueryHelper(getConfig());
        return dao.filterWithQuery(helper.getSelectedQuery()).create();
    }

    public LogFilter getConfig(){
        return config;
    }

    public void setConfig(LogFilter config){
        this.config = config;
    }
}
