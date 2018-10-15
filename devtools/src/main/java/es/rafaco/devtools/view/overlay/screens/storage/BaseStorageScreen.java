package es.rafaco.devtools.view.overlay.screens.storage;

import android.view.View;
import android.view.ViewGroup;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;

public abstract class BaseStorageScreen extends OverlayScreen {

    public BaseStorageScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_storage_body; }

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onStart(ViewGroup toolHead) {
        View view = getLayoutView();
        view.setClickable(true);
        bodyView.addView(view);

        onViewCreated();
    }

    protected abstract View getLayoutView();
    protected abstract void onViewCreated();

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }


    //region [ BaseFragment ]

    protected void showError(String message) {
        DevTools.showMessage("Error from Pandora: " + message);
    }

    protected final void showLoading() {
        //TODO
    }

    protected final void hideLoading() {
        //TODO
    }

    //endregion
}