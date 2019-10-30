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

package es.rafaco.inappdevtools.library.logic.log.filter;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.log.datasource.LogAnalysisHelper;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.AnalysisData;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class LogFilterHelper {

    public enum Preset { REPRO_STEPS, DEBUG, CRASHES, NETWORK, CUSTOM, ALL }
    private LogUiFilter uiFilter;

    private LogAnalysisHelper analysis;

    public LogFilterHelper(LogUiFilter filter) {
        this.uiFilter = filter;
        this.analysis = new LogAnalysisHelper();
    }

    public LogFilterHelper(Preset preset) {
        this(new LogUiFilter());
        applyPreset(preset);
    }

    public LogUiFilter getUiFilter() {
        return uiFilter;
    }

    public LogBackFilter getBackFilter() {
        return populateBackFilter();
    }

    //region [ PRESETS ]
    //TODO: custom presets defined from host app

    public void applyPreset(Preset preset) {
        if (preset.equals(Preset.REPRO_STEPS)){
            applyReproStepsPreset();
        }
        else if (preset.equals(Preset.NETWORK)){
            applyNetworkPreset();
        }
        else if (preset.equals(Preset.DEBUG)){
            applyDebugPreset();
        }
        else if (preset.equals(Preset.CRASHES)){
            applyCrashPreset();
        }
        else if (preset.equals(Preset.ALL)){
            applyAllPreset();
        }
        else if (preset.equals(Preset.CUSTOM)){
            applyCustomPreset();
        }
    }

    private void applyReproStepsPreset() {
        applyAllPreset();
        uiFilter.setSessionInt(1);   //Current
        uiFilter.setSeverityInt(2);  //Info
        uiFilter.setTypeInt(1);      //Events
    }

    private void applyNetworkPreset() {
        applyAllPreset();
        uiFilter.setSessionInt(1);   //Current
        uiFilter.setSeverityInt(2);  //Info
        uiFilter.setTypeInt(1);      //Events
        uiFilter.setCategoryInt(0);
        uiFilter.setCategoryName("Network");
    }

    private void applyDebugPreset() {
        applyAllPreset();
        uiFilter.setWrapLines(true);
        uiFilter.setSessionInt(1);   //Current
    }

    private void applyCrashPreset() {
        applyAllPreset();
        uiFilter.setTypeInt(1);      //Events
        uiFilter.setSeverityInt(4);  //Error
        uiFilter.setCategoryInt(0);
        uiFilter.setCategoryName("Error");
    }

    public void applyAllPreset() {
        uiFilter = new LogUiFilter();
        uiFilter.setWrapLines(false);
        uiFilter.setText("");
        uiFilter.setSessionInt(0);
        uiFilter.setSeverityInt(0);
        uiFilter.setTypeInt(0);
        uiFilter.setCategoryInt(0);
        uiFilter.setCategoryName("All");
        uiFilter.setTagInt(0);
        uiFilter.setTagName("All");
    }

    private void applyCustomPreset() {
        if (LogFilterStore.get() != null){
            uiFilter = LogFilterStore.get();
        }
        else{
            applyDebugPreset();
        }
    }

    //endregion

    //region [ OPTIONS ]

    public List<String> getSessionOptions() {
        ArrayList<String> list = new ArrayList<>();
        List<AnalysisData> sessions = analysis.getSessionResult();
        list.add("All");
        String sessionOrdinal;
        for (AnalysisData item : sessions) {
            sessionOrdinal = Humanizer.ordinal(Integer.valueOf(item.getName()));
            if (list.size() == 1){
                list.add("Current (" + sessionOrdinal + ")");
            }
            else if (list.size() == 2){
                list.add("Previous (" + sessionOrdinal + ")");
            }
            else {
                list.add("Session " + sessionOrdinal);
            }
        }
        return list;
    }

    public List<String> getSeverityOptions() {
        String[] levelsArray = IadtController.get().getContext().getResources()
                .getStringArray(R.array.log_levels);
        List<String> list = new ArrayList<>();
        for (String item : levelsArray) {
            list.add(item + " XX%");
        }
        return list;
    }

    public String getSeverityLongString() {
        String[] levelsArray = IadtController.get().getContext().getResources().getStringArray(R.array.log_levels);
        return Humanizer.toCapitalCase(levelsArray[uiFilter.getSeverityInt()]);
    }

    public List<String> getTypeOptions() {
        List<String> list = new ArrayList<>();
        list.add("All");
        list.add("Events");
        list.add("Logcat");
        return list;
    }

    public List<String> getCategoryOptions() {
        List<String> list = new ArrayList<>();
        List<AnalysisData> categoryResult = analysis.getCategoryResult();
        list.add("All");
        for (AnalysisData item : categoryResult) {
            list.add(item.getName() + " " + item.getPercentage()+ "%");
        }
        return list;
    }
    //TODO: multiple tags selection
    // At UI, a dialog selector with checkboxes could work
    public List<String> getTagOptions() {
        ArrayList<String> list = new ArrayList<>();
        List<AnalysisData> subcategoryResult = analysis.getLogcatTagResult();
        list.add("All");
        for (AnalysisData item : subcategoryResult) {
            list.add(item.getName() + " " + item.getPercentage()+ "%");
        }
        return list;
    }

    //endregion

    //region [ OVERVIEW ]

    public String getOverview(){
        AnalysisData currentAnalysisData = analysis.getCurrentFilterOverview(getBackFilter()).get(0);
        String result = "";

        if (TextUtils.isEmpty(uiFilter.getText())
                && uiFilter.getTypeInt() == 0
                && uiFilter.getSessionInt() == 0
                && uiFilter.getSeverityInt() == 0
                && uiFilter.getCategoryInt() == 0
                && uiFilter.getTagInt() == 0){
            result += "All from all sessions.";
            result += String.format(" %s%% of %s",
            currentAnalysisData.getPercentage(),
            analysis.getTotalLogSize());
            return result;
        }

        if (uiFilter.getTypeInt() != 0){
            result += ((uiFilter.getTypeInt() == 1) ? "Events" : "Logs") + " ";
        }else{
            result += "Events and Logs ";
        }

        if (uiFilter.getSessionInt() != 0){
            if(uiFilter.getSessionInt()==1){
                result += "from current session" + ", ";
            }
            else if(uiFilter.getSessionInt()==2){
                result += "from previous session" + ", ";
            }
            else{
                long target = DevToolsDatabase.getInstance().sessionDao().count() - uiFilter.getSessionInt() + 1;
                Session selected = DevToolsDatabase.getInstance().sessionDao().findById(target);
                result += "from session " + Humanizer.ordinal((int)selected.getUid()) + ", ";
            }
        }

        if (uiFilter.getSeverityInt() != 0){
            result += "with severity greater than " + getSeverityLongString() + ", ";
        }

        if (uiFilter.getCategoryInt() != 0){
            result += uiFilter.getCategoryName() + " events" + ", ";
        }

        if (uiFilter.getTagInt() != 0 ){
            result += uiFilter.getTagName() + " logs" +  ", ";
        }

        if (!TextUtils.isEmpty(uiFilter.getText())){
            result += "containing '" + uiFilter.getText() + "'" + ", ";
        }

        //remove last comma separator
        if (result.endsWith(", ")){
            result = result.substring(0, result.length() - 2);
        }

        //replace second last comma by "and"
        int lastCommaPosition = result.lastIndexOf(", ");
        if (lastCommaPosition > 0 && result.length() > lastCommaPosition + 2){
            result = result.substring(0, lastCommaPosition) + " and " +
                    result.substring(lastCommaPosition + 2);
        }

        result.trim();
        result += ". ";
        result += String.format(" %s%% of total (%s/%s)",
                currentAnalysisData.getPercentage(),
                currentAnalysisData.getCount(),
                analysis.getTotalLogSize());

        return result;
    }

    //endregion

    //region [ BACK FILTER POPULATION ]

    public LogBackFilter populateBackFilter(){
        LogBackFilter backFilter = new LogBackFilter();
        populateBackText(backFilter);
        populateBackSession(backFilter);
        populateBackSeverity(backFilter);
        populateBackType(backFilter);
        populateBackCategory(backFilter);
        populateBackTag(backFilter);
        return backFilter;
    }

    public void populateBackText(LogBackFilter backFilter){
        backFilter.setText(uiFilter.getText());
    }

    public void populateBackSession(LogBackFilter backFilter){
        if (uiFilter.getSessionInt() == 0){ //All
            backFilter.setFromDate(-1);
            backFilter.setToDate(-1);
        }
        else if (uiFilter.getSessionInt() == 1){ //Current
            Session selected = DevToolsDatabase.getInstance().sessionDao().getLast();
            backFilter.setFromDate(selected.getDate());
            backFilter.setToDate(-1);
        }
        else{ //Previous and others
            //TODO: better selection
            long target = DevToolsDatabase.getInstance().sessionDao().count() - uiFilter.getSessionInt() + 1;
            Session selected = DevToolsDatabase.getInstance().sessionDao().findById(target);
            backFilter.setFromDate(selected.getDate());
            //TODO: filter toDate properly
            Session next = DevToolsDatabase.getInstance().sessionDao().findById(target + 1);
            backFilter.setToDate(next.getDate());
        }
        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "Session changed to: " + uiFilter.getSessionInt()
                    + " (from " + backFilter.getFromDate() + ""
                    + " to " + backFilter.getToDate() + ")");
    }

    public void populateBackSeverity(LogBackFilter backFilter){
        backFilter.setSeverities(getFilterSeverities(uiFilter.getSeverityInt()));

        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "SeverityInt changed to: " + uiFilter.getSeverityInt()
                    + " (" + getSeverityShortString() + ")");
    }

    protected List<String> getFilterSeverities(int selected) {
        List<String> levels = Arrays.asList("V", "D", "I", "W", "E");
        return levels.subList(selected, levels.size());
    }

    public String getSeverityShortString(){
        List<String> levels = Arrays.asList("V", "D", "I", "W", "E");
        return levels.get(uiFilter.getSeverityInt());
    }

    public void populateBackType(LogBackFilter backFilter){

        List<String> inSelection = new ArrayList<>();
        List<String> notInSelection = new ArrayList<>();
        if(uiFilter.getTypeInt() == 1){
            notInSelection.add("Logcat");
        }else if (uiFilter.getTypeInt() == 2){
            inSelection.add("Logcat");
        }
        backFilter.setNotCategories(notInSelection);
        backFilter.setCategories(inSelection);

        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "TypeInt changed to: " + uiFilter.getTypeInt() + " (IN " + inSelection
                    + " and NOT IN " + notInSelection +")");
    }

    public void populateBackCategory(LogBackFilter backFilter){

        List<String> inCats = new ArrayList<>();
        if (uiFilter.getCategoryInt() == 0){ //All
            backFilter.setCategories(inCats);
        }else{
            inCats.add(uiFilter.getCategoryName());
            backFilter.setCategories(inCats);
        }

        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "CategoryInt changed to: " + uiFilter.getCategoryInt()
                    + " (IN " + inCats + ")");
    }

    public void populateBackTag(LogBackFilter backFilter){
        List<String> subcats = new ArrayList<>();
        if (uiFilter.getTagInt() == 0) { //All
            backFilter.setSubcategories(subcats);
        } else {
            subcats.add(uiFilter.getTagName());
            backFilter.setSubcategories(subcats);
            //List<String> cats = new ArrayList<>();
            //cats.add("Logcat");
            //filter.setCategories(cats);
        }

        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "TagInt changed to " + uiFilter.getTagInt()
                    + " (IN subcategory " + subcats + ")");
    }

    //endregion

}
