package es.rafaco.inappdevtools.library.view.components.flex;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//@import androidx.core.view.ViewCompat;
//@import androidx.recyclerview.widget.GridLayoutManager;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
//#endif

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.info.data.InfoGroupData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.db.entities.AnalysisItem;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;

import static tech.linjiang.pandora.util.Utils.getContext;

public class FlexibleAdapter extends RecyclerView.Adapter<FlexibleViewHolder> {

    public class FlexibleItemDescriptor {
        
        public final Class<?> dataClass;
        public final Class<? extends FlexibleViewHolder> viewHolderClass;
        public final int layoutResourceId;

        public FlexibleItemDescriptor(Class<?> dataClass, Class<? extends FlexibleViewHolder> viewHolderClass, int layoutResourceId) {
            this.dataClass = dataClass;
            this.viewHolderClass = viewHolderClass;
            this.layoutResourceId = layoutResourceId;
        }
    }

    private List<FlexibleItemDescriptor> descriptors;
    private final int spanCount;
    private List<Object> items;
    private Screen screen;

    public FlexibleAdapter(int spanCount, List<Object> data) {
        this.spanCount = spanCount;
        this.items = data;

        descriptors = new ArrayList<>();
        descriptors.add(new FlexibleItemDescriptor(String.class, HeaderViewHolder.class, R.layout.flexible_item_header));
        descriptors.add(new FlexibleItemDescriptor(RunButton.class, RunButtonViewHolder.class, R.layout.flexible_item_run_button));
        descriptors.add(new FlexibleItemDescriptor(CardData.class, CardViewHolder.class, R.layout.flexible_item_card));
        descriptors.add(new FlexibleItemDescriptor(LinkItem.class, LinkViewHolder.class, R.layout.flexible_item_link));
        descriptors.add(new FlexibleItemDescriptor(TraceItem.class, TraceViewHolder.class, R.layout.flexible_item_trace));
        descriptors.add(new FlexibleItemDescriptor(TraceGroupItem.class, TraceGroupViewHolder.class, R.layout.flexible_item_trace_group));
        descriptors.add(new FlexibleItemDescriptor(AnalysisItem.class, AnalysisViewHolder.class, R.layout.flexible_item_analysis));
        descriptors.add(new FlexibleItemDescriptor(ConfigItem.class, ConfigViewHolder.class, R.layout.flexible_item_config));
        descriptors.add(new FlexibleItemDescriptor(InfoGroupData.class, InfoGroupViewHolder.class, R.layout.flexible_item_info_group));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        final GridLayoutManager manager = new GridLayoutManager(getContext(), spanCount, RecyclerView.VERTICAL, false);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(getItemViewType(position)){
                    case 0: //TODO: FlexibleAdapter.TYPE_HEADER:
                        return manager.getSpanCount();
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(manager);
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        for (int i=0; i<descriptors.size(); i++){
            FlexibleItemDescriptor descriptor = descriptors.get(i);
            if (item.getClass().equals(descriptor.dataClass)){
                return i;
            }
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public FlexibleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        FlexibleItemDescriptor desc = descriptors.get(viewType);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(desc.layoutResourceId, viewGroup, false);

        FlexibleViewHolder holder = null;
        try {
            Constructor<? extends FlexibleViewHolder> ctor = desc.viewHolderClass.getConstructor(View.class, FlexibleAdapter.class);
            holder = ctor.newInstance(new Object[] { view, this });
        } catch (Exception e) {
            FriendlyLog.logException("Exception", e);
        }
        holder.onCreate(viewGroup, viewType);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FlexibleViewHolder holder, int position) {
        Object viewData = items.get(position);
        FlexibleItemDescriptor desc = descriptors.get(getItemViewType(position));
        desc.viewHolderClass.cast(holder).bindTo(desc.dataClass.cast(viewData), position);
    }

    public List<Object> getItems(){
        return items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void replaceItems(final List<Object> data){
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public Screen getScreen() {
        return screen;
    }
}
