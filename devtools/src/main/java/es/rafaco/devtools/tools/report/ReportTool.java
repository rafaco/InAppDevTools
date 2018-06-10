package es.rafaco.devtools.tools.report;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.tools.Tool;
import es.rafaco.devtools.tools.ToolsManager;
import es.rafaco.devtools.tools.home.HomeInfo;

public class ReportTool extends Tool {

    private TextView out;
    private Button sendButton;
    private Process process;

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
        out = (TextView) getView().findViewById(getResourceId(getView(), "id", "out"));
        sendButton = (Button) getView().findViewById(getResourceId(getView(), "id", "report_button"));

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendEmailPressed();
            }
        });
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
        if (process != null)
            process.destroy();
    }

    @Override
    public HomeInfo getHomeInfo(){
        HomeInfo info = new HomeInfo(ReportTool.class,
                getTitle(), //"Send a Report"
                "Send a bug, exception or feedback straight to the developers. Choose which attachments to include and add your own description or steps to reproduce it later in GMail.",
                ContextCompat.getColor(getContext(), R.color.rally_green));
        return  info;
    }



    private void onSendEmailPressed() {
        List<String> files = new ArrayList<>();
        files.add(saveLogcatToFile());
        sendEmail("rafaco@gmail.com", "",
                "DevTools report from ",
                "[Replace this line by your comments]" + "\n",
                files);
    }


    public void sendEmail(String emailTo, String emailCC,
                      String subject, String emailText, List<String> filePaths) {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});
        emailIntent.putExtra(Intent.EXTRA_CC, new String[]{emailCC});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
        
        ArrayList<Uri> uris = new ArrayList<>();
        for (String file : filePaths) {
            File fileIn = new File(file);
            Uri u = Uri.fromFile(fileIn);
            uris.add(u);
        }
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        Intent chooserIntent = Intent.createChooser(emailIntent, "Send mail...");
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getView().getContext().startActivity(chooserIntent);
    }

    public String saveLogcatToFile(){
        if(isExternalStorageWritable()){

            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/MyPersonalAppFolder");
            File logDirectory = new File(appDirectory + "/log");
            File logFile = new File(logDirectory, "logcat_" + System.currentTimeMillis() + ".txt");

            // create app folder
            if (!appDirectory.exists()) {
                appDirectory.mkdir();
            }

            // create log folder
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
            }

            // clear the previous logcat and then write the new one to the file
            try {
                process = Runtime.getRuntime().exec("logcat -d -f " + logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return logFile.getPath();

        } else if(isExternalStorageReadable() ){
            // only readable
        } else{
            // not accessible
        }

        return null;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }
}
