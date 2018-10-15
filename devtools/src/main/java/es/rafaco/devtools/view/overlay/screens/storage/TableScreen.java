package es.rafaco.devtools.view.overlay.screens.storage;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.database.DatabaseResult;
import tech.linjiang.pandora.ui.fragment.EditFragment;
import tech.linjiang.pandora.ui.fragment.TableFragment;
import tech.linjiang.pandora.ui.item.GridItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.GridDividerDecoration;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.ui.view.MenuRecyclerView;
import tech.linjiang.pandora.util.SimpleTask;
import tech.linjiang.pandora.util.Utils;
import tech.linjiang.pandora.util.ViewKnife;

public class TableScreen extends BaseStorageScreen {

    private UniversalAdapter adapter;
    private MenuRecyclerView recyclerView;
    private DatabaseSelection selection;
    private String primaryKey;

    public TableScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Table";
    }

    @Override
    protected void onViewCreated() {
        this.selection = new Gson().fromJson(getParam(), DatabaseSelection.class);
        this.primaryKey = Pandora.get().getDatabases().getPrimaryKey(selection.getKey(), selection.getTable());

        //initMenu();
        initAdapter();
        loadData(null);
    }

    protected View getLayoutView() {
        HorizontalScrollView scrollView = new HorizontalScrollView(this.getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -1);
        scrollView.setFillViewport(true);
        this.recyclerView = new MenuRecyclerView(this.getContext());
        this.recyclerView.setBackgroundColor(ViewKnife.getColor(tech.linjiang.pandora.core.R.color.pd_main_bg));
        scrollView.addView(this.recyclerView, params);
        return scrollView;
    }

    public void initAdapter() {

        this.adapter = new UniversalAdapter();
        //this.registerForContextMenu(this.recyclerView);
        this.recyclerView.addItemDecoration((new GridDividerDecoration.Builder()).setColor(ViewKnife.getColor(tech.linjiang.pandora.core.R.color.pd_divider_light)).setThickness(ViewKnife.dip2px(1.0F)).build());
        this.recyclerView.setAdapter(this.adapter);
        this.adapter.setListener(new UniversalAdapter.OnItemClickListener() {
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof GridItem) {
                    if (selection.getMode()) {
                        return;
                    }

                    if (!((GridItem)item).isEnable()) {
                        return;
                    }

                    /*
                    TableFragment.this.clickedItem = (GridItem)item;
                    Bundle bundle = new Bundle();
                    bundle.putString("param1", (String)((GridItem)item).data);
                    bundle.putSerializable("param2", TableFragment.this.callback);
                    TableFragment.this.launch(EditFragment.class, bundle);*/
                }

            }
        });
        this.adapter.setLongClickListener(new UniversalAdapter.OnItemLongClickListener() {
            public boolean onItemLongClick(int position, BaseItem item) {
                return item instanceof GridItem && !((GridItem)item).isEnable();
            }
        });
    }


    private void loadData(final String condition) {
        this.adapter.clearItems();
        this.showLoading();
        (new SimpleTask(new SimpleTask.Callback<Void, DatabaseResult>() {
            public DatabaseResult doInBackground(Void[] params) {
                return selection.getMode() ? Pandora.get().getDatabases().getTableInfo(selection.getKey(), selection.getTable()) : Pandora.get().getDatabases().query(selection.getKey(), selection.getTable(), condition);
            }

            public void onPostExecute(DatabaseResult result) {
                List<BaseItem> data = new ArrayList();
                if (result.sqlError == null) {
                    TableScreen.this.recyclerView.setLayoutManager(new GridLayoutManager(TableScreen.this.getContext(), result.columnNames.size()));
                    int pkIndex = 0;

                    int i;
                    for(i = 0; i < result.columnNames.size(); ++i) {
                        data.add(new GridItem((String)result.columnNames.get(i), true));
                        if (TextUtils.equals((CharSequence)result.columnNames.get(i), TableScreen.this.primaryKey)) {
                            pkIndex = i;
                        }
                    }

                    for(i = 0; i < result.values.size(); ++i) {
                        for(int j = 0; j < ((List)result.values.get(i)).size(); ++j) {
                            GridItem item = new GridItem((String)((List)result.values.get(i)).get(j), (String)((List)result.values.get(i)).get(pkIndex), (String)result.columnNames.get(j));
                            if (!selection.getMode() && pkIndex == j) {
                                item.setIsPrimaryKey();
                            }

                            data.add(item);
                        }
                    }

                    TableScreen.this.adapter.setItems(data);
                } else {
                    Utils.toast(result.sqlError.message);
                }

                TableScreen.this.hideLoading();
            }
        })).execute(new Void[0]);
    }
}
