package es.rafaco.devtools.view.overlay.screens.storage;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.ui.view.MenuRecyclerView;
import tech.linjiang.pandora.util.ViewKnife;


public abstract class BaseStorageListScreen extends BaseStorageScreen {

    public BaseStorageListScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    private MenuRecyclerView recyclerView;
    private UniversalAdapter adapter;

    protected View getLayoutView() {
        adapter = new UniversalAdapter();
        recyclerView = new MenuRecyclerView(getContext());
        recyclerView.setBackgroundColor(ViewKnife.getColor(tech.linjiang.pandora.core.R.color.pd_main_bg));
        recyclerView.setLayoutManager(onCreateLayoutManager());
        if (needDefaultDivider()) {
            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(ViewKnife.getDrawable(tech.linjiang.pandora.core.R.drawable.pd_divider_horizontal));
            recyclerView.addItemDecoration(divider);
        }
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    protected boolean needDefaultDivider() {
        return true;
    }

    protected final MenuRecyclerView getRecyclerView() {
        return recyclerView;
    }

    public final UniversalAdapter getAdapter() {
        return adapter;
    }

    protected RecyclerView.LayoutManager onCreateLayoutManager() {
        return new LinearLayoutManager(getContext());
    }
}
