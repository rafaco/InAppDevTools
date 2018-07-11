package es.rafaco.devtools.view.dialog;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.db.errors.Anr;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.db.errors.Screen;
import es.rafaco.devtools.utils.ThreadUtils;
import es.rafaco.devtools.view.overlay.tools.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.tools.DecoratedToolInfoAdapter;
import es.rafaco.devtools.view.overlay.tools.errors.ErrorsTool;
import es.rafaco.devtools.view.overlay.tools.report.ReportEmailHelper;

public class ReportDialogActivity extends AppCompatActivity {

    private DecoratedToolInfoAdapter adapter;
    private RecyclerView recyclerView;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                final List<Crash> crashes = DevTools.getDatabase().crashDao().getAll();
                final List<Anr> anrs = DevTools.getDatabase().anrDao().getAll();
                final List<Screen> screens = DevTools.getDatabase().screenDao().getAll();
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buildDialog(crashes, anrs, screens);
                    }
                });
            }
        });
    }

    private void buildDialog(List<Crash> crashes, List<Anr> anrs, List<Screen> screens) {

        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_report, null);
        builder.setView(dialogView)
                .setTitle("Building report")
                .setMessage("Choose what do you want to include")
                .setCancelable(false);

        initAdapter(dialogView);
        loadData(adapter, crashes, anrs, screens);


        AppCompatButton crashCancelButton = dialogView.findViewById(R.id.dialog_cancel_button);
        crashCancelButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               alertDialog.dismiss();
               finish();
           }
        });
        AppCompatButton crashReportButton = dialogView.findViewById(R.id.dialog_report_button);
        crashReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReportEmailHelper(ReportDialogActivity.this).sendEmailIntent();
                alertDialog.dismiss();
                finish();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void initAdapter(View dialogView){

        adapter = new DecoratedToolInfoAdapter(this, new ArrayList<DecoratedToolInfo>());
        adapter.enableSwitchMode();
        recyclerView = dialogView
                .findViewById(R.id.report_list);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void loadData(DecoratedToolInfoAdapter adapter, List<Crash> crashes, List<Anr> anrs, List<Screen> screens) {

        ArrayList<DecoratedToolInfo> array = new ArrayList<>();

        array.add(new DecoratedToolInfo(ErrorsTool.class,
                "Info",
                " - ",
                1,
                ContextCompat.getColor(this, R.color.rally_white)));

        array.add(new DecoratedToolInfo(ErrorsTool.class,
                "Errors",
                crashes.size() + " crashes and " + anrs.size() + " ANRs",
                3,
                ContextCompat.getColor(this, R.color.rally_orange)));

        array.add(new DecoratedToolInfo(ErrorsTool.class,
                "Screenshots",
                screens.size() + " screens",
                5,
                ContextCompat.getColor(this, R.color.rally_purple)));


        Collections.sort(array, new Comparator<DecoratedToolInfo>() {
            @Override
            public int compare(DecoratedToolInfo o1, DecoratedToolInfo o2) {
                return o1.getOrder().compareTo(o2.getOrder());
            }
        });

        adapter.replaceAll(array);
    }
}
