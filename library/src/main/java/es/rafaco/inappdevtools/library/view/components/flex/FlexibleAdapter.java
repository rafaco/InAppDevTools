package es.rafaco.inappdevtools.library.view.components.flex;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.integrations.RunnableConfig;
import es.rafaco.inappdevtools.library.logic.integrations.ThinItem;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;

import static tech.linjiang.pandora.util.Utils.getContext;

public class FlexibleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TYPE_HEADER = "TYPE_HEADER";
    public static final String TYPE_BUTTON = "TYPE_BUTTON";
    public static final String TYPE_LINK = "TYPE_LINK";
    public static final String TYPE_TRACE = "TYPE_TRACE";
    public static final String TYPE_TRACE_GROUP = "TYPE_TRACE_GROUP";

    private List<FlexibleItemDescriptor> descriptors;
    private final int spanCount;
    private List<Object> items;

    public FlexibleAdapter(int spanCount, List<Object> data) {
        this.spanCount = spanCount;
        this.items = data;

        descriptors = new ArrayList<>();
        descriptors.add(new FlexibleItemDescriptor(TYPE_HEADER, String.class,  HeaderViewHolder.class, R.layout.flexible_header));
        descriptors.add(new FlexibleItemDescriptor(TYPE_BUTTON, RunnableConfig.class,  RunnableViewHolder.class, R.layout.flexible_button));
        descriptors.add(new FlexibleItemDescriptor(TYPE_LINK, ThinItem.class,  LinkViewHolder.class, R.layout.flexible_link));
        descriptors.add(new FlexibleItemDescriptor(TYPE_TRACE, TraceItem.class,  TraceViewHolder.class, R.layout.flexible_trace));
        descriptors.add(new FlexibleItemDescriptor(TYPE_TRACE_GROUP, TraceGroupItem.class,  TraceGroupViewHolder.class, R.layout.flexible_trace_group));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        GridLayoutManager manager = new GridLayoutManager(getContext(), spanCount, RecyclerView.VERTICAL, false);
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
        if (position < 0 || position >= items.size()){
            return 2;
        }
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        FlexibleItemDescriptor desc = descriptors.get(getItemViewType(i));
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(desc.layoutResourceId, viewGroup, false);

        RecyclerView.ViewHolder holder = null;
        try {
            Constructor<? extends RecyclerView.ViewHolder> ctor = desc.viewHolderClass.getConstructor(View.class);
            holder = ctor.newInstance(new Object[] { view });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Object viewData = items.get(i);
        FlexibleItemDescriptor desc = descriptors.get(getItemViewType(i));

        desc.viewHolderClass.cast(viewHolder).bindTo(desc.dataClass.cast(viewData));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void replaceItems(List<Object> data){
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                items.clear();
                items.addAll(data);
                notifyDataSetChanged();
            }
        });
    }

    public class FlexibleItemDescriptor {
        public final String name;
        public final Class<?> dataClass;
        public final Class<? extends FlexibleViewHolder> viewHolderClass;
        public final int layoutResourceId;

        public FlexibleItemDescriptor(String name, Class<?> dataClass, Class<? extends FlexibleViewHolder> viewHolderClass, int layoutResourceId) {
            this.name = name;
            this.dataClass = dataClass;
            this.viewHolderClass = viewHolderClass;
            this.layoutResourceId = layoutResourceId;
        }
    }
}
