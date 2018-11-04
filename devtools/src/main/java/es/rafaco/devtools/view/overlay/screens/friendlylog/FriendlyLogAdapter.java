package es.rafaco.devtools.view.overlay.screens.friendlylog;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.rafaco.devtools.R;
import es.rafaco.devtools.storage.db.entities.Friendly;

public class FriendlyLogAdapter
        extends PagedListAdapter<Friendly, FriendlyLogViewHolder> {

    private static DiffUtil.ItemCallback<Friendly> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Friendly>() {
                @Override
                public boolean areItemsTheSame(Friendly oldItem, Friendly newItem) {
                    return oldItem.getUid() == newItem.getUid();
                }
                @Override
                public boolean areContentsTheSame(Friendly oldItem,
                                                  Friendly newItem) {
                    return oldItem.equals(newItem);
                }
            };

    private static long selectedItemId = -1;
    private static int selectedItemPosition = -1;
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
            //Log.d("Friendly", "bindTo()" + position + ":" + item.getUid());
            holder.bindTo(item, selectedItemId == item.getUid());
        } else {
            //Log.d("Friendly", "Placeholder for" + position);
            holder.showPlaceholder();
        }
    }

    public void setClickListener(){
        setClickListener((itemView, position, id) -> {
            if (selectedItemId == -1) {
                selectedItemId = id;
                selectedItemPosition = position;
            }
            else if (id == selectedItemId){
                selectedItemId = -1;
                selectedItemPosition = -1;
            }
            else if (selectedItemId != -1){
                int previousPosition = selectedItemPosition;
                selectedItemId = id;
                selectedItemPosition = position;
                notifyItemChanged(previousPosition);
            }
            notifyItemChanged(position);
        });
    }

    public void setClickListener(FriendlyLogViewHolder.OnClickListener clickListener) {
        mClickListener = clickListener;
    }
}