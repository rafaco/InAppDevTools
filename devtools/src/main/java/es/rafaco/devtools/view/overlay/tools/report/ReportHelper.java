package es.rafaco.devtools.view.overlay.tools.report;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.logic.PermissionActivity;
import es.rafaco.devtools.utils.SqliteExporter;
import es.rafaco.devtools.view.overlay.tools.info.InfoHelper;
import es.rafaco.devtools.view.overlay.tools.log.LogHelper;
import es.rafaco.devtools.view.overlay.tools.screenshot.ScreenHelper;

public class ReportHelper {

    public enum ReportType { CRASH, SESSION, FULL }

    Context context;
    ReportType type;
    Object target;

    public ReportHelper(Context context, ReportType type, Object target) {
        this.context = context;
        this.type = type;
        this.target = target;

        //TODO: remove this dependency
        if (!PermissionActivity.isNeededWithAutoStart(context,
                PermissionActivity.IntentAction.STORAGE))
            return;
    }

    public void build(){
        if (type.equals(ReportType.CRASH)){
            sendCrashReport((Crash) target);
        }else if (type.equals(ReportType.SESSION)){
            buildSessionReport();
        }
    }

    public void sendCrashReport(Crash crash){

    }

    public void buildSessionReport(){

        boolean isHtml = false;
        String emailTo = getEmailTo();
        String subject = getEmailSubject();

        String userTextPlaceholder = "[Replace this line by your comments]";
        String bigJump = "\n\n\n";
        String emailbody;

        if(!isHtml){
            emailbody = new  StringBuilder()
                    .append(getEmailSubject())
                    .append(userTextPlaceholder)
                    .append(bigJump)
                    .toString();
        }else{
            emailbody = new  StringBuilder()
                    .append("<h2><b>DevTools report!</b></h2>")
                    .append("<p><b>Some Content</b></p>")
                    .append("<small><p>More content</p></small>")
                    .append("<a href = \"https://example.com\">https://example.com</a>")
                    .toString();
        }

        List<String> filePaths = getAttachmentPaths();

        EmailUtils.sendEmailIntent(context,
                emailTo, "",
                subject, emailbody,
                filePaths, false);
    }

    @NonNull
    private List<String> getAttachmentPaths() {
        List<String> filePaths = new ArrayList<>();
        if(true){
            filePaths.add(new LogHelper(context).buildReport());
            filePaths.add(new InfoHelper(context).buildReport());
            filePaths.add(new ScreenHelper(context).buildReport());

            try {
                SupportSQLiteDatabase db = DevTools.getDatabase().getOpenHelper().getReadableDatabase();
                String name = DevTools.getDatabase().getOpenHelper().getDatabaseName();
                filePaths.add(SqliteExporter.export(name, db));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePaths;
    }

    @NonNull
    private String getEmailTo() {
        return "rafaco@gmail.com";
    }

    private String getEmailSubject(){
        InfoHelper helper = new InfoHelper(context);
        return helper.getAppName() + " report from " + Build.BRAND + " " + Build.MODEL;
    }
}
