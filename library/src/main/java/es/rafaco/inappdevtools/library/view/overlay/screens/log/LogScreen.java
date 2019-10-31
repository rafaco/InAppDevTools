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

package es.rafaco.inappdevtools.library.view.overlay.screens.log;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.appcompat.widget.SearchView;
//@import androidx.recyclerview.widget.RecyclerView;
//@import androidx.recyclerview.widget.LinearLayoutManager;
//@import androidx.lifecycle.LiveData;
//@import androidx.lifecycle.Observer;
//@import androidx.lifecycle.ProcessLifecycleOwner;
//@import androidx.paging.LivePagedListBuilder;
//@import androidx.paging.PagedList;
//@import androidx.sqlite.db.SupportSQLiteQuery;
//@import androidx.appcompat.view.ContextThemeWrapper;
//@import androidx.appcompat.widget.PopupMenu;
//#else
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
//#endif

import com.google.gson.Gson;

import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.log.datasource.LogDataSourceFactory;
import es.rafaco.inappdevtools.library.logic.log.datasource.LogQueryHelper;
import es.rafaco.inappdevtools.library.logic.log.filter.LogFilterDialog;
import es.rafaco.inappdevtools.library.logic.log.filter.LogFilterStore;
import es.rafaco.inappdevtools.library.logic.log.filter.LogUiFilter;
import es.rafaco.inappdevtools.library.logic.log.filter.LogFilterHelper;
import es.rafaco.inappdevtools.library.logic.log.reader.LogcatReaderService;
import es.rafaco.inappdevtools.library.logic.utils.ClipboardUtils;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.FriendlyDao;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;
import es.rafaco.inappdevtools.library.view.overlay.screens.ScreenHelper;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatHelper;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.ToolBarHelper;

public class LogScreen extends Screen implements LogViewHolder.Listener {

    private LogDataSourceFactory dataSourceFactory;
    private LiveData logList;
    private LogAdapter adapter;
    private RecyclerView recyclerView;
    private TextView welcome;
    private ToolBarHelper toolbarHelper;
    private LogFilterDialog filterDialog;

    private long selectedItemId = -1;
    private int selectedItemPosition = -1;
    private boolean pendingScrollToPosition;
    private LogFilterHelper filterHelper;

    public LogScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Logs";
    }

    @Override
    public boolean needNestedScroll() {
        return false;
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_log_body; }

    @Override
    public int getToolbarLayoutId() {
        return R.menu.screen_log;
    }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup toolHead) {
        initToolbar();

        welcome = bodyView.findViewById(R.id.welcome);
        welcome.setVisibility(View.GONE);
        recyclerView = getView().findViewById(R.id.list);

        adapter = new LogAdapter(this);
        initFilter();
        initSelected();
        initLiveData();
        initAdapter();

        if (LogFilterStore.get() == null){
            onProfilesButton();
        }
    }

    @Override
    protected void onResume() {
        observeData();
        requestData("Updated timer from onResume");
    }

    @Override
    protected void onPause() {
        removeDataObserver();
        requestData("Update timer onPause");
    }

    @Override
    protected void onStop() {
        if (filterDialog !=null && filterDialog.getDialog()!=null){
            filterDialog.getDialog().dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }

    //region [ DATA & ADAPTER ]

    private void initLiveData() {
        PagedList.Config myPagingConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(25 * 2)
                .build();

        FriendlyDao dao = DevToolsDatabase.getInstance().friendlyDao();
        dataSourceFactory = new LogDataSourceFactory(dao, getFilter().getBackFilter());

        LivePagedListBuilder livePagedListBuilder = new LivePagedListBuilder<>(dataSourceFactory, myPagingConfig);
        if (pendingScrollToPosition){
            if (isLogDebug()){
                Log.d(Iadt.TAG, "LogScreen - setInitialLoadKey to " + selectedItemPosition);
            }
            livePagedListBuilder.setInitialLoadKey(selectedItemPosition);
        }

        logList = livePagedListBuilder.build();
    }

    private void initAdapter(){
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                if (isLogDebug())
                    Log.v(Iadt.TAG, "LogScreen onItemRangeInserted("
                            + positionStart + ", " + itemCount + ")");

                if (pendingScrollToPosition){
                    if (selectedItemPosition >= positionStart
                            && selectedItemPosition <= positionStart + itemCount) {
                        scrollToPendingPosition();
                    }
                }
                else if (positionStart != 0 && !recyclerView.canScrollVertically(1)){
                    scrollToBottom();
                }
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void requestData(String s) {
        Intent intent = LogcatReaderService.getStartIntent(getContext(), s);
        LogcatReaderService.enqueueWork(getContext(), intent);
    }

    private Observer<PagedList<Friendly>> dataObserver = new Observer<PagedList<Friendly>>() {
        @Override
        public void onChanged(PagedList<Friendly> pagedList) {
            if (isLogDebug())
                Log.v(Iadt.TAG, "LogScreen observer OnChange (" + pagedList.size() + ")");

            //adapter.getCurrentList().getDataSource().invalidate();
            adapter.submitList(pagedList);
            //adapter.notifyDataSetChanged();
        }
    };

    private void observeData() {
        logList.observe(ProcessLifecycleOwner.get(), dataObserver);
        if (isLogDebug()) Log.v(Iadt.TAG, "LogScreen observer added");
    }

    private void removeDataObserver() {
        logList.removeObservers(ProcessLifecycleOwner.get());
        if (isLogDebug()) Log.v(Iadt.TAG, "LogScreen observer removed");
    }

    //endregion

    //region [ PARAMS]

    public static String buildParams(LogUiFilter filter){
        Gson gson = new Gson();
        return gson.toJson(new InnerParams(filter));
    }

    public static String buildParams(LogUiFilter filter, long selected){
        Gson gson = new Gson();
        return gson.toJson(new InnerParams(filter, selected));
    }

    public InnerParams getParams(){
        Gson gson = new Gson();
        return gson.fromJson(getParam(), InnerParams.class);
    }

    public static class InnerParams {
        LogUiFilter filter;
        long selected;

        public InnerParams(LogUiFilter filter) {
            this.filter = filter;
        }

        public InnerParams(LogUiFilter filter, long selected) {
            this(filter);
            this.selected = selected;
        }
    }

    //endregion

    //region [ FILTER ]

    public void initFilter(){
        if (getParams()!=null && getParams().filter != null){
            filterHelper = new LogFilterHelper(getParams().filter);
        }
        else if (LogFilterStore.get() != null){
            filterHelper = new LogFilterHelper(LogFilterStore.get());
        }
        else{
            filterHelper = new LogFilterHelper(LogFilterHelper.Preset.REPRO_STEPS);
        }
    }

    public LogFilterHelper getFilter(){
        return filterHelper;
    }

    public void updateFilter(LogFilterHelper newFilter){
        if (newFilter.getUiFilter().equals(filterHelper.getUiFilter())){
            if (isDebug()) Log.w(Iadt.TAG, "LogScreen updateFilter without filter changed");
        }
        if (isLogDebug()) Log.v(Iadt.TAG, "LogScreen updateFilter");
        if(logList != null) removeDataObserver();
        LogFilterStore.store(newFilter.getUiFilter());
        filterHelper = new LogFilterHelper(newFilter.getUiFilter());
        //dataSourceFactory.setFilter(uiFilter.getBackFilter());
        updateSelectedById(selectedItemId);
        recyclerView.invalidate();
        recyclerView.requestLayout();

        initLiveData();
        observeData();
        initAdapter();

        ThreadUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                scrollToPendingPosition();
            }
        }, 500);

        /*adapter.notifyDataSetChanged();
        if (adapter.getCurrentList().getDataSource() != null )
            adapter.getCurrentList().getDataSource().invalidate();*/
        //recyclerView.invalidate();
        //recyclerView.requestLayout();
    }

    //endregion

    //region [ SELECTED ITEM ]

    private void initSelected() {
        InnerParams params = getParams();
        if (params!= null && params.selected > -1){
            long id = params.selected;
            if (updateSelectedById(id))
                return;
        }
        clearSelected();
    }

    private boolean updateSelectedById(long id) {
        if (id > -1){
            if (isLogDebug()){
                Log.d(Iadt.TAG, "LogScreen - updateSelectedById started");
            }
            int position = calculatePositionAtFilter(id);
            if (position > -1){
                if (isLogDebug()){
                    Log.d(Iadt.TAG, "LogScreen - updateSelectedById for "
                            + id + " at " + position);
                }
                savePendingScrollPosition(true);
                setSelected(id, position);
                return true;
            }
            if (isLogDebug()){
                Log.d(Iadt.TAG, "LogScreen - updateSelectedById not found for " + id);
            }
        }
        clearSelected();
        return false;
    }

    private void setSelected(long id, int position) {
        selectedItemId = id;
        selectedItemPosition = position;
    }

    public void clearSelected() {
        if (isLogDebug()) {
            Log.d(Iadt.TAG, "LogScreen - cleared selection");
        }
        setSelected(-1, -1);
    }

    private int calculatePositionAtFilter(long id) {
        LogQueryHelper logQueryHelper = new LogQueryHelper(getFilter().getBackFilter());
        SupportSQLiteQuery positionQuery = logQueryHelper.getPositionQuery(id);
        List<Friendly> positionResult = IadtController.getDatabase().friendlyDao().findPositionAtFilter(positionQuery);
        if (positionResult != null
                && positionResult.size() == 1){
            return (int) positionResult.get(0).getDate();
        }
        return - 1;
    }

    @Override
    public boolean isSelected(long id) {
        return selectedItemId != -1 && selectedItemId == id;
    }

    @Override
    public boolean isBeforeSelected(int position) {
        return selectedItemId != -1 && position + 1 == selectedItemPosition;
    }

    @Override
    public void onItemClick(View itemView, final int clickedPosition, long clickedId) {
        int previousPosition = -1;

        boolean isDeselectingSelected = (clickedId == selectedItemId);
        if (isDeselectingSelected) {
            if (isLogDebug()) {
                Log.d(Iadt.TAG, "LogScreen - deselected " + clickedId + " at " + clickedPosition);
            }
            previousPosition = selectedItemPosition;
            clearSelected();
        }
        else {
            if (isLogDebug()) {
                Log.d(Iadt.TAG, "LogScreen - selected " + clickedId + " at " + clickedPosition);
            }
            if (selectedItemId > -1) {
                previousPosition = selectedItemPosition;
            }
            setSelected(clickedId, clickedPosition);
            savePendingScrollPosition(false);
        }

        //Notify previously selected and the item before
        if (!isDeselectingSelected && previousPosition != -1) {
            adapter.notifyItemChanged(previousPosition);
            if (previousPosition > 1) {
                adapter.notifyItemChanged(previousPosition-1);
            }
        }

        //Notify to clicked and the item before
        adapter.notifyItemChanged(clickedPosition);
        if (clickedPosition>1) {
            adapter.notifyItemChanged(clickedPosition - 1);
        }

        //Scroll to clicked position
        recyclerView.post(new Runnable() {
                @Override
                public void run() {
                recyclerView.scrollToPosition(clickedPosition);
            }
        });
    }

    //endregion
    
    //region [ ITEM OVERFLOW MENU ]

    @Override
    public void onOverflowClick(View itemView, int position, long id) {
        showPopupMenu(itemView, position, id);
    }

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
            Friendly data = adapter.getCurrentList().get(position);

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

    //region [ TOOL BAR ]

    private void initToolbar() {
        toolbarHelper = new ToolBarHelper(getToolbar());
        toolbarHelper.initSearchFilterButtons(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                dataSourceFactory.getFilter().setText(newText);
                adapter.getCurrentList().getDataSource().invalidate();
                return false;
            }
        });
        toolbarHelper.showAllMenuItem();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            int itemId = item.getItemId();
            if (itemId == R.id.action_level){
                menu.getItem(i).setVisible(false);
                menu.getItem(i).setEnabled(false);
            }
        }
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int selected = item.getItemId();
        //OverlayService.performNavigation(AnalysisScreen.class);

        if (selected == R.id.action_profile) {
            onProfilesButton();
        }
        else if (selected == R.id.action_tune) {
            onTuneButton();
        }
        else if (selected == R.id.action_wrap_lines) {
            onWrapLinesButton();
        }
        else if (selected == R.id.action_save) {
            //onSaveButton();
            Iadt.showMessage("Currently disabled, sorry");
        }
        else if (selected == R.id.action_delete) {
            onClearButton();
        }
        else{
            Iadt.showMessage("Not already implemented");
        }
        return super.onMenuItemClick(item);
    }

    private void onProfilesButton() {
        LogUiFilter clonedUiFilter = getFilter().getUiFilter().clone();
        final LogFilterHelper tempFilter = new LogFilterHelper(clonedUiFilter);
        filterDialog = new LogFilterDialog(getContext(), tempFilter, new Runnable() {
            @Override
            public void run() {
                updateFilter(tempFilter);
            }
        });
        filterDialog.showStandardDialog();
    }

    private void onTuneButton() {
        LogUiFilter clonedUiFilter = getFilter().getUiFilter().clone();
        final LogFilterHelper tempFilter = new LogFilterHelper(clonedUiFilter);
        filterDialog = new LogFilterDialog(getContext(), tempFilter, new Runnable() {
            @Override
            public void run() {
                updateFilter(tempFilter);
            }
        });
        filterDialog.showAdvancedDialog();
    }

    private void onWrapLinesButton() {
        LogUiFilter uiFilter = getFilter().getUiFilter();
        uiFilter.setWrapLines(!uiFilter.isWrapLines());
        updateFilter(getFilter());
    }

    @Override
    public boolean isWrapLines() {
        return filterHelper.getUiFilter().isWrapLines();
    }

    private void onSaveButton() {
        ScreenHelper helper = new LogcatHelper();
        String path = (String) helper.getReportPath();
        Iadt.showMessage("Log stored to " + path);
    }

    private void onClearButton() {
        final boolean[] checked = { false };
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext())
                .setTitle("Delete all log in DB?")
                //.setMessage("Do you really want to wipe the db with the log history? You will not be able to see it or use it in your reports")
                .setCancelable(true)
                .setMultiChoiceItems(new String[]{"Clear also logcat buffer"}, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        checked[0] = b;
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearAll(checked[0]);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(Layer.getLayoutType());
        alertDialog.show();
    }

    private void clearAll(boolean clearLogcatBuffer) {
        if (clearLogcatBuffer){
            IadtController.cleanSession();
        }
        IadtController.get().getDatabase().friendlyDao().deleteAll();
        FriendlyLog.log("D", "Iadt", "Delete","Friendly log history deleted by user");

        if(logList != null) {
            removeDataObserver();
        }
        observeData();
    }

    //endregion

    //region [ SCROLL ]

    private void savePendingScrollPosition(boolean value) {
        pendingScrollToPosition = value;
    }

    private void scrollToPendingPosition() {
        if (selectedItemPosition != -1){
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (adapter.getCurrentList().size() > selectedItemPosition){
                        recyclerView.smoothScrollToPosition(selectedItemPosition);
                        savePendingScrollPosition(false);
                    }
                }
            });
        }
    }

    private void scrollToBottom() {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(adapter.getItemCount()-1);
            }
        });
    }

    //endregion


    //TODO: relocate to a better place... DebugMode by categories?
    // Used by Screen, ReaderService, QueryHelper, LogViewHolder...
    // WARNING: enabling it produce too much log noise
    public static boolean isLogDebug() {
        boolean isLogDebug = false;
        return IadtController.get().isDebug() && isLogDebug;
    }
}
