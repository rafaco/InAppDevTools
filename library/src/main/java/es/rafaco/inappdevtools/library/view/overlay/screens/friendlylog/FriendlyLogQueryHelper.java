package es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog;

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

public class FriendlyLogQueryHelper {

    private final FriendlyLogDataSourceFactory factory;
    private String queryString = new String();
    private List<Object> args = new ArrayList();
    private boolean containsCondition = false;

    public FriendlyLogQueryHelper(FriendlyLogDataSourceFactory factory) {
        this.factory = factory;
    }

    public SupportSQLiteQuery getSelectedQuery() {
        queryString = new String();
        args = new ArrayList();
        containsCondition = false;

        queryString += "SELECT * FROM friendly WHERE";

        if(!TextUtils.isEmpty(factory.getText())){
            addAndIfNeeded();
            queryString += " ( message LIKE ? OR category LIKE ? OR subcategory LIKE ? OR extra LIKE ? )";
            multiplicateArg(factory.getText(), 4);
        }

        if(!factory.getLevel().equals("V")) {
            addAndIfNeeded();
            addInWithList("severity", factory.getSeverities(), true);
        }

        if(!factory.getCategories().isEmpty()){
            addAndIfNeeded();
            addInWithList("category", factory.getCategories(), true);
        }

        if(!factory.getNotCategories().isEmpty()){
            addAndIfNeeded();
            addInWithList("category", factory.getNotCategories(), false);
        }

        if(!factory.getSubcategories().isEmpty()){
            addAndIfNeeded();
            addInWithList("subcategory", factory.getSubcategories(), true);
        }

        /*if(fromDate!=null){
            queryString += " date AFTER ?";
            args.add(fromDate.getTime());
            containsCondition = true;
        }

        if(toDate!=null){
            queryString += " date BEFORE ?";
            args.add(toDate.getTime());
            containsCondition = true;
        }*/

        queryString += " ORDER BY date ASC";

        Log.d(Iadt.TAG, "ROOM QUERY: " + queryString);
        return new SimpleSQLiteQuery(queryString, args.toArray());
    }

    private void addAndIfNeeded() {
        if (containsCondition)
            queryString += " AND";
        else
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
