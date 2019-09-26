package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.os.Handler;
import android.os.Looper;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.info.InfoReport;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;

public class InfoScreen extends Screen {

    private Timer updateTimer;
    private boolean[] expandedState;

    private RelativeLayout overviewView;
    private TextView overviewTitleView;
    private TextView overviewIconView;
    private TextView overviewContentView;

    private RecyclerView flexibleContents;
    private FlexibleAdapter adapter;
    private int infoReportIndex;

    public InfoScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Info";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_info_body; }

    @Override
    protected void onCreate() {
        //Deliberately empty
    }

    @Override
    protected void onStart(ViewGroup view) {
        overviewView = view.findViewById(R.id.overview);
        overviewContentView = view.findViewById(R.id.overview_content);
        overviewIconView = view.findViewById(R.id.overview_icon);
        overviewTitleView = view.findViewById(R.id.overview_title);
        flexibleContents = view.findViewById(R.id.flexible_contents);

        adapter = new FlexibleAdapter(1, new ArrayList<>());
        adapter.setScreen(this);
        flexibleContents.setAdapter(adapter);

        infoReportIndex = getInitialPosition();

        updateView(getData(infoReportIndex));

        if (infoReportIndex == 0){
            startUpdateTimer();
        }
    }

    private int getInitialPosition() {
        if (TextUtils.isEmpty(getParam())){
            return 0;
        }

        int paramPosition = Integer.parseInt(getParam());
        return paramPosition;
    }

    private InfoReportData getData(int reportPosition) {
        InfoReport report = InfoReport.values()[reportPosition];
        InfoReportData reportData = IadtController.get().getInfoManager().getReportData(report);
        if (expandedState == null){
            initExpandedState(reportData.getGroups().size());
        }
        reportData = updateDataWithExpandedState(reportData);
        return reportData;
    }

    public void updateView(InfoReportData reportData) {
        updateHeader(reportData);
        updateContents(reportData);
    }

    private void updateHeader(InfoReportData data) {
        getScreenManager().setTitle(data.getTitle() + " Info");
        overviewTitleView.setText(data.getTitle());

        if (data.getIcon()>0){
            IconUtils.markAsIconContainer(overviewIconView, IconUtils.MATERIAL);
            overviewIconView.setText(data.getIcon());
            overviewIconView.setVisibility(View.VISIBLE);
        }else{
            overviewIconView.setVisibility(View.GONE);
        }

        overviewContentView.setText(data.getOverview());
    }

    private void updateContents(InfoReportData data) {
        List<Object> objectList = new ArrayList<Object>(data.getGroups());
        adapter.replaceItems(objectList);
    }

    private void initExpandedState(int size) {
        expandedState = new boolean[size];
        Arrays.fill(expandedState, false);
    }

    public boolean toggleExpandedState(int position){
        boolean newState = !expandedState[position];
        expandedState[position] = newState;
        return newState;
    }

    private InfoReportData updateDataWithExpandedState(InfoReportData reportData) {
        for (int i = 0; i < reportData.getGroups().size(); i++) {
            reportData.getGroups().get(i).setExpanded(expandedState[i]);
        }
        return reportData;
    }


    private void startUpdateTimer() {
        final Handler handler = new Handler(Looper.getMainLooper());
        updateTimer = new Timer(false);
        TimerTask updateTimerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateView(getData(infoReportIndex));
                        startUpdateTimer();
                    }
                });
            }
        };
        updateTimer.schedule(updateTimerTask, 2000);
    }

    private void stopUpdateTimer() {
        if (updateTimer!=null) updateTimer.cancel();
    }

    @Override
    protected void onStop() {
        //Deliberately empty
    }

    @Override
    protected void onDestroy() {
        stopUpdateTimer();
    }
}
