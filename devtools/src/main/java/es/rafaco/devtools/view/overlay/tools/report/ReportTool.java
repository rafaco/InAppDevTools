package es.rafaco.devtools.view.overlay.tools.report;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.db.errors.Screen;
import es.rafaco.devtools.logic.PermissionActivity;
import es.rafaco.devtools.view.overlay.tools.DecoratedToolInfoAdapter;
import es.rafaco.devtools.view.overlay.tools.OverlayTool;
import es.rafaco.devtools.view.overlay.OverlayToolsManager;
import es.rafaco.devtools.view.overlay.tools.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.tools.info.InfoHelper;
import es.rafaco.devtools.view.overlay.tools.info.InfoTool;
import es.rafaco.devtools.view.overlay.tools.log.LogTool;
import es.rafaco.devtools.view.overlay.tools.screenshot.ScreenAdapter;

public class ReportTool extends OverlayTool {

    private TextView out;
    private Button sendButton;
    private DecoratedToolInfoAdapter adapter;
    private RecyclerView recyclerView;
    private TextView header;

    public ReportTool(OverlayToolsManager manager) {
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
        DecoratedToolInfo info = new DecoratedToolInfo(ReportTool.class,
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
                onSendEmailPressed();
            }
        });
    }

    private void initAdapter() {
        ArrayList<DecoratedToolInfo> array = new ArrayList<>();
        array.add(getManager().getTool(InfoTool.class).getReportInfo());
        //array.add(getManager().getTool(CommandsTool.class).getReportInfo());
        array.add(getManager().getTool(LogTool.class).getReportInfo());

        adapter = new DecoratedToolInfoAdapter(this, array);
        adapter.enableSwitchMode();
        recyclerView = getView().findViewById(R.id.report_list);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }


    //region [ TOOL SPECIFIC ]

    private void onSendEmailPressed() {

        if (!PermissionActivity.isNeededWithAutoStart(getContext(),
                PermissionActivity.IntentAction.STORAGE))
            return;

        sendEmailIntent();
    }

    private void sendEmailIntent() {
        String emailTo = getEmailTo();
        String subject = getEmailSubject();

        String emailbody = "[Replace this line by your comments]" + "\n\n\n";
        List<String> filePaths = new ArrayList<>();

        if(true){
            String info = (String) getManager().getTool(InfoTool.class).getReport();
            emailbody += info;
        }

        if(true){
            String logcatPath = (String)getManager().getTool(LogTool.class).getReport();
            filePaths.add(logcatPath);
        }

        sendEmailIntent(emailTo, "",
                subject,
                emailbody,
                filePaths);
    }

    @NonNull
    private String getEmailTo() {
        return "rafaco@gmail.com";
    }

    private String getEmailSubject(){
        InfoHelper helper = new InfoHelper(getContext());
        return helper.getAppName() + " report from " + Build.BRAND + " " + Build.MODEL;
    }

    private void sendEmailIntent(String emailTo, String emailCC,
                                String subject, String emailText, List<String> filePaths) {

        final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});
        emailIntent.putExtra(Intent.EXTRA_CC, new String[]{emailCC});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);

        if (filePaths != null && filePaths.size()>0){
            ArrayList<Uri> uris = new ArrayList<>();
            for (String file : filePaths) {
                File fileIn = new File(file);
                if (!fileIn.exists() || !fileIn.canRead()) {
                    DevTools.showMessage("Attachment Error");
                    return;
                }
                Uri u = Uri.fromFile(fileIn);
                uris.add(u);
            }
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        }

        Intent chooserIntent = Intent.createChooser(emailIntent, "Send mail...");
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getView().getContext().startActivity(chooserIntent);
    }

    //endregion
}
