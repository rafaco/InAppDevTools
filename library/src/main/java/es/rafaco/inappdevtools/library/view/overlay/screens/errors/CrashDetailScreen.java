/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.view.overlay.screens.errors;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
//#endif

import java.util.List;

import es.rafaco.compat.AppCompatButton;
import es.rafaco.compat.AppCompatImageButton;
import es.rafaco.compat.RecyclerView;
import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.log.filter.LogFilterHelper;
import es.rafaco.inappdevtools.library.logic.reports.ReportType;
import es.rafaco.inappdevtools.library.logic.utils.ClipboardUtils;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.storage.files.utils.FileProviderUtils;
import es.rafaco.inappdevtools.library.view.components.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.ImageLoaderAsyncTask;

public class CrashDetailScreen extends Screen {

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
    private AppCompatButton reproStepsButton;
    private AppCompatButton revealDetailsButton;
    private AppCompatButton reportButton;

    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    private TextView session;

    private LinearLayout messageButtons1;
    private AppCompatButton revealStacktrace1;
    private AppCompatImageButton copy1;
    private AppCompatImageButton google1;
    private LinearLayout messageButtons2;
    private AppCompatButton revealStacktrace2;
    private AppCompatImageButton copy2;
    private AppCompatImageButton google2;

    public CrashDetailScreen(ScreenManager manager) {
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
        session = view.findViewById(R.id.detail_session);
        subtitle = view.findViewById(R.id.detail_subtitle);
        messageButtons1 = view.findViewById(R.id.message_buttons1);
        copy1 = view.findViewById(R.id.copy1);
        revealStacktrace1 = view.findViewById(R.id.reveal_stacktrace1);
        google1 = view.findViewById(R.id.google1);
        recyclerView1 = bodyView.findViewById(R.id.flexible1);

        title2 = view.findViewById(R.id.detail_title2);
        subtitle2 = view.findViewById(R.id.detail_subtitle2);
        messageButtons2 = view.findViewById(R.id.message_buttons2);
        revealStacktrace2 = view.findViewById(R.id.reveal_stacktrace2);
        copy2 = view.findViewById(R.id.copy2);
        google2 = view.findViewById(R.id.google2);
        thread = view.findViewById(R.id.detail_thread);
        recyclerView2 = bodyView.findViewById(R.id.flexible2);

        reportButton = view.findViewById(R.id.report_button);
        reproStepsButton = view.findViewById(R.id.repro_steps_button);
        logcatButton = view.findViewById(R.id.logcat_button);
        revealDetailsButton = view.findViewById(R.id.reveal_details_button);
        out = view.findViewById(R.id.out);
    }

    private void requestData() {
        if (!TextUtils.isEmpty(getParam())){
            final long crashId = Long.parseLong(getParam());
            crash = IadtController.get().getDatabase().crashDao().findById(crashId);
        }
        else{
            crash = IadtController.get().getDatabase().crashDao().getLast();
        }
        updateView();
    }

    private void updateView() {
        initOverview();
        initButtons();
        initExceptionDetails();
        initStackTraces();
        //initFooter();
    }

    private void initOverview() {
        when.setText("Session " + crash.getSessionId() + " crashed " + Humanizer.getElapsedTimeLowered(crash.getDate()));
        //session.setText();
        foreground.setText("App status: " + (crash.isForeground() ? "Foreground" : "Background"));
        lastActivity.setText("Last activity: " + crash.getLastActivity());
        thread.setText("Crashed thread: " + crash.getThreadName());
        
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                long screenId = crash.getScreenId();
                final Screenshot screenshot = IadtController.get().getDatabase().screenshotDao().findById(screenId);
                if (screenshot !=null && !TextUtils.isEmpty(screenshot.getPath())){
                    new ImageLoaderAsyncTask(thumbnail).execute(screenshot.getPath());
                    thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FileProviderUtils.viewExternally("", screenshot.getPath() );
                        }
                    });
                }
            }
        });
    }

    private void initButtons() {
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IadtController.get().startReportWizard(ReportType.CRASH, crash.getUid());
            }
        });

        final long logId = IadtController.getDatabase().friendlyDao()
                .findLogIdByCrashId(crash.getUid());

        Session crashSession = IadtController.getDatabase().sessionDao()
                .findByCrashId(crash.getUid());

        if(crashSession==null){
            reproStepsButton.setVisibility(View.GONE);
            logcatButton.setVisibility(View.GONE);
            return;
        }

        //TODO: replace by crash.getSessionId()
        long crashSessionId = crashSession.getUid();
        final LogFilterHelper stepsFilter = new LogFilterHelper(LogFilterHelper.Preset.REPRO_STEPS);
        stepsFilter.setSessionById(crashSessionId);
        reproStepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OverlayService.performNavigation(LogScreen.class,
                        LogScreen.buildParams(stepsFilter.getUiFilter(), logId));
            }
        });

        final LogFilterHelper logsFilter = new LogFilterHelper(LogFilterHelper.Preset.DEBUG);
        logsFilter.setSessionById(crashSessionId);
        logcatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OverlayService.performNavigation(LogScreen.class,
                        LogScreen.buildParams(logsFilter.getUiFilter(), logId));
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

        final String composedMessage = crash.getException() + " " + crash.getMessage();
        revealStacktrace1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isHidden = (recyclerView1.getVisibility() == View.GONE);
                recyclerView1.setVisibility(isHidden ? View.VISIBLE : View.GONE);
                int i = isHidden ? R.drawable.ic_arrow_up_white_24dp : R.drawable.ic_arrow_down_white_24dp;
                Drawable drawable = ContextCompat.getDrawable(getContext(), i);
                revealStacktrace1.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                revealStacktrace1.setText(isHidden ? "Hide Stacktrace" : "Show Stacktrace");
            }
        });
        copy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardUtils.save(getContext(), composedMessage);
                Iadt.showMessage("Exception copied to clipboard");
            }
        });
        google1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.google.com/search?q=" + composedMessage;
                ExternalIntentUtils.viewUrl(url);
            }
        });

        recyclerView1.setVisibility(View.GONE);

        if (cause!=null){
            title2.setText("Caused by: " + crash.getCauseException());
            subtitle2.setText(crash.getCauseMessage());

            final String composedMessage2 = crash.getCauseException() + " " + crash.getCauseMessage();
            revealStacktrace2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isHidden = (recyclerView2.getVisibility() == View.GONE);
                    recyclerView2.setVisibility(isHidden ? View.VISIBLE : View.GONE);
                    int i = isHidden ? R.drawable.ic_arrow_up_white_24dp : R.drawable.ic_arrow_down_white_24dp;
                    Drawable drawable = ContextCompat.getDrawable(getContext(), i);
                    revealStacktrace2.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    revealStacktrace2.setText(isHidden ? "Hide Stacktrace" : "Show Stacktrace");
                }
            });
            copy2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardUtils.save(getContext(), composedMessage);
                    Iadt.showMessage("Exception copied to clipboard");
                }
            });
            google2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://www.google.com/search?q=" + composedMessage2;
                    ExternalIntentUtils.viewUrl(url);
                }
            });
        }else{
            title2.setVisibility(View.GONE);
            subtitle2.setVisibility(View.GONE);
            messageButtons2.setVisibility(View.GONE);
        }
    }

    private void initStackTraces() {
        List<Sourcetrace> traces = DevToolsDatabase.getInstance().sourcetraceDao().filterCrash(crash.getUid());

        FlexibleAdapter adapter;
        TraceGrouper grouper = new TraceGrouper();
        grouper.process(traces);

        adapter = grouper.getExceptionAdapter();
        if (adapter != null){
            recyclerView1.setAdapter(adapter);
        }

        adapter = grouper.getCauseAdapter();
        if (adapter != null){
            recyclerView2.setAdapter(adapter);
        }
    }

    private void initFooter() {
        DocumentData report = DocumentRepository.getDocument(DocumentType.CRASH, crash);
        out.setText(report.toString());
        
        revealDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newVisibility = (out.getVisibility()==View.GONE) ? View.VISIBLE : View.GONE;
                out.setVisibility(newVisibility);
                getScreenManager().getScreenLayer().scrollToView(out);
            }
        });
    }

    //region [ TOOL BAR ]

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_send) {
            IadtController.get().startReportWizard(ReportType.CRASH, crash.getUid());
        }
        else if (selected == R.id.action_share)
        {
            Iadt.showMessage("Sharing crash document");
            DocumentRepository.shareDocument(DocumentType.CRASH, crash);
            return true;
        }
        /*else if (selected == R.id.action_delete)
        {
            onDelete();
        }*/
        return super.onMenuItemClick(item);
    }

    private void onDelete() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DevToolsDatabase db = IadtController.get().getDatabase();
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
