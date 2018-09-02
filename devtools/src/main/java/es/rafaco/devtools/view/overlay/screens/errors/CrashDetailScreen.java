package es.rafaco.devtools.view.overlay.screens.errors;

import android.text.TextUtils;
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
    protected void onStart(ViewGroup toolHead) {
        out = toolHead.findViewById(R.id.out);
        title = toolHead.findViewById(R.id.detail_title);
        subtitle = toolHead.findViewById(R.id.detail_subtitle);
        console = toolHead.findViewById(R.id.detail_console);

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

    private void updateOutput() {
        helper = new CrashHelper();
        InfoCollection report = helper.parseToInfoGroup(crash);
        report.removeGroupEntries(3);

        title.setText(crash.getException() + " " + DateUtils.getElapsedTimeLowered(crash.getDate()));
        subtitle.setText(crash.getMessage());
        out.setText(report.toString());
        console.setText(crash.getStacktrace());
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }
}
