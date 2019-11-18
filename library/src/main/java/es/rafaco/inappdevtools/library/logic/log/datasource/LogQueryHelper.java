/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.logic.log.datasource;

import android.text.TextUtils;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.sqlite.db.SimpleSQLiteQuery;
//@import androidx.sqlite.db.SupportSQLiteQuery;
//#else
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.filter.LogBackFilter;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;

public class LogQueryHelper {

    private final LogBackFilter filter;
    private String queryString = new String();
    private List<Object> args = new ArrayList();
    private boolean containsCondition = false;

    public LogQueryHelper(LogBackFilter filter) {
        this.filter = filter;
    }

    public SupportSQLiteQuery getFilterQuery() {
        queryString = new String();
        args = new ArrayList();
        containsCondition = false;

        queryString += "SELECT * FROM friendly";

        if(!TextUtils.isEmpty(filter.getText())){
            addConjunction();
            queryString += " ( message LIKE ? OR category LIKE ? OR subcategory LIKE ? OR extra LIKE ? )";
            String likeFilter = "%" + filter.getText() + "%";
            multiplicateArg(likeFilter, 4);
        }

        if(filter.getSeverities().size() < 5) {
            addConjunction();
            addInWithList("severity", filter.getSeverities(), true);
        }

        if(!filter.getCategories().isEmpty()){
            addConjunction();
            addInWithList("category", filter.getCategories(), true);
        }

        if(!filter.getNotCategories().isEmpty()){
            addConjunction();
            addInWithList("category", filter.getNotCategories(), false);
        }

        if(!filter.getSubcategories().isEmpty()){
            addConjunction();
            addInWithList("subcategory", filter.getSubcategories(), true);
        }

        if(filter.getFromDate()>0){
            addConjunction();
            queryString += " date >= ?";
            args.add(filter.getFromDate());
        }

        if(filter.getToDate()>0){
            addConjunction();
            //TODO: replace by <= when using real finish date instead of next session date
            queryString += " date < ?";
            args.add(filter.getToDate());
        }

        queryString += " ORDER BY date ASC";

        if (LogScreen.isLogDebug())
            Log.d(Iadt.TAG, "FILTER QUERY: " + queryString);

        return new SimpleSQLiteQuery(queryString, args.toArray());
    }

    public SupportSQLiteQuery getPositionQuery(long id) {
        getFilterQuery();

        queryString = "SELECT COUNT(*) AS date" +
                " FROM ( " + queryString + ")" +
                " WHERE date < (SELECT date FROM friendly WHERE uid = ? )";
        args.add(id);

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

    public SimpleSQLiteQuery getFilterSizeQuery(){
        getFilterQuery();

        queryString = queryString.replace("SELECT *",
                "SELECT 'Current filter' AS name,"
                        + " COUNT(*) AS count,"
                        + " (count(*) * 100.0 / (select count(*) from friendly)) AS percentage");
        queryString = queryString.replace("ORDER BY date ASC", "");

        if (LogScreen.isLogDebug())
            Log.d(Iadt.TAG, "FILTER SIZE QUERY: " + queryString);

        return new SimpleSQLiteQuery(queryString, args.toArray());
    }
}
