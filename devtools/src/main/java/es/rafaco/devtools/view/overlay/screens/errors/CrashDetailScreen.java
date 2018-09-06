package es.rafaco.devtools.view.overlay.screens.errors;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.utils.DateUtils;
import es.rafaco.devtools.utils.ThreadUtils;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.info.InfoCollection;

public class CrashDetailScreen extends OverlayScreen {

    private Crash crash;
    private TextView out;
    private CrashHelper helper;
    private TextView title;
    private TextView subtitle;
    private TextView console;
    private Toolbar toolbar;

    public CrashDetailScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Crash detail";
    }

    @Override
    public int getBodyLayoutId() {
        return R.layout.tool_error_detail_body;
    }

    @Override
    public int getHeadLayoutId() { return R.layout.tool_toolbar; }

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onStart(ViewGroup view) {
        initToolbar(headView);
        initView(bodyView);

        requestData();
    }


    private void initView(ViewGroup view) {
        out = view.findViewById(R.id.out);
        title = view.findViewById(R.id.detail_title);
        subtitle = view.findViewById(R.id.detail_subtitle);
        console = view.findViewById(R.id.detail_console);
    }

    private void updateView() {
        helper = new CrashHelper();
        InfoCollection report = helper.parseToInfoGroup(crash);
        report.removeGroupEntries(3);

        title.setText(crash.getException() + " " + DateUtils.getElapsedTimeLowered(crash.getDate()));
        subtitle.setText(crash.getMessage());
        out.setText(report.toString());
        console.setText(crash.getStacktrace());
    }

    private void requestData() {
        if (!TextUtils.isEmpty(getParam())){
            final long crashId = Long.parseLong(getParam());

            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    crash = DevTools.getDatabase().crashDao().findById(crashId);
                    updateView();
                }
            });
        }else{
            getScreenManager().goBack();
        }
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }

    //region [ TOOL BAR ]

    private void initToolbar(View view) {
        toolbar = view.findViewById(R.id.tool_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onToolbarButtonPressed(item);
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.detail);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toolbar.requestLayout();

    }

    protected void onHeadVisibilityChanged(int visibility) {

    }

    private void onToolbarButtonPressed(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_send)
        {
            //TODO: send all errors
            DevTools.showMessage("Not already implemented");
        }
        else if (selected == R.id.action_share)
        {
            //TODO: share error
            DevTools.showMessage("Not already implemented");
        }
        else if (selected == R.id.action_delete)
        {
            onDelete();
        }
    }

    //endregion

    private void onDelete() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DevToolsDatabase db = DevTools.getDatabase();
                db.crashDao().delete(crash);
            }
        });
        getScreenManager().goBack();
    }
}
