package es.rafaco.inappdevtools.library.logic.reports;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreenHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.AppInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.EmailUtils;

public class ReportHelper extends OverlayScreenHelper {

    @Override
    public String getReportPath() {
        return null;
    }

    @Override
    public String getReportContent() {
        return null;
    }

    public enum ReportType { SESSION, CRASH, WIZARD, FULL }

    ReportType type;
    Object target;

    public void start(ReportType type, Object target) {
        this.type = type;
        this.target = target;
        boolean isHtml = false;

        EmailUtils.sendEmailIntent(context,
                getEmailTo(),
                "",
                getEmailSubject(),
                getEmailBody(isHtml),
                getFilePaths(),
                false);
    }

    @NonNull
    private String getEmailTo() {
        //TODO: IMPORTANT pick from configuration
        return "rafaco@gmail.com";
    }

    private String getEmailSubject(){
        AppInfoHelper helper = new AppInfoHelper(context);
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

    @NonNull
    private String getEmailBody(boolean isHtml) {
        String emailbody;
        String userTextPlaceholder = "Hi devs,\n\n";
        String jump = "\n";

        if(!isHtml){
            emailbody = new  StringBuilder()
                    //.append(getEmailSubject())
                    .append(userTextPlaceholder)
                    //.append(jump)
                    .toString();
        }else{
            emailbody = new  StringBuilder()
                    .append("<h2><b>Iadt report!</b></h2>")
                    .append("<p><b>Some Content</b></p>")
                    .append("<small><p>More content</p></small>")
                    .append("<a href = \"https://example.com\">https://example.com</a>")
                    .toString();
        }
        return emailbody;
    }

    @NonNull
    private List<String> getFilePaths() {
        List<String> filePaths = new ArrayList<>();
        filePaths.add(new InfoHelper().getReportPath());

        if(type.equals(ReportType.SESSION)){

            filePaths.add(new LogcatHelper().getReportPath());

            //Include only the last one
            //filePaths.add(new ScreenHelper().getReportPath());

            try{
                ArrayList<Uri> screens = (ArrayList<Uri>)target;
                if (screens != null && screens.size()>0){
                    for (Uri screen : screens) {
                        filePaths.add(screen.getPath());
                    }
                }
            }catch (Exception e){
                Log.e(Iadt.TAG, "Exception parsing screens for report");
            }

            /* TODO: Re-enable db dump
            try {
                SupportSQLiteDatabase db = IadtController.getSelectedQuery().getDatabase().getOpenHelper().getReadableDatabase();
                String name = IadtController.getSelectedQuery().getDatabase().getOpenHelper().getDatabaseName();
                filePaths.add(SqliteExporter.export(name, db));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
        else if (type.equals(ReportType.CRASH)){
            Crash crash = (Crash) target;
            filePaths.addAll(new CrashHelper().getReportPaths(crash));
        }
        return filePaths;
    }
}
