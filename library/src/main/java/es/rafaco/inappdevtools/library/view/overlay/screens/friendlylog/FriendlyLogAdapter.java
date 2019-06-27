package es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//#ifdef MODERN
//@import androidx.annotation.NonNull;
//@import androidx.paging.PagedListAdapter;
//@import androidx.recyclerview.widget.DiffUtil;
//#else
import android.support.annotation.NonNull;
import android.arch.paging.PagedListAdapter;
import  android.support.v7.util.DiffUtil;
//#endif

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;

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
                    return oldItem.equalContent(newItem);
                }
            };

    private static long selectedItemId = -1;
    private static int selectedItemPosition = -1;
    private OnClickListener mClickListener;

    protected FriendlyLogAdapter() {
        super(DIFF_CALLBACK);

        setClickListener();
    }

    @NonNull
    @Override
    public FriendlyLogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tool_friendlylog_item, viewGroup, false);
        FriendlyLogViewHolder friendlyLogViewHolder = new FriendlyLogViewHolder(itemView, mClickListener);

        return friendlyLogViewHolder;
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
        setClickListener(new OnClickListener() {
            @Override
            public void onClick(View itemView, int position, long id) {
                if (selectedItemId == -1) {
                    selectedItemId = id;
                    selectedItemPosition = position;
                } else if (id == selectedItemId) {
                    selectedItemId = -1;
                    selectedItemPosition = -1;
                } else if (selectedItemId != -1) {
                    int previousPosition = selectedItemPosition;
                    selectedItemId = id;
                    selectedItemPosition = position;
                    FriendlyLogAdapter.this.notifyItemChanged(previousPosition);
                }
                FriendlyLogAdapter.this.notifyItemChanged(position);
            }
        });
    }

    public void setClickListener(OnClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface OnClickListener {
        void onClick(View itemView, int position, long id);
    }
}
