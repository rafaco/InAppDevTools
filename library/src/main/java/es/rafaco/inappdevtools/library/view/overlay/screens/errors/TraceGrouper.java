package es.rafaco.inappdevtools.library.view.overlay.screens.errors;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.components.flex.TraceGroupItem;
import es.rafaco.inappdevtools.library.view.components.flex.TraceGroupViewHolder;
import es.rafaco.inappdevtools.library.view.components.flex.TraceItem;

public class TraceGrouper {

    List<Object> traceData1;
    List<Object> traceData2;

    public TraceGrouper() {
    }


    public void process(List<Sourcetrace> traces) {
        traceData1 = new ArrayList<>();
        traceData2 = new ArrayList<>();

        boolean causeFound = false;
        for(int i=0;i<traces.size();i++){
            TraceItem item = new TraceItem(traces.get(i));
            if (i==0){
                item.setPosition(TraceItem.Position.START);
            }
            else if (item.getSourcetrace().getExtra()!=null
                    && item.getSourcetrace().getExtra().equals("cause")){
                causeFound = true;
                item.setPosition(TraceItem.Position.START);
            }

            if (!causeFound) traceData1.add(item);
            else traceData2.add(item);
        }

        injectGroups(traceData1, 1); //Never group the first item
        finishTimeline(traceData1);

        if (causeFound) {
            injectGroups(traceData2, 1); //Never group the first item
            finishTimeline(traceData2);
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
                String currentGroupKey = getGroupKey(current.getSourcetrace().getClassName());

                if (!TextUtils.isEmpty(currentGroupKey)) {
                    if (group == null){
                        group = new TraceGroupItem(currentGroupKey, i, 1, false);
                    }
                    else if (group.getGroupKey().equals(currentGroupKey)){
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

    private String getGroupKey(String className) {

        HashMap<String, String> matcher = new HashMap<>();
        matcher.put("android.", "Android Internal");
        matcher.put("com.android", "Android Internal");
        matcher.put("java.", "Java");
        matcher.put("androidx.", "AndroidX Library");

        for (HashMap.Entry<String, String> entry : matcher.entrySet()) {
            if (className.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void addGroup(List<Object> traceData, TraceGroupItem group) {
        traceData.add(group.getIndex(), group);
        propagateExpandedState(group, traceData);
    }

    private void finishTimeline(List<Object> traceData) {
        Object lastItem = traceData.get(traceData.size()-1);

        if (lastItem instanceof TraceItem){
            ((TraceItem)lastItem).setPosition(TraceItem.Position.END);
        }

        for (int i = traceData.size()-2; i < 0; i--){
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
