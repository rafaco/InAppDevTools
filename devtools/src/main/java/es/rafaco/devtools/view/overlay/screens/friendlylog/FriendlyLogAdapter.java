package es.rafaco.devtools.view.overlay.screens.friendlylog;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.rafaco.devtools.R;
import es.rafaco.devtools.storage.db.entities.Friendly;

public class FriendlyLogAdapter
        extends PagedListAdapter<Friendly, FriendlyLogViewHolder> {

    private static DiffUtil.ItemCallback<Friendly> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Friendly>() {
                // Concert details may have changed if reloaded from the database,
                // but ID is fixed.
                @Override
                public boolean areItemsTheSame(Friendly oldConcert, Friendly newConcert) {
                    return oldConcert.getUid() == newConcert.getUid();
                }

                @Override
                public boolean areContentsTheSame(Friendly oldConcert,
                                                  Friendly newConcert) {
                    return oldConcert.equals(newConcert);
                }
            };

    private static int selectedItem = -1;

    private FriendlyLogViewHolder.OnClickListener mClickListener;

    protected FriendlyLogAdapter() {
        super(DIFF_CALLBACK);

        setClickListener();
    }

    @NonNull
    @Override
    public FriendlyLogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tool_friendlylog_item, viewGroup, false);

        return new FriendlyLogViewHolder(itemView, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendlyLogViewHolder holder,
                                 int position) {
        Friendly item = getItem(position);
        if (item != null) {
            Log.d("Friendly", "bindTo()" + position + ":" + item.getUid());
            holder.bindTo(item, selectedItem == position);
        } else {
            // Null defines a placeholder item - PagedListAdapter automatically
            // invalidates this row when the actual object is loaded from the
            // database.
            Log.d("Friendly", "Placeholder for" + position);
            holder.showPlaceholder();
        }
    }

    public void setClickListener(){
        setClickListener((itemView, position) -> {
            if (selectedItem == -1) {
                selectedItem = position;
            }
            else if (position == selectedItem){
                selectedItem = -1;
            }
            else if (selectedItem != -1){
                int previous = selectedItem;
                selectedItem = position;
                notifyItemChanged(previous);
            }
            notifyItemChanged(position);
        });
    }

    public void setClickListener(FriendlyLogViewHolder.OnClickListener clickListener) {
        mClickListener = clickListener;
    }
}