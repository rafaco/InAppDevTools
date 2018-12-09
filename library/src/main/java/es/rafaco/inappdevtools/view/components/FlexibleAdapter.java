package es.rafaco.inappdevtools.view.components;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import es.rafaco.inappdevtools.R;
import es.rafaco.inappdevtools.logic.integrations.RunnableConfig;
import es.rafaco.inappdevtools.logic.integrations.ThinItem;
import es.rafaco.inappdevtools.logic.utils.ThreadUtils;

import static tech.linjiang.pandora.util.Utils.getContext;

public class FlexibleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_BUTTON = 1;
    public static final int TYPE_LINK = 2;

    private final int spanCount;
    private List<Object> items;

    public FlexibleAdapter(int spanCount, List<Object> data) {
        this.spanCount = spanCount;
        this.items = data;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        GridLayoutManager manager = new GridLayoutManager(getContext(), spanCount, RecyclerView.VERTICAL, false);
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
        if (position <0 || position > items.size()){
            return 2;
        }
        Object item = items.get(position);
        if (item instanceof String)
            return TYPE_HEADER;
        else if (item instanceof RunnableConfig)
            return TYPE_BUTTON;
        else if (item instanceof ThinItem)
            return TYPE_LINK;
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
            case TYPE_LINK:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.flexible_link, viewGroup, false);
                return new LinkViewHolder(view);
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
            case TYPE_LINK:
                ((LinkViewHolder) viewHolder).bindTo((ThinItem) viewData);
                break;
            default:
                break;
        }
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
}
