package es.rafaco.inappdevtools.library.view.overlay.screens.errors;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.storage.db.entities.Anr;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoReport;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class AnrDetailScreen extends Screen {

    private Anr anr;
    private TextView out;
    private TextView title;
    private TextView subtitle;
    private TextView console;

    public AnrDetailScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Anr detail";
    }

    @Override
    public int getBodyLayoutId() {
        return R.layout.tool_crash_detail_body;
    }

    @Override
    public int getToolbarLayoutId() {
        return R.menu.crash_detail;
    }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup toolHead) {
        out = toolHead.findViewById(R.id.out);

        title = toolHead.findViewById(R.id.detail_title);
        subtitle = toolHead.findViewById(R.id.detail_subtitle);
        console = toolHead.findViewById(R.id.detail_console);

        if (!TextUtils.isEmpty(getParam())){
            final long anrId = Long.parseLong(getParam());

            ThreadUtils.runOnMain(new Runnable() {
                @Override
                public void run() {
                    anr = IadtController.get().getDatabase().anrDao().findById(anrId);
                    updateOutput();
                }
            });
        }

        View codeViewer = toolHead.findViewById(R.id.code_view);
        codeViewer.setVisibility(View.GONE);
    }

    private void updateOutput() {
        AnrHelper helper = new AnrHelper();
        InfoReport report = helper.parseToInfoGroup(anr);
        report.removeGroupEntries(1);

        title.setText(anr.getMessage() + " " + Humanizer.getElapsedTimeLowered(anr.getDate()));
        subtitle.setText(anr.getCause());
        out.setText(report.toString());
        console.setText(anr.getStacktrace());
    }

    @Override
    protected void onStop() {
        //Nothing needed
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }
}
