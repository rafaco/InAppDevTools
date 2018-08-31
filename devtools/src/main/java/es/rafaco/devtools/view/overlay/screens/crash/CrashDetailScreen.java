package es.rafaco.devtools.view.overlay.screens.crash;

import android.view.ViewGroup;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.OverlayScreenManager;
import es.rafaco.devtools.view.overlay.screens.errors.CrashHelper;

public class CrashDetailScreen extends OverlayScreen {

    private CrashHelper helper;

    public CrashDetailScreen(OverlayScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Crash detail";
    }

    @Override
    public int getBodyLayoutId() {
        return R.layout.tool_info_body;
    }

    @Override
    protected void onCreate() {
        helper = new CrashHelper();
    }

    @Override
    protected void onStart(ViewGroup toolHead) {

    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }
}
