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

package es.rafaco.inappdevtools.library.view.overlay.screens.sources;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//@import androidx.appcompat.widget.SearchView;
//#else
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
//#endif

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
import es.rafaco.inappdevtools.library.view.components.items.LinkItem;
import es.rafaco.inappdevtools.library.logic.sources.SourceEntry;
import es.rafaco.inappdevtools.library.logic.utils.ClipboardUtils;
import es.rafaco.inappdevtools.library.view.components.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.utils.ToolbarSearchHelper;
import es.rafaco.inappdevtools.library.view.utils.PathUtils;

public class SourcesScreen extends Screen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;
    private ToolbarSearchHelper toolbarSearch;
    private String contentOverview;
    private AsyncTask<Object, String, List<Object>> currentTask;

    public SourcesScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Sources";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.flexible_container; }

    @Override
    public int getToolbarLayoutId() {
        return R.menu.sources;
    }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup view) {
        List<Object> data = initData();
        initAdapter(data);
    }

    @Override
    protected void onStop() {
        //Nothing needed
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }

    public static String buildParams(String origin, String path){
        SourceEntry paramObject = new SourceEntry(origin, path, true);
        Gson gson = new Gson();
        return gson.toJson(paramObject);
    }

    public SourceEntry getParams(){
        Gson gson = new Gson();
        return gson.fromJson(getParam(), SourceEntry.class);
    }

    //region [ DATA AND ADAPTER ]

    private List<Object> initData() {
        SourceEntry params = getParams();
        return getDataByEntry(params);
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexibleAdapter(FlexibleAdapter.Layout.GRID, 1, data);
        recyclerView = bodyView.findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
    }

    private List<Object> getDataByEntry(SourceEntry filter) {
        String path = (filter!=null) ? filter.getName() : null;
        List<SourceEntry> filteredItems = IadtController.get().getSourcesManager().getChildItems(path);

        List<Object> data= new ArrayList<>();
        addRootAndUp(filter, data);
        addSourceEntries(filter, data, filteredItems);

        buildContentOverview(filter, filteredItems);
        return data;
    }

    private List<Object> getDataBySearch(String filter) {
        List<Object> data= new ArrayList<>();
        List<SourceEntry> filteredItems = IadtController.get().getSourcesManager().getSearchItems(filter);
        if (filter.length()< 2) {
            addShortSearchResult(data);
            return data;
        }
        else if (filteredItems.size()==0){
            addEmptySearchResult(data);
            return data;
        }
        addSourceEntries(null, data, filteredItems);
        buildContentOverview(filter, filteredItems);
        return data;
    }

    private void updateFilter(SourceEntry entry) {
        if (entry == null)
            updateParams(null);
        else
            updateParams(buildParams("TODO", entry.getName()));

        if (currentTask != null){
            currentTask.cancel(true);
        }

        currentTask = new AsyncTask<Object, String, List<Object>>(){

            @Override
            protected List<Object> doInBackground(Object... objects) {
                SourceEntry entry = (SourceEntry)objects[0];
                List<Object> filteredItems = getDataByEntry(entry);
                return filteredItems;
            }

            @Override
            protected void onPostExecute(final List<Object> filteredItems) {
                super.onPostExecute(filteredItems);
                if (!currentTask.isCancelled()){
                    adapter.replaceItems(filteredItems);
                }
                currentTask = null;
            }
        };
        currentTask.execute(entry);
    }

    private void updateSearch(String text) {
        if (text == null) {
            updateFilter(getParams());
            return;
        }

        if (currentTask != null){
            currentTask.cancel(true);
        }

        currentTask = new AsyncTask<Object, String, List<Object>>(){
            @Override
            protected List<Object> doInBackground(Object... objects) {
                String text = (String)objects[0];
                List<Object> filteredItems = getDataBySearch(text);
                return filteredItems;
            }

            @Override
            protected void onPostExecute(final List<Object> filteredItems) {
                super.onPostExecute(filteredItems);
                if (!currentTask.isCancelled()){
                    adapter.replaceItems(filteredItems);
                }
                currentTask = null;
            }
        };
        currentTask.execute(text);
    }

    //endregion

    //region [ FLEXIBLE ITEMS ]

    private List<Object> addShortSearchResult(List<Object> data) {
        data.add(new LinkItem(
                "Type 2 characters at least...",
                R.string.gmd_pause,
                R.color.rally_white,
                new Runnable() {
                    @Override
                    public void run() {
                        //TODO: close search view
                    }
                }
        ));
        return data;
    }

    private List<Object> addEmptySearchResult(List<Object> data) {
        data.add(new LinkItem(
                "No results found",
                R.string.gmd_stop,
                R.color.rally_white,
                new Runnable() {
                    @Override
                    public void run() {
                        //TODO: close search view
                    }
                }
        ));
        return data;
    }

    private List<Object> addRootAndUp(SourceEntry filter, List<Object> data) {

        if (filter != null) { // Not root

            data.add(new LinkItem(
                    "Go to root",
                    R.string.gmd_home,
                    R.color.rally_white,
                    new Runnable() {
                        @Override
                        public void run() {
                            SourcesScreen.this.goRoot();
                        }
                    }
            ));

            if (filter.getDeepLevel() != 0){
                data.add(new LinkItem(
                        "Up from " + PathUtils.removeLastSlash(filter.getName()),
                        R.string.gmd_arrow_upward,
                        R.color.rally_white,
                        new Runnable() {
                            @Override
                            public void run() {
                                SourcesScreen.this.goUp();
                            }
                        }
                ));
            }
        }
        return data;
    }

    private List<Object> addSourceEntries(SourceEntry filter, List<Object> data, List<SourceEntry> filteredItems) {

        for (final SourceEntry entry : filteredItems) {
            String label = getLinkName(entry);
            if (filter!=null){
                label = label.replace(filter.getName(), "");
            }

            data.add(new LinkItem(label,
                    entry.isDirectory() ? R.string.gmd_folder_filled : R.string.gmd_insert_drive_file,
                    entry.isDirectory() ? R.color.rally_yellow : R.color.rally_blue_med,
                    entry.isDirectory() ? new Runnable() {
                        @Override
                        public void run() {
                            SourcesScreen.this.updateFilter(entry);
                        }
                    } : new Runnable() {
                        @Override
                        public void run() {
                            //Review this following line
                            toolbarSearch.getSearchView().setIconified(true);
                            SourcesScreen.this.openSource(entry);
                        }
                    }));
        }

        return data;
    }

    //endregion

    //region [ TOOL BAR ]

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbarSearch = new ToolbarSearchHelper(getToolbar(), R.id.action_search);
        toolbarSearch.setHint("Search file names...");
        toolbarSearch.getSearchView().setIconifiedByDefault(false);
        toolbarSearch.setOnChangeListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //codeViewer.findNext(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!query.isEmpty()) {
                    updateSearch(query);
                } else {
                    updateSearch(null);
                }
                return true;
            }
        });
        toolbarSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                updateSearch(null);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_share) {
            ExternalIntentUtils.shareText(getContentOverview());
        }
        else if (selected == R.id.action_copy) {
            ClipboardUtils.save(getContext(), getContentOverview());
            Iadt.showMessage("Content overview copied to clipboard");
        }
        return super.onMenuItemClick(item);
    }

    //endregion

    private void openSource(SourceEntry entry) {
        String params = SourceDetailScreen.buildSourceParams(entry.getName());
        OverlayService.performNavigation(SourceDetailScreen.class, params);
    }

    private void goRoot() {
        updateFilter(null);
    }

    private void goUp() {
        String upName = getSubtitle();
        updateFilter(new SourceEntry("TODO", upName, true));
    }

    private String getSubtitle() {
        if (getParams()==null || TextUtils.isEmpty(getParams().getName())){
            return "";
        }
        String currentName = PathUtils.removeLastSlash(getParams().getName());
        return currentName.substring(0, currentName.lastIndexOf('/')+1);
    }

    private String getLinkName(SourceEntry entry) {
        if (TextUtils.isEmpty(entry.getName())){
            return entry.getOrigin();
        }
        return entry.isDirectory() ? PathUtils.removeLastSlash(entry.getName())
                : entry.getFileName();
    }

    private void buildContentOverview(Object filter, List<SourceEntry> filteredItems) {
        String result ="";
        if (filter instanceof String){
            result += "Sources searching for " + filter + ":\n";
        }
        else { //(filter instanceof String)
            SourceEntry entry = (SourceEntry) filter;
            String origin = (filter == null) ? "root folder" : PathUtils.getLastLevelName(entry.getName());
            result += "Sources filtered at " + origin;
            if (filter != null)
                result += " (" + entry.getName() + ")";
            result +=  ":\n";
        }
        for (SourceEntry item : filteredItems){
            result += " - " + (item.isDirectory() ? "Folder " : "File " ) + PathUtils.getLastLevelName(item.getName());
            result += " (" + item.getName() + ")\n";
        }
        contentOverview = result;
    }

    private String getContentOverview() {
        return contentOverview;
    }
}
