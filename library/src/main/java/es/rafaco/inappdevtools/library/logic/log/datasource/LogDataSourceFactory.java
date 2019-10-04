package es.rafaco.inappdevtools.library.logic.log.datasource;

import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.paging.DataSource;
//#else
import android.arch.paging.DataSource;
//#endif

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.log.filter.LogBackFilter;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.FriendlyDao;

public class LogDataSourceFactory extends DataSource.Factory {

    private LogBackFilter filter;
    private FriendlyDao dao;

    public LogDataSourceFactory(FriendlyDao dao, LogBackFilter filter) {
        this.dao = dao;
        this.filter = (filter != null) ? filter : new LogBackFilter();
    }

    @Override
    public DataSource<Integer, Friendly> create() {
        Log.v(Iadt.TAG, "LogDataSource created");
        LogQueryHelper helper = new LogQueryHelper(getFilter());
        return dao.filterWithQuery(helper.getSelectedQuery()).create();
    }

    public LogBackFilter getFilter(){
        return filter;
    }

    public void setFilter(LogBackFilter filter){
        this.filter = filter;
    }
}
