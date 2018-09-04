package es.rafaco.devtools.view.overlay.screens.errors;

import android.content.res.Configuration;
import android.support.v7.widget.ActionMenuView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
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
    private ActionMenuView actionMenuView;

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
    protected void onCreate() {
    }

    @Override
    protected void onStart(ViewGroup view) {
        out = view.findViewById(R.id.out);
        title = view.findViewById(R.id.detail_title);
        subtitle = view.findViewById(R.id.detail_subtitle);
        console = view.findViewById(R.id.detail_console);


        actionMenuView = view.findViewById(R.id.detail_menu);
        Menu bottomMenu = actionMenuView.getMenu();
        MenuInflater menuInflater = new MenuInflater(view.getContext());
        menuInflater.inflate(R.menu.detail, bottomMenu);
        actionMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                return false;
            }
        });



        if (!TextUtils.isEmpty(getParam())){
            final long crashId = Long.parseLong(getParam());

            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    crash = DevTools.getDatabase().crashDao().findById(crashId);
                    updateOutput();
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionMenuView.onConfigurationChanged(newConfig);

    }

    private void updateOutput() {
        helper = new CrashHelper();
        InfoCollection report = helper.parseToInfoGroup(crash);
        report.removeGroupEntries(3);

        title.setText(crash.getException() + " " + DateUtils.getElapsedTimeLowered(crash.getDate()));
        subtitle.setText(crash.getMessage());
        out.setText(report.toString());
        console.setText(crash.getStacktrace());
        actionMenuView.onConfigurationChanged(getContext().getResources().getConfiguration());
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }
}
