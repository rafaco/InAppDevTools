package es.rafaco.devtools.view.components;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.integrations.RunnableConfig;

import static tech.linjiang.pandora.util.Utils.getContext;

public class FlexibleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_BUTTON = 1;
    private final int spanCount;

    private List<Object> items;

    public FlexibleAdapter(int spanCount, List<Object> data) {
        this.spanCount = spanCount;
        this.items = data;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        GridLayoutManager manager = new GridLayoutManager(getContext(), spanCount, LinearLayoutManager.VERTICAL, false);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(getItemViewType(position)){
                    case FlexibleAdapter.TYPE_HEADER:
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
        if (item instanceof String)
            return TYPE_HEADER;
        else if (item instanceof RunnableConfig)
            return TYPE_BUTTON;
        else
            return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        switch (getItemViewType(i)){
            case TYPE_HEADER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.flexible_header, viewGroup, false);
                return new HeaderViewHolder(view);
            case TYPE_BUTTON:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.flexible_button, viewGroup, false);
                return new RunnableViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Object viewData = items.get(i);
        switch (getItemViewType(i)) {
            case TYPE_HEADER:
                ((HeaderViewHolder) viewHolder).bindTo((String) viewData);
                break;
            case TYPE_BUTTON:
                ((RunnableViewHolder) viewHolder).bindTo((RunnableConfig) viewData);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
