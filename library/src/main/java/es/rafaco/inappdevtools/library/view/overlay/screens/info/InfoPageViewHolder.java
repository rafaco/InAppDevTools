package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;

public class InfoPageViewHolder {

    private ViewGroup viewGroup;
    TextView overviewView;
    private RecyclerView flexibleContents;
    private FlexibleAdapter adapter;

    public InfoPageViewHolder() {
    }

    public View onCreatedView(ViewGroup view) {
        viewGroup = view;
        overviewView = view.findViewById(R.id.overview);
        flexibleContents = view.findViewById(R.id.flexible_contents);
        adapter = new FlexibleAdapter(1, new ArrayList<>());
        flexibleContents.setAdapter(adapter);
        return view;
    }

    public void update(InfoReportData reportData) {
        updateOverview(reportData);
        updateContents(reportData);
    }

    private void updateOverview(InfoReportData data) {
        overviewView.setVisibility((TextUtils.isEmpty(data.getOverview()) ? View.GONE : View.VISIBLE));
        overviewView.setText(data.getOverview());
    }

    private void updateContents(InfoReportData data) {
        List<Object> objectList = new ArrayList<Object>(data.getEntries());
        adapter.replaceItems(objectList);
    }
}
