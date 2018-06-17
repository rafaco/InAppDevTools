package es.rafaco.devtools.tools.report;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.PermissionActivity;
import es.rafaco.devtools.tools.DecoratedToolInfoAdapter;
import es.rafaco.devtools.tools.Tool;
import es.rafaco.devtools.tools.ToolsManager;
import es.rafaco.devtools.tools.DecoratedToolInfo;
import es.rafaco.devtools.tools.info.InfoHelper;
import es.rafaco.devtools.tools.info.InfoTool;
import es.rafaco.devtools.tools.log.LogTool;

public class ReportTool extends Tool {

    private TextView out;
    private Button sendButton;
    private DecoratedToolInfoAdapter adapter;
    private ListView reportList;
    private TextView header;

    public ReportTool(ToolsManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Report";
    }

    @Override
    public String getLayoutId() {
        return "tool_report";
    }

    @Override
    protected void onInit() {

    }

    @Override
    protected void onStart(View toolView) {
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
        //array.add(getManager().getTool(ShellTool.class).getReportInfo());
        array.add(getManager().getTool(LogTool.class).getReportInfo());

        adapter = new DecoratedToolInfoAdapter(this, array);
        adapter.enableSwitchMode();
        reportList = getView().findViewById(R.id.report_list);
        reportList.setAdapter(adapter);
    }


    //region [ TOOL SPECIFIC ]

    private void onSendEmailPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                    (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){
                DevTools.showMessage("Permissions needed");
                Intent intent = PermissionActivity.buildIntent(PermissionActivity.IntentAction.STORAGE, getContext());
                getContext().startActivity(intent, null);
                return;
            }
        }
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
