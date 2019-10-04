package es.rafaco.inappdevtools.library.view.overlay.screens.errors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.rafaco.inappdevtools.library.BuildConfig;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.components.flex.TraceGroupItem;
import es.rafaco.inappdevtools.library.view.components.flex.TraceItem;
import es.rafaco.inappdevtools.library.logic.info.reporters.AppInfoReporter;

public class TraceGrouper {

    public static final String IADT_TAG = "InAppDevTools";
    public static final String YOUR_APP_TAG = "Your App";
    public static final String ANDROID_TAG = "Android Internals";
    public static final String ANDROIDX_LIB_TAG = "AndroidX";
    public static final String SUPPORT_LIB_TAG = "Android Support";
    public static final String OTHER_TAG = "Other";

    HashMap<String, String> matcher;
    List<Object> traceData1;
    List<Object> traceData2;

    public TraceGrouper() {
    }

    public void process(List<Sourcetrace> traces) {
        fillTraceItems(traces);

        markAlwaysExpanded(traceData1);
        injectGroups(traceData1, 0);
        adjustTimeline(traceData1);

        if (traceData2.size()>0) {
            markAlwaysExpanded(traceData2);
            injectGroups(traceData2, 0);
            adjustTimeline(traceData2);
        }
    }

    private void markAlwaysExpanded(List<Object> traceData) {
        boolean firstTraceFound = false;
        boolean firstOpenableFound = false;
        for (int i = 0; i < traceData.size(); i++){
            Object firstItem = traceData.get(i);
            if (firstItem instanceof TraceItem){
                TraceItem current = (TraceItem)firstItem;
                if (!firstTraceFound){
                    current.setAlwaysExpanded(true);
                    firstTraceFound = true;
                }
                if (current.isOpenable()){
                    current.setAlwaysExpanded(true);
                    firstOpenableFound = true;
                }
                if (firstTraceFound && firstOpenableFound){
                    break;
                }
            }
        }
    }

    public FlexibleAdapter getExceptionAdapter(){
        if (traceData1 == null){
            return null;
        }
        return new FlexibleAdapter(1, traceData1);
    }

    public FlexibleAdapter getCauseAdapter(){
        if (traceData2 == null){
            return null;
        }
        return new FlexibleAdapter(1, traceData2);
    }



    private void fillTraceItems(List<Sourcetrace> traces) {
        traceData1 = new ArrayList<>();
        traceData2 = new ArrayList<>();

        boolean causeFound = false;
        for(int i=0;i<traces.size();i++){
            TraceItem item = new TraceItem(traces.get(i));
            fillClassifier(item);

            if (item.getSourcetrace().getExtra()!=null &&
                    item.getSourcetrace().getExtra().equals("cause")){
                causeFound = true;
            }

            if (!causeFound) traceData1.add(item);
            else traceData2.add(item);
        }
    }

    private void fillClassifier(TraceItem item) {
        if (matcher == null)
            initMatcher();

        String classifier = null;
        String className = item.getSourcetrace().getClassName();
        for (HashMap.Entry<String, String> entry : matcher.entrySet()) {
            if (className.startsWith(entry.getKey())) {
                classifier = entry.getValue();
                break;
            }
        }

        item.setTag( (classifier!=null) ? classifier : OTHER_TAG);
        item.setColor(getColor(item));
        item.setFullPath(IadtController.get().getSourcesManager().getPathFromClassName(item.getSourcetrace().getClassName()));
    }

    private void initMatcher() {
        matcher = new HashMap<>();
        AppInfoReporter infoHelper = new AppInfoReporter(IadtController.get().getContext());
        matcher.put(BuildConfig.APPLICATION_ID, IADT_TAG); //Our library
        matcher.put(infoHelper.getPackageName(), YOUR_APP_TAG); //Host app
        matcher.put(infoHelper.getInternalPackageName(), YOUR_APP_TAG); //Host app
        matcher.put("androidx.", ANDROIDX_LIB_TAG);
        matcher.put("com.android.support", SUPPORT_LIB_TAG);
        matcher.put("com.android", ANDROID_TAG);
        matcher.put("android.", ANDROID_TAG);
        matcher.put("java.", ANDROID_TAG);
    }

    private int getColor(TraceItem item) {
        if (item.getTag().equals(YOUR_APP_TAG))
            return R.color.rally_green;
        else if (item.getTag().equals(IADT_TAG))
            return R.color.rally_blue;
        else
            return R.color.rally_gray;
    }

    public static void propagateExpandedState(TraceGroupItem group, List<Object> data) {
        int start = group.getIndex();
        int end = start + group.getCount() + 1;
        for(int i = start + 1; i < end ; i++) {
            Object item = data.get(i);
            if (item instanceof TraceItem ){
                TraceItem current = (TraceItem) item;
                current.setGrouped(true);
                current.setExpanded(group.isExpanded());
            }
        }
    }


    private void injectGroups(List<Object> traceData, int from) {
        TraceGroupItem group = null;
        for (int i = from; i<traceData.size();i++){
            TraceItem current;
            if (traceData.get(i) instanceof TraceItem) {
                current = (TraceItem) traceData.get(i);
                String currentGroupKey = current.getTag();

                if (!current.isAlwaysExpanded()) {
                    if (group == null){
                        group = new TraceGroupItem(currentGroupKey, i, 1, false);
                        group.setColor(current.getColor());
                    }
                    else if (group.getTag().equals(currentGroupKey)){
                        group.setCount(group.getCount()+1);
                    }
                    else{
                        break;
                    }
                }
                else if (group != null) {
                    break;
                }
            }
            else if (group != null) {
                break;
            }
        }
        if (group != null){
            addGroup(traceData, group);

            int nextPosition = group.getIndex() + group.getCount() + 1;
            if (traceData.size() > nextPosition){
                injectGroups(traceData, nextPosition);
            }
        }
    }

    private void addGroup(List<Object> traceData, TraceGroupItem group) {
        traceData.add(group.getIndex(), group);
        propagateExpandedState(group, traceData);
    }

    private void adjustTimeline(List<Object> traceData) {
        if (traceData.size()==0){
            return;
        }
        
        for (int i = 0; i < traceData.size(); i++){
            Object firstItem = traceData.get(i);
            if (firstItem instanceof TraceItem){
                ((TraceItem)firstItem).setPosition(TraceItem.Position.START);
                break;
            }
        }
        Object lastItem = traceData.get(traceData.size()-1);
        if (lastItem instanceof TraceItem){
            ((TraceItem)lastItem).setPosition(TraceItem.Position.END);
        }

        for (int i = traceData.size()-1; i >= 0; i--){
            Object currentItem = traceData.get(i);
            if (currentItem instanceof TraceGroupItem){
                TraceGroupItem lastGroup = (TraceGroupItem) currentItem;
                if (lastGroup.getIndex()+lastGroup.getCount()+1 == traceData.size()){
                    lastGroup.setLastOnCollapsed(true);
                }
                return;
            }
        }
    }
}
