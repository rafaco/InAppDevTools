package es.rafaco.inappdevtools.library.logic.log.datasource;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.AnalysisItem;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class LogFilterHelper {

    private LogAnalysisHelper analysis;
    private LogFilter filter;
    private int sessionInt;
    private int severityInt;
    private int typeInt;
    private int categoryInt;
    private int tagInt;

    public LogFilterHelper() {
        this(new LogFilter());
        defaultFilter();
    }

    public LogFilterHelper(LogFilter filter) {
        this.filter = filter;
        this.analysis = new LogAnalysisHelper();
    }

    public LogFilter getFilter() {
        return filter;
    }

    public void setFilter(LogFilter filter) {
        this.filter = filter;
    }

    public String getOverview(){
        AnalysisItem currentAnalysisItem = analysis.getCurrentFilterOverview(getFilter()).get(0);
        String result = Humanizer.fullStop() + "Currently showing ";

        if (TextUtils.isEmpty(filter.getText())
                && getTypeInt() == 0
                && getSessionInt() == 0
                && getSeverityInt() == 0
                && getCategoryInt() == 0
                && getTagInt() == 0){
            result += "All";
            result += ". ";
            result += String.format("all. %s%% (%s)",
                    currentAnalysisItem.getPercentage(),
                    analysis.getTotalLogSize());
            result += Humanizer.fullStop();
            return result;
        }

        if (getTypeInt() != 0){
            result += ((getTypeInt() == 1) ? "Events" : "Logs") + " ";
        }else{
            result += "Events and Logs ";
        }

        if (getSessionInt() != 0){
            if(getSessionInt()==1){
                result += "from current session " + ", ";
            }else{
                long target = DevToolsDatabase.getInstance().sessionDao().count() - getSessionInt() + 1;
                Session selected = DevToolsDatabase.getInstance().sessionDao().findById(target);
                result += "from session " + Humanizer.ordinal((int)selected.getUid()) + ", ";
            }
        }

        if (getSeverityInt() != 0){
            result += "with severity greater than " + getSeverityLongString() + ", ";
        }

        if (getCategoryInt() != 0){
            result += filter.getCategories().get(0) + " events" + ", ";
        }

        if (getTagInt() != 0 ){
            result += filter.getSubcategories().get(0) + " logcat" +  ", ";
        }

        if (!TextUtils.isEmpty(filter.getText())){
            result += "containing '" + filter.getText() + "'" + ", ";
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

        result += ". ";
        result += String.format("%s%% (%s/%s)",
                currentAnalysisItem.getPercentage(),
                currentAnalysisItem.getCount(),
                analysis.getTotalLogSize());
        result += Humanizer.fullStop();

        return result;
    }

    //region [ TEXT ]

    public void setTextFilter(String text) {
        filter.setText(text);
    }

    public void setSeverityInt(int selectedSeverity) {
        this.severityInt = selectedSeverity;
        filter.setSeverities(getSeveritiesFromSelected(selectedSeverity));
        Log.v(Iadt.TAG, "SeverityInt changed to: " + selectedSeverity + " (" + getSeverityShortString() + ")");
    }

    //endregion

    //region [ SESSION ]

    public List<String> getSessionOptions() {
        ArrayList<String> list = new ArrayList<>();
        List<AnalysisItem> sessions = analysis.getSessionResult();
        list.add("All 100%");
        String sessionOrdinal;
        for (AnalysisItem item : sessions) {
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

    public int getSessionInt() {
        return sessionInt;
    }

    public void setSessionInt(int position) {
        this.sessionInt = position;

        if (sessionInt == 0){ //All
            filter.setFromDate(-1);
            filter.setToDate(-1);
        }
        else if (sessionInt == 1){ //Current
            Session selected = DevToolsDatabase.getInstance().sessionDao().getLast();
            filter.setFromDate(selected.getDate());
            filter.setToDate(-1);
        }
        else{ //Previous and others
            //TODO: better selection
            long target = DevToolsDatabase.getInstance().sessionDao().count() - position + 1;
            Session selected = DevToolsDatabase.getInstance().sessionDao().findById(target);
            filter.setFromDate(selected.getDate());
            //TODO: filter toDate properly
            Session next = DevToolsDatabase.getInstance().sessionDao().findById(target + 1);
            filter.setToDate(next.getDate());
        }
        Log.v(Iadt.TAG, "Session changed to: " + sessionInt
                + " -> from " + filter.getFromDate() + ""
                + " to " + filter.getToDate() + "");
    }

    //endregion

    //region [ SEVERITY ]

    public List<String> getSeverityOptions() {
        String[] levelsArray = IadtController.get().getAppContext().getResources()
                .getStringArray(R.array.log_levels);
        List<String> list = new ArrayList<>();
        for (String item : levelsArray) {
            list.add(item + " XX%");
        }
        return list;
    }

    public int getSeverityInt() {
        return severityInt;
    }

    public String getSeverityShortString(){
        List<String> levels = Arrays.asList("V", "D", "I", "W", "E");
        return levels.get(severityInt);
    }

    public String getSeverityLongString() {
        String[] levelsArray = IadtController.get().getAppContext().getResources().getStringArray(R.array.log_levels);
        return Humanizer.toCapitalCase(levelsArray[severityInt]);
    }

    protected List<String> getSeveritiesFromSelected(int selected) {
        List<String> levels = Arrays.asList("V", "D", "I", "W", "E");
        return levels.subList(selected, levels.size());
    }

    //endregion

    //region [ TYPE ]

    public List<String> getTypeOptions() {
        List<String> list = new ArrayList<>();
        list.add("All");
        list.add("Events");
        list.add("Logcat");
        return list;
    }

    public int getTypeInt() {
        return typeInt;
    }

    public void setTypeInt(int typeInt) {
        this.typeInt = typeInt;

        List<String> inSelection = new ArrayList<>();
        List<String> notInSelection = new ArrayList<>();
        if(typeInt == 1){
            notInSelection.add("Logcat");
        }else if (typeInt == 2){
            inSelection.add("Logcat");
        }
        filter.setNotCategories(notInSelection);
        filter.setCategories(inSelection);
        Log.v(Iadt.TAG, "TypeInt changed to: " + typeInt + " IN (" + inSelection + ") and NOT IN (" + notInSelection +")");
    }

    //endregion

    //region [ CATEGORIES ]

    public List<String> getCategoryOptions() {
        List<String> list = new ArrayList<>();
        List<AnalysisItem> categoryResult = analysis.getCategoryResult();
        list.add("All 100%");
        for (AnalysisItem item : categoryResult) {
            list.add(item.getName() + " " + item.getPercentage()+ "%");
        }
        return list;
    }

    public int getCategoryInt() {
        return categoryInt;
    }

    public void setCategoryInt(int categoryInt, String realItemCategory) {
        this.categoryInt = categoryInt;

        List<String> inCats = new ArrayList<>();
        if (categoryInt == 0){ //All
            filter.setCategories(inCats);
        }else{
            inCats.add(realItemCategory);
            filter.setCategories(inCats);
        }
        Log.v(Iadt.TAG, "CategoryInt changed to: " + categoryInt + " IN (" + inCats + ")");
    }

    //endregion

    //region [ TAGS / SUBCATEGORIES ]

    public List<String> getTagList() {
        ArrayList<String> list = new ArrayList<>();
        List<AnalysisItem> subcategoryResult = analysis.getLogcatTagResult();
        list.add("All 100%");
        for (AnalysisItem item : subcategoryResult) {
            list.add(item.getName() + " " + item.getPercentage()+ "%");
        }
        return list;
    }

    public int getTagInt() {
        return tagInt;
    }

    public void setTagInt(int tagInt, String realSubcategory) {
        this.tagInt = tagInt;

        List<String> subcats = new ArrayList<>();
        if (tagInt == 0) { //All
            filter.setSubcategories(subcats);
        } else {
            subcats.add(realSubcategory);
            filter.setSubcategories(subcats);
            //List<String> cats = new ArrayList<>();
            //cats.add("Logcat");
            //filter.setCategories(cats);
        }
        Log.v(Iadt.TAG, "TagInt changed to " + tagInt + " -> IN subcategory(" + subcats + ")");
    }

    //endregion

    private void defaultFilter() {
        setTextFilter("");
        setSessionInt(1);   //Current
        setSeverityInt(2);  //Info
        setTypeInt(1);      //Events
        setCategoryInt(0, "All");
        setTagInt(0, "All");
    }

    public void disableAll() {
        setTextFilter("");
        setSessionInt(0);
        setSeverityInt(0);
        setTypeInt(0);
        setCategoryInt(0, "All");
        setTagInt(0, "All");
    }
}
