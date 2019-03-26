package es.rafaco.inappdevtools.library.view.overlay.screens.errors;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Screen;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog.FriendlyLogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.structs.InfoReport;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportHelper;
import es.rafaco.inappdevtools.library.view.utils.ImageLoaderAsyncTask;

public class CrashDetailScreen extends OverlayScreen {

    private Crash crash;
    private TextView out;
    private CrashHelper helper;
    private TextView title;
    private TextView subtitle;
    private TextView title2;
    private TextView subtitle2;
    private TextView when;
    private TextView foreground;
    private TextView lastActivity;
    private ImageView thumbnail;
    private TextView thread;
    private AppCompatButton logcatButton;
    private AppCompatButton autologButton;
    private AppCompatButton revealDetailsButton;

    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;

    public CrashDetailScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Crash detail";
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
        title2 = view.findViewById(R.id.detail_title2);
        subtitle2 = view.findViewById(R.id.detail_subtitle2);
        thread = view.findViewById(R.id.detail_thread);

        autologButton = view.findViewById(R.id.autolog_button);
        logcatButton = view.findViewById(R.id.logcat_button);
        revealDetailsButton = view.findViewById(R.id.reveal_details_button);
        out = view.findViewById(R.id.out);
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
    }

    private void updateView() {
        initOverview();
        initExceptionDetails();
        initStackTraces();

        initFooter();
    }

    private void initOverview() {
        when.setText("Your app crashed " + DateUtils.getElapsedTimeLowered(crash.getDate()));
        thread.setText("Thread: " + crash.getThreadName());
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
    }

    private void initExceptionDetails() {
        String message = crash.getMessage();
        String cause = helper.getCaused(crash);
        if (cause!=null && message!=null && message.contains(cause)){
            message = message.replace(cause, "(...)");
        }

        title.setText(crash.getException());
        subtitle.setText(message);

        if (cause!=null){
            title2.setText("Caused by: " + crash.getCauseException());
            subtitle2.setText(crash.getCauseMessage());
        }else{
            title2.setVisibility(View.GONE);
            subtitle2.setVisibility(View.GONE);
        }
    }

    private void initStackTraces() {
        List<Sourcetrace> traces = DevToolsDatabase.getInstance().sourcetraceDao().filterCrash(crash.getUid());

        FlexibleAdapter adapter;
        TraceGrouper grouper = new TraceGrouper();
        grouper.process(traces);

        adapter = grouper.getExceptionAdapter();
        if (adapter != null){
            recyclerView1 = bodyView.findViewById(R.id.flexible1);
            recyclerView1.setAdapter(adapter);
        }

        adapter = grouper.getCauseAdapter();
        if (adapter != null){
            recyclerView2 = bodyView.findViewById(R.id.flexible2);
            recyclerView2.setAdapter(adapter);
        }
    }

    private void initFooter() {
        InfoReport report = helper.parseToInfoGroup(crash);
        out.setText(report.toString());

        autologButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OverlayUIService.performNavigation(FriendlyLogScreen.class, null);
            }
        });
        logcatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OverlayUIService.performNavigation(LogScreen.class, null);
            }
        });
        revealDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newVisibility = (out.getVisibility()==View.GONE) ? View.VISIBLE : View.GONE;
                out.setVisibility(newVisibility);
                getScreenManager().getMainLayer().scrollToView(out);
            }
        });
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

    //endregion

    @Override
    protected void onStop() {
        //Nothing needed
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }
}
