package es.rafaco.inappdevtools.library.view.overlay.screens.errors;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Screen;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.components.flex.TraceGroupItem;
import es.rafaco.inappdevtools.library.view.components.flex.TraceItem;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoCollection;
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

    //private CodeView stacktraceView;

    private FlexibleAdapter adapter1;
    private FlexibleAdapter adapter2;
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
        title2 = view.findViewById(R.id.detail_title2);
        subtitle2 = view.findViewById(R.id.detail_subtitle2);
        thread = view.findViewById(R.id.detail_thread);

        //stacktraceView = view.findViewById(R.id.code_view);
        autologButton = view.findViewById(R.id.autolog_button);
        logcatButton = view.findViewById(R.id.logcat_button);
        revealDetailsButton = view.findViewById(R.id.reveal_details_button);
        out = view.findViewById(R.id.out);

        autologButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevTools.showMessage("Not already implemented");
                //OverlayUIService.performNavigation(FriendlyLogScreen.class, ...);
            }
        });
        logcatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevTools.showMessage("Not already implemented");
                //OverlayUIService.performNavigation(LogcatScreen.class, ...);
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
        when.setText("Your app crashed " + DateUtils.getElapsedTimeLowered(crash.getDate()));

        String threadString = (crash.isMainThread()) ? "the main" : "a background";
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

        initSourcetraceAdapters();

        String message = crash.getMessage();
        String cause = helper.getCaused(crash);
        if (cause!=null && message.contains(cause)){
            message.replace(cause, "(...)");
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

        InfoCollection report = helper.parseToInfoGroup(crash);
        //report.removeGroupEntries(3);
        out.setText(report.toString());

        /*
        final StacktraceAdapter myAdapter = new StacktraceAdapter(getContext(), crash.getStacktrace());
        stacktraceView.setAdapter(myAdapter);
        stacktraceView.getOptions()
                .addCodeLineClickListener((n, line) -> {
                    if (myAdapter.canOpenSource(n)){
                        String path = myAdapter.extractPath(n);
                        int lineNumber = myAdapter.extractLineNumber(n);

                        OverlayUIService.performNavigation(SourceDetailScreen.class,
                                SourceDetailScreen.buildParams(SourcesManager.DEVTOOLS_SRC, path, lineNumber));
                    }
                });
        */
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

    //region [ SOURCE TRACE ]

    private void initSourcetraceAdapters() {

        List<Sourcetrace> traces = DevToolsDatabase.getInstance().sourcetraceDao().filterCrash(crash.getUid());

        List<Object> traceData1 = new ArrayList<>();
        List<Object> traceData2 = new ArrayList<>();

        boolean causeFound = false;
        for(int i=0;i<traces.size();i++){
            TraceItem item = new TraceItem(traces.get(i));
            if (i==0){
                item.setPosition(TraceItem.Position.START);
            }
            else if (item.getSourcetrace().getExtra()!=null
                    && item.getSourcetrace().getExtra().equals("cause")){
                causeFound = true;
                item.setPosition(TraceItem.Position.START);
            }

            if (!causeFound) traceData1.add(item);
            else traceData2.add(item);
        }

        //TODO: Collapse Sourcetraces (TraceGroupItem not working)
        //traceData1.add(new TraceGroupItem(false, 2, "Title", "Subtitle", null));
        //traceData1.add(2, new TraceGroupItem(false, 2, "Title", "Subtitle", null));
        //addGroup(traceData1, 2, 3, adapter1);


        initTraceAdapter(traceData1, adapter1, recyclerView1, R.id.flexible1);
        if (causeFound) {
            initTraceAdapter(traceData2, adapter2, recyclerView2, R.id.flexible2);
        }
    }

    private void addGroup(List<Object> traceData, int index, int count, FlexibleAdapter adapter) {
        traceData.add(index, new TraceGroupItem(false, count, "Title", "Subtitle",
                () ->{
                    setExpandedToItems(true, traceData, index, count);
                    adapter.notifyDataSetChanged();
                } ));

        setExpandedToItems(false, traceData, index, count);
    }

    private void setExpandedToItems(boolean value, List<Object> data, int start, int count) {
        for(int i = start; i < start + count + 1 ; i++){
            Object item = data.get(i);
            if (i == start){
                ((TraceGroupItem)item).setExpanded(value);
            }else{
                ((TraceItem)item).setExpanded(value);
            }
        }
    }

    private void initTraceAdapter(List<Object> traceData, FlexibleAdapter adapterP, RecyclerView recyclerViewP, int layoutId) {
        TraceItem lastItem = null;
        try{
            lastItem = (TraceItem)traceData.get(traceData.size()-1);
        }catch (Exception e){
            FriendlyLog.log("W", "Flex", "initTraceAdapter", "Unable to parse last item");
        }
        if(lastItem!=null)
            lastItem.setPosition(TraceItem.Position.END);

        adapterP = new FlexibleAdapter(1, traceData);
        recyclerViewP = bodyView.findViewById(layoutId);
        recyclerViewP.setAdapter(adapterP);
    }

    //endregion

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }


}
