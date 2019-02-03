package es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog;

import android.content.Context;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import es.rafaco.inappdevtools.library.R;

public class ToolBarHelper {
    Context context;
    Toolbar toolbar;

    public ToolBarHelper(Toolbar toolbar) {
        this.context = toolbar.getContext();
        this.toolbar = toolbar;
    }

    public void initSearchButtons(SearchView.OnQueryTextListener onQueryCallback) {
        final MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        final MenuItem filterItem = toolbar.getMenu().findItem(R.id.action_filter);
        if (searchItem != null && filterItem != null) {
            MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    hideOthersMenuItem(item);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    showAllMenuItem();
                    return true;
                }
            };
            searchItem.setOnActionExpandListener(onActionExpandListener);
            filterItem.setOnActionExpandListener(onActionExpandListener);

            final SearchView searchView = (SearchView) searchItem.getActionView();
            final SearchView filterView = (SearchView) filterItem.getActionView();
            if (searchView != null && filterView != null) {

                searchView.setQueryHint("Search...");
                filterView.setQueryHint("Filter...");
                int searchImgId = androidx.appcompat.R.id.search_button; // I used the explicit layout ID of searchview's ImageView
                ImageView v = filterView.findViewById(searchImgId);
                v.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_filter_list_rally_24dp));
                filterView.setOnQueryTextListener(onQueryCallback);
            }
        }
    }

    public void showAllMenuItem() {
        Menu menu = toolbar.getMenu();
        for(int i = 0; i<menu.size(); i++ ){
            MenuItem current = menu.getItem(i);
            current.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }
    }

    public void hideOthersMenuItem(MenuItem filterItem) {
        Menu menu = toolbar.getMenu();
        for(int i = 0; i<menu.size(); i++ ){
            MenuItem current = menu.getItem(i);
            if(current.getItemId() != filterItem.getItemId()){
                current.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }
        }
    }

}
