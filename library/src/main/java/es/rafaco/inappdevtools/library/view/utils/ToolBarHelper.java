package es.rafaco.inappdevtools.library.view.utils;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

//#ifdef ANDROIDX
//@import androidx.appcompat.widget.SearchView;
//@import androidx.appcompat.widget.Toolbar;
//@import static androidx.appcompat.R.*;
//#else
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import static android.support.v7.appcompat.R.*;
//#endif

import es.rafaco.inappdevtools.library.R;

public class ToolBarHelper {
    Context context;
    Toolbar toolbar;

    public ToolBarHelper(Toolbar toolbar) {
        this.context = toolbar.getContext();
        this.toolbar = toolbar;
    }

    public void initSearchFilterButtons(SearchView.OnQueryTextListener onQueryCallback) {
        initSearchMenuItem(R.id.action_search, "Search...");
        MenuItem filterItem = initSearchMenuItem(R.id.action_filter, "Filter...");

        SearchView filterView = (SearchView) filterItem.getActionView();
        if (filterView != null) {
            int searchImgId = id.search_button; // I used the explicit layout ID of searchview's ImageView
            ImageView v = filterView.findViewById(searchImgId);
            v.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_filter_list_white_24dp));
            filterView.setOnQueryTextListener(onQueryCallback);
        }
    }

    public MenuItem initSearchMenuItem(int menuActionId, String hint){
        final MenuItem item = toolbar.getMenu().findItem(menuActionId);
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
        item.setOnActionExpandListener(onActionExpandListener);

        final SearchView searchView = (SearchView) item.getActionView();
        if (searchView != null) {
            searchView.setQueryHint(hint);
        }
        return item;
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
