package es.rafaco.devtools.view.overlay.screens.errors;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.db.errors.Screen;
import es.rafaco.devtools.utils.DateUtils;
import es.rafaco.devtools.utils.ThreadUtils;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.info.InfoCollection;
import es.rafaco.devtools.view.overlay.screens.screenshots.ImageLoaderAsyncTask;

public class CrashDetailScreen extends OverlayScreen {

    private Crash crash;
    private TextView out;
    private CrashHelper helper;
    private TextView title;
    private TextView subtitle;
    private TextView console;
    private TextView title2;
    private TextView subtitle2;
    private TextView when;
    private TextView foreground;
    private TextView lastActivity;
    private ImageView thumbnail;
    private TextView thread;

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
        out = view.findViewById(R.id.out);
        when = view.findViewById(R.id.detail_when);
        foreground = view.findViewById(R.id.detail_foreground);
        lastActivity = view.findViewById(R.id.detail_last_activity);
        thumbnail = view.findViewById(R.id.thumbnail);

        title = view.findViewById(R.id.detail_title);
        subtitle = view.findViewById(R.id.detail_subtitle);
        title2 = view.findViewById(R.id.detail_title2);
        subtitle2 = view.findViewById(R.id.detail_subtitle2);
        thread = view.findViewById(R.id.detail_thread);
        console = view.findViewById(R.id.detail_console);
    }

    private void updateView() {

        when.setText("Your app crashed " + DateUtils.getElapsedTimeLowered(crash.getDate()));
        foreground.setText("State: " + (crash.isForeground() ? "Foreground" : "Background"));
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

        title.setText(crash.getException() + ": " + message);
        subtitle.setText(helper.getFormattedAt(crash));

        if (cause!=null){
            title2.setText(cause);
            subtitle2.setText(helper.getCausedAt(crash));
        }else{
            title2.setVisibility(View.GONE);
            subtitle2.setVisibility(View.GONE);
        }
        String threadString = (crash.isMainThread()) ? "the main" : "a background";
        thread.setText("running on " + threadString + " thread: " + crash.getThreadName());

        InfoCollection report = helper.parseToInfoGroup(crash);
        //report.removeGroupEntries(3);
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
                    helper.undoRawReport(crash, new Runnable() {
                        @Override
                        public void run() {
                            updateView();
                        }
                    });
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
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
