/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.inappdevtools.library.view.utils;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

//#ifdef ANDROIDX
//@import androidx.appcompat.widget.SearchView;
//@import androidx.appcompat.widget.Toolbar;
//@import androidx.annotation.DrawableRes;
//@import androidx.annotation.IdRes;
//@import static androidx.appcompat.R.*;
//#else
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import static android.support.v7.appcompat.R.*;
//#endif


public class ToolbarSearchHelper {
    Context context;
    Toolbar toolbar;
    MenuItem searchMenuItem;
    SearchView searchView;

    public ToolbarSearchHelper(Toolbar toolbar, @IdRes int menuItemId) {
        this.context = toolbar.getContext();
        this.toolbar = toolbar;
        this.searchMenuItem = toolbar.getMenu().findItem(menuItemId);
        this.searchView = (SearchView) searchMenuItem.getActionView();
        init();
    }

    private void init() {
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
        searchMenuItem.setOnActionExpandListener(onActionExpandListener);
        showAllMenuItem();
    }

    private void showAllMenuItem() {
        Menu menu = toolbar.getMenu();
        for(int i = 0; i<menu.size(); i++ ){
            MenuItem current = menu.getItem(i);
            current.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }
    }

    private void hideOthersMenuItem(MenuItem filterItem) {
        Menu menu = toolbar.getMenu();
        for(int i = 0; i<menu.size(); i++ ){
            MenuItem current = menu.getItem(i);
            if(current.getItemId() != filterItem.getItemId()){
                current.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }
        }
    }


    public void setHint(String hint){
        if (searchView != null) {
            searchView.setQueryHint(hint);
        }
    }

    public void setSubmitButtonEnabled(boolean enabled){
        if (searchView != null) {
            searchView.setSubmitButtonEnabled(enabled);
        }
    }

    public void setOnChangeListener(SearchView.OnQueryTextListener onChange){
        if (searchView != null) {
            searchView.setOnQueryTextListener(onChange);
        }
    }

    public void setOnCloseListener(SearchView.OnCloseListener onClose) {
        if (searchView != null) {
            searchView.setOnCloseListener(onClose);
        }
    }

    public void replaceActionViewIcon(@DrawableRes int iconResource){
        //Use an iconResource directly like R.drawable.ic_filter_list_white_24dp
        if (searchView != null) {
            ImageView v = searchView.findViewById(id.search_button);
            v.setImageDrawable(context.getResources().getDrawable(iconResource));
        }
    }

    public SearchView getSearchView(){
        return searchView;
    }
}
