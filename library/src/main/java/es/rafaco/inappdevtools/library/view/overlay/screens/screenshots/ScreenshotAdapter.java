package es.rafaco.inappdevtools.library.view.overlay.screens.screenshots;

import android.content.Context;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//@import androidx.recyclerview.widget.DiffUtil;
//@import androidx.cardview.widget.CardView;
//@import androidx.appcompat.view.ContextThemeWrapper;
//@import androidx.appcompat.widget.PopupMenu;
//#else
import android.support.v7.widget.RecyclerView;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
//#endif

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.logic.integrations.CustomToast;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.ImageLoaderAsyncTask;

public class ScreenshotAdapter extends RecyclerView.Adapter<ScreenshotAdapter.ScreenViewHolder> {

    private Context mContext;
    private List<Screenshot> screenshotList;
    private RecyclerView recycledView;

    public ScreenshotAdapter(Context mContext, List<Screenshot> screenshotList) {
        this.mContext = mContext;
        this.screenshotList = screenshotList;
    }

    @Override
    public int getItemCount() {
        return screenshotList.size();
    }

    //region [ VIEW HOLDER ]

    @Override
    public ScreenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tool_screenshots_item, parent, false);

        return new ScreenViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ScreenViewHolder holder, int position) {
        holder.cardView.setRadius(R.dimen.card_radius);

        Screenshot screenshot = screenshotList.get(position);
        holder.title.setText(screenshot.getActivityName());
        holder.count.setText(Humanizer.getElapsedTime(screenshot.getDate()));

        // loading album cover using Glide library
        //Glide.with(mContext).load(screenshot.getThumbnail()).into(holder.thumbnail);
        //ImageLoader.loadInto(screenshot.getPath(), holder.thumbnail);
        new ImageLoaderAsyncTask(holder.thumbnail).execute(screenshot.getPath());

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    public class ScreenViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public ScreenViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.card_view);
            title = view.findViewById(R.id.title);
            count = view.findViewById(R.id.subtitle);
            thumbnail = view.findViewById(R.id.thumbnail);
            overflow = view.findViewById(R.id.overflow);
        }
    }

    //endregion

    //region [ SET DATA ]

    public void setData(List<Screenshot> newData) {
        if (screenshotList != null) {
            ScreenDiffCallback postDiffCallback = new ScreenDiffCallback(screenshotList, newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            screenshotList.clear();
            screenshotList.addAll(newData);
            diffResult.dispatchUpdatesTo(this);
        }
    }

    class ScreenDiffCallback extends DiffUtil.Callback {

        private final List<Screenshot> oldPosts, newPosts;

        public ScreenDiffCallback(List<Screenshot> oldPosts, List<Screenshot> newPosts) {
            this.oldPosts = oldPosts;
            this.newPosts = newPosts;
        }

        @Override
        public int getOldListSize() {
            return oldPosts.size();
        }

        @Override
        public int getNewListSize() {
            return newPosts.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).getUid() == newPosts.get(newItemPosition).getUid();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }

    //endregion

    //region [ ACTIONS POP UP ]

    private void showPopupMenu(View view) {

        Context wrapper = new ContextThemeWrapper(mContext, R.style.LibPopupMenuStyle);
        PopupMenu popup = new PopupMenu(wrapper, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.screenshots, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int i = menuItem.getItemId();
            if (i == R.id.action_preview) {
                Iadt.showMessage("Preview");
                return true;
            } else if (i == R.id.action_open) {
                CustomToast.show(recycledView.getContext(), "Open", CustomToast.TYPE_WARNING);

                return true;
            } else if (i == R.id.action_delete) {
                Iadt.showMessage("Delete");
                return true;
            }
            return false;
        }
    }

    //endregion

    public void setRecycledView(RecyclerView view){
        recycledView = view;
    }
}
