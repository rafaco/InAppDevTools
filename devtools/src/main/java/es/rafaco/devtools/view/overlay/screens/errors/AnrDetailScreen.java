package es.rafaco.devtools.view.overlay.screens.errors;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.db.errors.Anr;
import es.rafaco.devtools.utils.DateUtils;
import es.rafaco.devtools.utils.ThreadUtils;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.info.InfoCollection;

public class AnrDetailScreen extends OverlayScreen {

    private Anr anr;
    private TextView out;
    private AnrHelper helper;
    private TextView title;
    private TextView subtitle;
    private TextView console;

    public AnrDetailScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Anr detail";
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
            final long anrId = Long.parseLong(getParam());

            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    anr = DevTools.getDatabase().anrDao().findById(anrId);
                    updateOutput();
                }
            });
        }
    }

    private void updateOutput() {
        helper = new AnrHelper();
        InfoCollection report = helper.parseToInfoGroup(anr);
        report.removeGroupEntries(1);

        title.setText(anr.getMessage() + " " + DateUtils.getElapsedTimeLowered(anr.getDate()));
        subtitle.setText(anr.getCause());
        out.setText(report.toString());
        console.setText(anr.getStacktrace());
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }
}
