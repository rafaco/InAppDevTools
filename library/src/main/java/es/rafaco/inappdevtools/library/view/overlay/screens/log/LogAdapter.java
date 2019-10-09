package es.rafaco.inappdevtools.library.view.overlay.screens.log;

import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//@import androidx.paging.PagedListAdapter;
//@import androidx.recyclerview.widget.DiffUtil;
//#else
import android.support.annotation.NonNull;
import android.arch.paging.PagedListAdapter;
import  android.support.v7.util.DiffUtil;
//#endif

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.ClipboardUtils;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class LogAdapter
        extends PagedListAdapter<Friendly, LogViewHolder> {

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


    protected LogAdapter() {
        super(DIFF_CALLBACK);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getDate();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tool_log_item, viewGroup, false);
        LogViewHolder logViewHolder = new LogViewHolder(itemView, itemClickListener);

        return logViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder,
                                 int position) {
        Friendly item = getItem(position);
        if (item != null) {
            holder.bindTo(item,
                    selectedItemId != -1 && selectedItemId == item.getUid(),
                    selectedItemPosition - 1 == position);
        } else {
            holder.showPlaceholder(position);
        }
    }


    public interface OnLogClickListener {
        void onItemClick(View itemView, int position, long id);
        void onOverflowClick(View itemView, int position, long id);
    }

    private OnLogClickListener itemClickListener = new OnLogClickListener() {
        @Override
        public void onItemClick(View itemView, int position, long id) {
            updatedExpandedItems(position, id);
        }

        @Override
        public void onOverflowClick(View itemView, int position, long id) {
            showPopupMenu(itemView, position, id);
        }
    };

    private void updatedExpandedItems(int clickedPosition, long clickedId) {
        int previousPosition = -1;

        boolean isDeselection = (clickedId == selectedItemId);
        if (isDeselection) {
            previousPosition = selectedItemPosition;
            selectedItemId = -1;
            selectedItemPosition = -1;
        }
        else {
            if (selectedItemId > -1) {
                previousPosition = selectedItemPosition;
            }
            selectedItemId = clickedId;
            selectedItemPosition = clickedPosition;
        }

        notifyItemChanged(clickedPosition);
        if (clickedPosition>1) {
            notifyItemChanged(clickedPosition - 1);
        }

        if (!isDeselection && previousPosition != -1) {
            notifyItemChanged(previousPosition);
            if (previousPosition > 1) {
                notifyItemChanged(previousPosition-1);
            }
        }
    }

    //region [ ACTIONS POP UP ]

    private void showPopupMenu(View view, int position, long id) {
        Context wrapper = new ContextThemeWrapper(view.getContext(), R.style.LibPopupMenuStyle);
        PopupMenu popup = new PopupMenu(wrapper, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.log_item, popup.getMenu());
        popup.setOnMenuItemClickListener(new OverflowClickListener(position, id));
        popup.show();
    }

    class OverflowClickListener implements PopupMenu.OnMenuItemClickListener {

        private final int position;
        private final long id;

        public OverflowClickListener(int position, long id) {
            this.position = position;
            this.id = id;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int action = menuItem.getItemId();
            Friendly data = getItem(position);

            if (action == R.id.action_search) {
                Iadt.showMessage("Search log on internet");
                ExternalIntentUtils.search(data.getMessage());
                return true;
            }
            else if (action == R.id.action_include) {
                Iadt.showMessage("Include");
                return true;
            }
            else if (action == R.id.action_exclude) {
                Iadt.showMessage("exclude");
                return true;
            }
            else if (action == R.id.action_share) {
                Iadt.showMessage("Sharing log overview");
                String textToShare = data.getMessage() + Humanizer.fullStop()
                        + LogViewHolder.getFormattedDetails(data);
                ExternalIntentUtils.shareText(textToShare);
                return true;
            }
            else if (action == R.id.action_copy) {
                Iadt.showMessage("Log message copied to clipboard");
                ClipboardUtils.save(IadtController.get().getContext(), data.getMessage());
                return true;
            }
            return false;
        }
    }

    //endregion
}
