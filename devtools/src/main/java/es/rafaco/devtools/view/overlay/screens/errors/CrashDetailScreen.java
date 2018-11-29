package es.rafaco.devtools.view.overlay.screens.errors;

import android.os.AsyncTask;
import androidx.appcompat.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.sources.SourcesManager;
import es.rafaco.devtools.logic.sources.StacktraceAdapter;
import es.rafaco.devtools.storage.db.DevToolsDatabase;
import es.rafaco.devtools.storage.db.entities.Crash;
import es.rafaco.devtools.storage.db.entities.Screen;
import es.rafaco.devtools.logic.utils.DateUtils;
import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.info.InfoCollection;
import es.rafaco.devtools.view.overlay.screens.report.ReportHelper;
import es.rafaco.devtools.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.devtools.view.utils.ImageLoaderAsyncTask;
import io.github.kbiakov.codeview.CodeView;

public class CrashDetailScreen extends OverlayScreen {

    private Crash crash;
    private TextView out;
    private CrashHelper helper;
    private TextView title;
    private TextView subtitle;
    private TextView body;
    private TextView title2;
    private TextView subtitle2;
    private TextView body2;
    private TextView when;
    private TextView foreground;
    private TextView lastActivity;
    private ImageView thumbnail;
    private TextView thread;
    private AppCompatButton logcatButton;
    private AppCompatButton revealDetailsButton;
    private CodeView stacktraceView;

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
    public int getToolbarLayoutId() {
        return R.menu.crash_detail;
    }

    @Override
    protected void onCreate() {
        helper = new CrashHelper();
    }

    @Override
    protected void onStart(ViewGroup view) {
        initView(bodyView);
        requestData();
    }


    private void initView(ViewGroup view) {

        when = view.findViewById(R.id.detail_when);
        foreground = view.findViewById(R.id.detail_foreground);
        lastActivity = view.findViewById(R.id.detail_last_activity);
        thumbnail = view.findViewById(R.id.thumbnail);

        title = view.findViewById(R.id.detail_title);
        subtitle = view.findViewById(R.id.detail_subtitle);
        body = view.findViewById(R.id.detail_body);
        title2 = view.findViewById(R.id.detail_title2);
        subtitle2 = view.findViewById(R.id.detail_subtitle2);
        body2 = view.findViewById(R.id.detail_body2);
        thread = view.findViewById(R.id.detail_thread);

        stacktraceView = view.findViewById(R.id.code_view);

        logcatButton = view.findViewById(R.id.logcat_button);
        logcatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevTools.showMessage("Not already implemented");
            }
        });

        revealDetailsButton = view.findViewById(R.id.reveal_details_button);
        revealDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newVisibility = (out.getVisibility()==View.GONE) ? View.VISIBLE : View.GONE;
                out.setVisibility(newVisibility);
                getScreenManager().getMainLayer().scrollToView(out);
            }
        });
        out = view.findViewById(R.id.out);
    }

    private void updateView() {

        String threadString = (crash.isMainThread()) ? "the main" : "a background";
        thread.setText("Thread: " + crash.getThreadName());
        when.setText("When: " + DateUtils.getElapsedTimeLowered(crash.getDate()));
        foreground.setText("App status: " + (crash.isForeground() ? "Foreground" : "Background"));
        lastActivity.setText("Last activity: " + crash.getLastActivity());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                long screenId = crash.getScreenId();
                Screen screen = DevTools.getDatabase().screenDao().findById(screenId);
                if (screen!=null && !TextUtils.isEmpty(screen.getPath())){
                    new ImageLoaderAsyncTask(thumbnail).execute(screen.getPath());
                }
            }
        });

        String message = crash.getMessage();
        String cause = helper.getCaused(crash);
        if (cause!=null && message.contains(cause)){
            message.replace(cause, "(...)");
        }

        title.setText(crash.getException());
        subtitle.setText(message);
        body.setText(helper.getFormattedAt(crash));

        if (cause!=null){
            title2.setText("Caused by: " + crash.getCauseException());
            subtitle2.setText(crash.getCauseMessage());
            body2.setText(helper.getCausedAt(crash));
        }else{
            title2.setVisibility(View.GONE);
            subtitle2.setVisibility(View.GONE);
            body2.setVisibility(View.GONE);
        }

        InfoCollection report = helper.parseToInfoGroup(crash);
        //report.removeGroupEntries(3);
        out.setText(report.toString());

        final StacktraceAdapter myAdapter = new StacktraceAdapter(getContext(), crash.getStacktrace());
        stacktraceView.setAdapter(myAdapter);
        stacktraceView.getOptions()
                .addCodeLineClickListener((n, line) -> {
                    if (myAdapter.canOpenSource(n)){
                        String path = myAdapter.extractPath(n);
                        int lineNumber = myAdapter.extractLineNumber(n);

                        OverlayUIService.performNavigation(SourceDetailScreen.class,
                                SourceDetailScreen.buildParams(SourcesManager.DEVTOOLS, path, lineNumber));
                    }
                });
    }

    private void requestData() {
        if (!TextUtils.isEmpty(getParam())){
            final long crashId = Long.parseLong(getParam());
            crash = DevTools.getDatabase().crashDao().findById(crashId);
        }
        else{
            crash = DevTools.getDatabase().crashDao().getLast();
        }
        updateView();

        helper.solvePendingData(crash, () -> {
            crash = DevTools.getDatabase().crashDao().findById(crash.getUid());
            updateView();
        });
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }

    //region [ TOOL BAR ]

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_send)
        {
            DevTools.sendReport(ReportHelper.ReportType.CRASH, crash.getUid());
            getScreenManager().hide();
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
        return super.onMenuItemClick(item);
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
