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

import android.arch.persistence.db.SupportSQLiteQuery;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
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
//@import androidx.lifecycle.Lifecycle;
//@import androidx.lifecycle.LifecycleObserver;
//@import androidx.lifecycle.OnLifecycleEvent;
//@import androidx.lifecycle.ProcessLifecycleOwner;
//@import androidx.paging.LivePagedListBuilder;
//@import androidx.paging.PagedList;
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
    private int pendingScrollPosition = -1;

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
        return R.menu.friendlylog;
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
        initFilterFromParams();
        initSelectionFromParams();
        initLiveData();
        initAdapter();
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
        if (pendingScrollPosition > -1)
            livePagedListBuilder.setInitialLoadKey(pendingScrollPosition);
        logList = livePagedListBuilder.build();

        removeDataObserver();
    }

    private void initAdapter(){
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                if (isDebug())
                    Log.v(Iadt.TAG, "LogScreen onItemRangeInserted("
                            + positionStart + ", " + itemCount + ")");

                if (pendingScrollPosition > -1){
                    if (pendingScrollPosition >= positionStart
                            && pendingScrollPosition <= positionStart + itemCount) {
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
            if (isDebug())
                Log.v(Iadt.TAG, "LogScreen observer OnChange (" + pagedList.size() + ")");

            //adapter.getCurrentList().getDataSource().invalidate();
            adapter.submitList(pagedList);
            //adapter.notifyDataSetChanged();
        }
    };

    private void observeData() {
        logList.observe(ProcessLifecycleOwner.get(), dataObserver);
        if (isDebug()) Log.v(Iadt.TAG, "LogScreen observer added");
    }

    private void removeDataObserver() {
        logList.removeObservers(ProcessLifecycleOwner.get());
        if (isDebug()) Log.v(Iadt.TAG, "LogScreen observer removed");
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

    //region [ SELECTED ITEM ]

    @Override
    public boolean isSelected(long id) {
        return selectedItemId != -1 && selectedItemId == id;
    }

    @Override
    public boolean isBeforeSelected(int position) {
        return selectedItemId != -1 && position + 1 == selectedItemPosition;
    }

    public void clearSelection() {
        selectedItemId = -1;
        selectedItemPosition = -1;
    }

    @Override
    public void onItemClick(View itemView, int position, long id) {
        int previousPosition = -1;
        int clickedPosition = position;
        long clickedId = id;

        boolean isSingleDeselection = (clickedId == selectedItemId);
        if (isSingleDeselection) {
            previousPosition = selectedItemPosition;
            clearSelection();
        }
        else {
            if (selectedItemId > -1) {
                previousPosition = selectedItemPosition;
            }
            selectedItemId = clickedId;
            selectedItemPosition = clickedPosition;
        }

        //Notify change at clicked and the item before
        adapter.notifyItemChanged(clickedPosition);
        if (clickedPosition>1) {
            adapter.notifyItemChanged(clickedPosition - 1);
        }

        //Notify change at previous and the item before
        if (!isSingleDeselection && previousPosition != -1) {
            adapter.notifyItemChanged(previousPosition);
            if (previousPosition > 1) {
                adapter.notifyItemChanged(previousPosition-1);
            }
        }
    }

    private void initSelectionFromParams() {
        InnerParams params = getParams();
        if (params!= null && params.selected > -1){
            long id = params.selected;
            int position = calculatePositionAtFilter();
            if (position > -1){
                savePendingScrollPosition(position);
                selectedItemId = id;
                selectedItemPosition = position;
            }
        }
    }

    private int calculatePositionAtFilter() {
        LogQueryHelper logQueryHelper = new LogQueryHelper(getFilter().getBackFilter());
        SupportSQLiteQuery positionQuery = logQueryHelper.getPositionQuery(getParams().selected);
        List<Friendly> positionResult = IadtController.getDatabase().friendlyDao().findPositionAtFilter(positionQuery);
        if (positionResult != null
                && positionResult.size() == 1){
            return ((int) positionResult.get(0).getDate()) - 1;
        }
        return - 1;
    }

    //endregion

    //region [ FILTER ]

    public void initFilterFromParams(){
        LogUiFilter filter = (getParams()!=null) ? getParams().filter : null;
        if (filter != null){
            LogFilterStore.store(filter);
        }
    }

    public LogFilterHelper getFilter(){
        LogUiFilter logUiFilter = LogFilterStore.get();
        if (logUiFilter == null){
            // Fallback to default filter
            return new LogFilterHelper(LogFilterHelper.Preset.EVENTS_INFO);
        }

        return new LogFilterHelper(logUiFilter);
    }

    public void updateFilter(LogFilterHelper uiFilter){
        LogFilterStore.store(uiFilter.getUiFilter());

        if(logList != null) {
            removeDataObserver();
        }

        //initLiveData();
        //initAdapter();

        //dataSourceFactory.setFilter(uiFilter.getBackFilter());
        initLiveData();
        observeData();
        initAdapter();
        /*adapter.notifyDataSetChanged();
        if (adapter.getCurrentList().getDataSource() != null )
            adapter.getCurrentList().getDataSource().invalidate();*/
        //recyclerView.invalidate();
        //recyclerView.requestLayout();
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
    public boolean onMenuItemClick(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_tune) {
            //OverlayService.performNavigation(AnalysisScreen.class);
            onTuneButton();
        }
        else if (selected == R.id.action_level) {
            onLevelButton();
        }
        else if (selected == R.id.action_save) {
            //onSaveButton();
        }
        else if (selected == R.id.action_delete) {
            onClearButton();
        }
        else{
            Iadt.showMessage("Not already implemented");
        }
        return super.onMenuItemClick(item);
    }

    private void onTuneButton() {
        final LogFilterHelper filter = getFilter();
        filterDialog = new LogFilterDialog(getContext(),adapter, filter);
        filterDialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                updateFilter(filter);
            }
        });
        filterDialog.getDialog().show();
    }

    private void onLevelButton() {
        String[] levelsArray = getContext().getResources().getStringArray(R.array.log_levels);
        final LogFilterHelper filter = getFilter();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext())
                .setTitle("Select log level")
                .setCancelable(true)
                .setSingleChoiceItems(levelsArray, filter.getUiFilter().getSeverityInt(), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != filter.getUiFilter().getSeverityInt()) {
                            filter.getUiFilter().setSeverityInt(which);
                            updateFilter(filter);
                        }
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(Layer.getLayoutType());
        alertDialog.show();
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

    private void savePendingScrollPosition(int positionAtFilter) {
        if (isDebug()){
            Log.d(Iadt.TAG, "Log scroll to position " + pendingScrollPosition
                    + " for id " +getParams().selected);
        }
        pendingScrollPosition = positionAtFilter;
    }

    private void scrollToPendingPosition() {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(pendingScrollPosition);
                savePendingScrollPosition(-1);
            }
        });
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
}
