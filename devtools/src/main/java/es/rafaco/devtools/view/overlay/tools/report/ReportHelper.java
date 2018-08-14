package es.rafaco.devtools.view.overlay.tools.report;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.utils.SqliteExporter;
import es.rafaco.devtools.view.overlay.tools.errors.CrashHelper;
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
    }

    public void start(){
        buildReport();
    }

    private void buildReport(){

        boolean isHtml = false;
        String emailTo = getEmailTo();
        String subject = getEmailSubject();

        String userTextPlaceholder = "Hi devs,\n\n";
        String jump = "\n";
        String emailbody;

        if(!isHtml){
            emailbody = new  StringBuilder()
                    //.append(getEmailSubject())
                    .append(userTextPlaceholder)
                    //.append(jump)
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
        filePaths.add(new InfoHelper(context).buildReport());

        if(type.equals(ReportType.SESSION)){

            filePaths.add(new LogHelper(context).buildReport());
            filePaths.add(new ScreenHelper(context).buildReport());

            try{
                ArrayList<Uri> screens = (ArrayList<Uri>)target;
                if (screens != null && screens.size()>0){
                    for (Uri screen : screens) {
                        filePaths.add(screen.getPath());
                    }
                }
            }catch (Exception e){
                Log.e(DevTools.TAG, "Exception parsing screens for report");
            }

            try {
                SupportSQLiteDatabase db = DevTools.getDatabase().getOpenHelper().getReadableDatabase();
                String name = DevTools.getDatabase().getOpenHelper().getDatabaseName();
                filePaths.add(SqliteExporter.export(name, db));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(ReportType.CRASH)){
            Crash crash = (Crash) target;
            filePaths.addAll(new CrashHelper(context).buildReport(crash));
        }
        return filePaths;
    }

    @NonNull
    private String getEmailTo() {
        return "rafaco@gmail.com";
    }

    private String getEmailSubject(){
        InfoHelper helper = new InfoHelper(context);
        String formatter = "%s %s report from %s %s";
        String currentType = "";
        if(type.equals(ReportType.SESSION)){
            currentType = "session";
        }else if (type.equals(ReportType.CRASH)){
            currentType = "crash";
        }else if (type.equals(ReportType.FULL)){
            currentType = "full";
        }
        return String.format(formatter,
                helper.getAppName(), currentType,
                Build.BRAND, Build.MODEL);
    }
}
