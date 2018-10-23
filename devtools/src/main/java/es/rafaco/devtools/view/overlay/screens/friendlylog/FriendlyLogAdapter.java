package es.rafaco.devtools.view.overlay.screens.friendlylog;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.utils.FriendlyLog;
import es.rafaco.devtools.storage.db.entities.Friendly;

public class FriendlyLogAdapter
        extends PagedListAdapter<Friendly, FriendlyLogAdapter.FriendlyLogViewHolder> {

    protected FriendlyLogAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public FriendlyLogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.decorated_tool_info_item, viewGroup, false);

        return new FriendlyLogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendlyLogViewHolder holder,
                                 int position) {
        Friendly item = getItem(position);
        if (item != null) {
            Log.d("Friendly", "bindTo()" + position + ":" + item.getUid());
            holder.bindTo(item);
        } else {
            // Null defines a placeholder item - PagedListAdapter automatically
            // invalidates this row when the actual object is loaded from the
            // database.
            Log.d("Friendly", "clear()" + position);
            holder.clear();
        }
    }

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

    public static class FriendlyLogViewHolder extends RecyclerView.ViewHolder {
        ImageView headIcon;
        View decorator;
        TextView title, message;
        ImageView icon;
        Switch switchButton;

        public FriendlyLogViewHolder(View view) {
            super(view);
            decorator = view.findViewById(R.id.decorator);
            title = view.findViewById(R.id.title);
            message = view.findViewById(R.id.message);
            headIcon = view.findViewById(R.id.head_icon);
            icon = view.findViewById(R.id.icon);
            switchButton = view.findViewById(R.id.switch_button);
        }

        public void bindTo(Friendly data) {
            int icon = FriendlyLog.getIcon(data);
            int color = FriendlyLog.getColor(data);

            title.setVisibility(View.GONE);

            message.setVisibility(View.VISIBLE);
            message.setText(data.getUid() + ":" + data.getMessage());

            int contextualizedColor = ContextCompat.getColor(itemView.getContext(), color);
            title.setTextColor(contextualizedColor);
            decorator.setBackgroundColor(contextualizedColor);


            if (icon != -1){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    headIcon.setImageDrawable(itemView.getContext().getDrawable(icon));
                    headIcon.setColorFilter(contextualizedColor);
                } else {
                    headIcon.setImageDrawable(itemView.getContext().getResources().getDrawable(icon));
                }
                headIcon.setVisibility(View.VISIBLE);
            }else{
                headIcon.setVisibility(View.GONE);
            }
        }

        public void clear() {

        }
    }
}