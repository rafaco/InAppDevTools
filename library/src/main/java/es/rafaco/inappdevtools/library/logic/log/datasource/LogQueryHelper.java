package es.rafaco.inappdevtools.library.logic.log.datasource;

import android.text.TextUtils;
import android.util.Log;

//#ifdef MODERN
//@import androidx.sqlite.db.SimpleSQLiteQuery;
//@import androidx.sqlite.db.SupportSQLiteQuery;
//#else
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;

public class LogQueryHelper {

    private final LogFilter config;
    private String queryString = new String();
    private List<Object> args = new ArrayList();
    private boolean containsCondition = false;

    public LogQueryHelper(LogFilter config) {
        this.config = config;
    }

    public SupportSQLiteQuery getSelectedQuery() {
        queryString = new String();
        args = new ArrayList();
        containsCondition = false;

        queryString += "SELECT * FROM friendly";

        if(!TextUtils.isEmpty(config.getText())){
            addConjunction();
            queryString += " ( message LIKE ? OR category LIKE ? OR subcategory LIKE ? OR extra LIKE ? )";
            String likeFilter = "%" + config.getText() + "%";
            multiplicateArg(likeFilter, 4);
        }

        if(config.getSeverities().size() < 5) {
            addConjunction();
            addInWithList("severity", config.getSeverities(), true);
        }

        if(!config.getCategories().isEmpty()){
            addConjunction();
            addInWithList("category", config.getCategories(), true);
        }

        if(!config.getNotCategories().isEmpty()){
            addConjunction();
            addInWithList("category", config.getNotCategories(), false);
        }

        if(!config.getSubcategories().isEmpty()){
            addConjunction();
            addInWithList("subcategory", config.getSubcategories(), true);
        }

        if(config.getFromDate()>0){
            addConjunction();
            queryString += " date >= ?";
            args.add(config.getFromDate());
        }

        if(config.getToDate()>0){
            addConjunction();
            //TODO: replace by <= when using real finish date instead of next session date
            queryString += " date < ?";
            args.add(config.getToDate());
        }

        queryString += " ORDER BY date ASC";

        Log.d(Iadt.TAG, "ROOM QUERY: " + queryString);
        return new SimpleSQLiteQuery(queryString, args.toArray());
    }

    private void addConjunction() {
        if (containsCondition)
            queryString += " AND";
        else
            queryString += " WHERE";
            containsCondition = true;
    }

    private void addInWithList(String key, List<String> list, boolean isIn){
        String operator = (isIn) ? "IN" : "NOT IN";
        int size = list.size();
        String paramList = "";
        for (int i=0; i<size; i++){
            paramList += ", ?";
            args.add(list.get(i));
        }
        queryString += String.format(" %s %s (%s)", key, operator, paramList.substring(2));
        //sample: " severity IN (?, ?, ?)"
    }

    private void multiplicateArg(Object arg, int size){
        for (int i=0; i<size; i++) {
            args.add(arg);
        }
    }

    public SimpleSQLiteQuery getCurrentFilterSize(){
        getSelectedQuery();

        queryString = queryString.replace("SELECT *",
                "SELECT 'Current filter' AS name,"
                        + " COUNT(*) AS count,"
                        + " (count(*) * 100.0 / (select count(*) from friendly)) AS percentage");

        //args.add(0, columnName);

        queryString = queryString.replace("ORDER BY date ASC", "");
                //"GROUP BY " + columnName + "ORDER BY COUNT(*) DESC");

        Log.d(Iadt.TAG, "FILTER QUERY: " + queryString);
        return new SimpleSQLiteQuery(queryString, args.toArray());
    }
}
