package es.rafaco.inappdevtools.library.view.overlay.screens.report;

import android.content.DialogInterface;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.icons.IconDrawable;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.layers.NavigationStep;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoScreen;
import es.rafaco.inappdevtools.library.view.components.DecoratedToolInfoAdapter;
import es.rafaco.inappdevtools.library.view.components.DecoratedToolInfo;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreensScreen;

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

        TextView icon = getView().findViewById(R.id.test_icon);
        IconUtils.markAsIconContainer(icon, IconUtils.MATERIAL);
        icon.setText(R.string.gmd_3d_rotation);

        AppCompatButton icon2 = getView().findViewById(R.id.test_icon2);
        Drawable drawable = new IconDrawable(getContext(), R.string.gmd_access_alarms,
                IconUtils.MATERIAL).sizeDp(24);
        icon2.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);


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

        adapter = new DecoratedToolInfoAdapter(getContext(), getReportSelectors());
        //adapter.enableSwitchMode();

        recyclerView = getView().findViewById(R.id.report_list);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<DecoratedToolInfo> getReportSelectors(){
        ArrayList<DecoratedToolInfo> array = new ArrayList<>();
        NavigationStep step = new NavigationStep(InfoScreen.class, null);

        array.add(new DecoratedToolInfo(
                "Current session",
                "From your last restart",
                R.color.rally_blue, 1,
                new Runnable() {
                    @Override
                    public void run() {
                        onSessionReport();
                    }
                }));

        array.add(new DecoratedToolInfo(
                "Last crash report",
                "",
                R.color.rally_orange, 1,
                new Runnable() {
                    @Override
                    public void run() {
                        onCrashReport();
                    }
                }));


        array.add(new DecoratedToolInfo(
                "Custom report",
                "You choose everything",
                R.color.rally_yellow, 1,
                new Runnable() {
                    @Override
                    public void run() {
                        onCustomReport();
                    }
                }));

        array.add(new DecoratedToolInfo(
                "Full report",
                "All in a zip",
                R.color.rally_green, 1,
                new Runnable() {
                    @Override
                    public void run() {
                        onFullReport();
                    }
                }));

        return array;
    }

    private void onSessionReport() {
        DevTools.sendReport(ReportHelper.ReportType.SESSION, null);
    }

    private void onFullReport() {
        DevTools.sendReport(ReportHelper.ReportType.FULL, null);
    }

    private void onCustomReport() {
        onLevelButton();
    }

    private void onCrashReport() {
        DevTools.sendReport(ReportHelper.ReportType.CRASH, null);
    }



    private void onLevelButton() {
        String[] levelsArray = new String[]{ "Stored logs", "Crash", "Screens" };
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext())
                .setTitle("Select crash")
                .setCancelable(true)
                .setMultiChoiceItems(levelsArray, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    }
                })
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
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
