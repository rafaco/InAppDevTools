/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.view.overlay.screens.network;

import android.content.DialogInterface;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.lifecycle.LiveData;
//@import androidx.lifecycle.Observer;
//@import androidx.lifecycle.ProcessLifecycleOwner;
//@import androidx.paging.LivePagedListBuilder;
//@import androidx.paging.PagedList;
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.appcompat.widget.SearchView;
//@import androidx.recyclerview.widget.RecyclerView;
//@import androidx.recyclerview.widget.LinearLayoutManager;
//#else
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
//#endif

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.external.chuck.HttpBinService;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.NetSummary;
import es.rafaco.inappdevtools.library.storage.db.entities.NetSummaryDao;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.utils.ToolbarSearchHelper;

public class NetScreen extends Screen implements NetViewHolder.Listener {

    private NetDataSourceFactory dataSourceFactory;
    private LiveData liveData;
    private NetAdapter adapter;
    private RecyclerView recyclerView;
    private ToolbarSearchHelper toolbarSearch;
    private long filter;

    public NetScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Network";
    }

    @Override
    public boolean needNestedScroll() {
        return false;
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_live_data_body; }

    @Override
    public int getToolbarLayoutId() {
        return R.menu.screen_net;
    }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup toolHead) {

        recyclerView = getView().findViewById(R.id.list);
        adapter = new NetAdapter(this);

        initToolbar();
        initFilter();
        initLiveData();
        initAdapter();
    }

    @Override
    protected void onResume() {
        observeData();
    }

    @Override
    protected void onPause() {
        removeDataObserver();
    }

    @Override
    protected void onStop() {
        //Nothing needed
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

        NetSummaryDao dao = DevToolsDatabase.getInstance().netSummaryDao();
        dataSourceFactory = new NetDataSourceFactory(dao, filter);

        LivePagedListBuilder livePagedListBuilder = new LivePagedListBuilder<>(dataSourceFactory, myPagingConfig);
        liveData = livePagedListBuilder.build();
    }

    private void initAdapter(){
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                if (positionStart != 0 && !recyclerView.canScrollVertically(1)){
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

    private Observer<PagedList<NetSummary>> dataObserver = new Observer<PagedList<NetSummary>>() {
        @Override
        public void onChanged(PagedList<NetSummary> pagedList) {
            if (IadtController.get().isDebug())
                Log.v(Iadt.TAG, "NetScreen observer OnChange (" + pagedList.size() + ")");

            //adapter.getCurrentList().getDataSource().invalidate();
            adapter.submitList(pagedList);
            //adapter.notifyDataSetChanged();
        }
    };

    private void observeData() {
        liveData.observe(ProcessLifecycleOwner.get(), dataObserver);
        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "NetScreen observer added");
    }

    private void removeDataObserver() {
        liveData.removeObservers(ProcessLifecycleOwner.get());
        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "NetScreen observer removed");
    }

    //endregion


    //region [ FILTER ]

    private void initFilter() {
        filter = IadtController.get().getSessionManager().getCurrentUid();
    }

    //endregion


    //region [ TOOL BAR ]

    private void initToolbar() {
        toolbarSearch = new ToolbarSearchHelper(getToolbar(), R.id.action_search);
        toolbarSearch.setHint("Search...");
        toolbarSearch.setOnChangeListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //TODO: update net filter
                //dataSourceFactory.getFilter().setText(newText);
                adapter.getCurrentList().getDataSource().invalidate();
                return false;
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int selected = item.getItemId();
        //OverlayService.performNavigation(AnalysisScreen.class);

        if (selected == R.id.action_session) {
            onProfilesButton();
        }
        else if (selected == R.id.action_simulate) {
            onSimulateButton();
        }
        else if (selected == R.id.action_share) {
            onShareButton();
        }
        else{
            Iadt.showMessage("Not already implemented");
        }
        return super.onMenuItemClick(item);
    }

    private void onSimulateButton() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext())
                .setTitle("Inject network traffic?")
                .setMessage("We can do few network request to HttpBin to help you testing this features. Do you want to inject some network traffic?")
                .setCancelable(true)
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HttpBinService.simulation();
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(Layer.getLayoutType());
        alertDialog.show();
    }

    private void onProfilesButton() {
        //TODO: show session selector
    }

    private void onShareButton() {
        //TODO:
        //DocumentRepository.shareDocument(DocumentType.LOG_FILTER, DateUtils.getLong());
    }

    //endregion

    @Override
    public void onItemClick(View itemView, final int clickedPosition, long clickedId) {
        OverlayService.performNavigation(NetDetailScreen.class, clickedId + "");

    }

    private void scrollToBottom() {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(adapter.getItemCount()-1);
            }
        });
    }
}
