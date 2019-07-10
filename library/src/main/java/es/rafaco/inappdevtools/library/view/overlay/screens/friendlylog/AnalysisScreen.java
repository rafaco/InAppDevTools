package es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog;

import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.log.datasource.LogAnalysisHelper;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;

public class AnalysisScreen extends OverlayScreen {

    public AnalysisScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Log analysis";
    }

    @Override
    public int getBodyLayoutId() {
        return R.layout.tool_log_analysis;
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onStart(ViewGroup toolHead) {
        LogAnalysisHelper analysis = new LogAnalysisHelper();

        List<Object> data1 = new ArrayList<Object>(analysis.getSeverityResult());
        initAdapter(R.id.flexible1, data1);

        List<Object> data2 = new ArrayList<Object>(analysis.getCategoryResult());
        initAdapter(R.id.flexible2, data2);
    }

    private void initAdapter(int resourceId, List<Object> data) {
        FlexibleAdapter adapter = new FlexibleAdapter(1, data);
        RecyclerView recyclerView = bodyView.findViewById(resourceId);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }
}
