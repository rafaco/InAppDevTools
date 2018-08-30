package es.rafaco.devtools.view.overlay.screens.report;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.OverlayScreenManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.DecoratedToolInfoAdapter;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.screens.info.InfoScreen;
import es.rafaco.devtools.view.overlay.screens.log.LogScreen;
import es.rafaco.devtools.view.overlay.screens.screenshots.ScreensScreen;

public class ReportScreen extends OverlayScreen {

    private TextView out;
    private Button sendButton;
    private DecoratedToolInfoAdapter adapter;
    private RecyclerView recyclerView;
    private TextView header;

    public ReportScreen(OverlayScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Report";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_report; }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onStart(ViewGroup view) {
        initView();
        initAdapter();
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }

    @Override
    public DecoratedToolInfo getHomeInfo(){
        DecoratedToolInfo info = new DecoratedToolInfo(ReportScreen.class,
                getFullTitle(), //"Send a Report"
                "Send a bug, exception or feedback straight to the developers. Choose which attachments to include and add your own description or steps to reproduce it later in GMail.",
                3,
                ContextCompat.getColor(getContext(), R.color.rally_green));
        return  info;
    }



    private void initView() {
        out = getView().findViewById(R.id.out);
        header = getView().findViewById(R.id.report_welcome);
        sendButton = getView().findViewById(R.id.report_button);

        header.setText("Choose elements to send and press Send");
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendReportPressed();
            }
        });
    }

    private void initAdapter() {
        ArrayList<DecoratedToolInfo> array = new ArrayList<>();

        //TODO: make dynamic ( foreach screen if getReportInfo != null)
        //array.add(getManager().getScreenClass(InfoScreen.class).getReportInfo());
        //array.add(getManager().getScreenClass(LogScreen.class).getReportInfo());
        //array.add(getManager().getScreenClass(ScreensScreen.class).getReportInfo());

        adapter = new DecoratedToolInfoAdapter(getContext(), array);
        adapter.enableSwitchMode();
        recyclerView = getView().findViewById(R.id.report_list);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }


    //region [ TOOL SPECIFIC ]

    private void onSendReportPressed() {
        //TODO:
        DevTools.sendReport(ReportHelper.ReportType.SESSION, null);
    }

    //endregion
}
