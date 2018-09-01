package es.rafaco.devtools.view.overlay.screens.report;

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
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.DecoratedToolInfoAdapter;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.screens.screenshots.ScreensScreen;

public class ReportScreen extends OverlayScreen {

    private TextView out;
    private Button sendButton;
    private DecoratedToolInfoAdapter adapter;
    private RecyclerView recyclerView;
    private TextView header;
    private Button manageScreensButton;

    public ReportScreen(MainOverlayLayerManager manager) {
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


    private void initView() {
        out = getView().findViewById(R.id.out);
        header = getView().findViewById(R.id.report_welcome);
        sendButton = getView().findViewById(R.id.report_button);
        manageScreensButton = getView().findViewById(R.id.manage_screens_button);

        header.setText("Choose elements to send and press Send");
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendReportPressed();
            }
        });
        manageScreensButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onManageScreensPressed();
            }
        });
    }

    private void initAdapter() {
        ArrayList<DecoratedToolInfo> array = new ArrayList<>();
        array.addAll(DevTools.getToolManager().getReportInfos());

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

    private void onManageScreensPressed() {
        getScreenManager().goTo(ScreensScreen.class);
    }

    //endregion
}
