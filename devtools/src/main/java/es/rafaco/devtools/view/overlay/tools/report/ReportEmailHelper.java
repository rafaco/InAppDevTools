package es.rafaco.devtools.view.overlay.tools.report;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.Html;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.logic.PermissionActivity;
import es.rafaco.devtools.view.overlay.tools.info.InfoHelper;
import es.rafaco.devtools.view.overlay.tools.log.LogHelper;
import es.rafaco.devtools.view.overlay.tools.screenshot.ScreenHelper;

public class ReportEmailHelper {

    Context context;

    boolean isHtml = false;

    public ReportEmailHelper(Context context) {
        this.context = context;
    }

    public void sendEmailIntent() {

        //TODO: remove this dependency
        if (!PermissionActivity.isNeededWithAutoStart(context,
                PermissionActivity.IntentAction.STORAGE))
            return;

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

        sendEmailIntent(emailTo, "",
                subject,
                emailbody,
                filePaths);
    }

    @NonNull
    private List<String> getAttachmentPaths() {
        List<String> filePaths = new ArrayList<>();
        if(true){
            filePaths.add(new LogHelper(context).buildReport());
            filePaths.add(new InfoHelper(context).buildReport());
            filePaths.add(new ScreenHelper(context).buildReport());
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

    private void sendEmailIntent(String emailTo, String emailCC,
                                 String subject, String emailText, List<String> filePaths) {

        final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType(isHtml ? "text/html" : "text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});
        emailIntent.putExtra(Intent.EXTRA_CC, new String[]{emailCC});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        if (isHtml){
            emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(emailText).toString());
        }else{
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
        }

        if (filePaths != null && filePaths.size()>0){
            ArrayList<Uri> uris = new ArrayList<>();
            for (String filePath : filePaths) {
                File file = new File(filePath);

                //TODO:??
                if (!file.exists() || !file.canRead()) {
                    DevTools.showMessage("Attachment Error");
                    //return;
                }

                Uri contentUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    String authority = context.getApplicationContext().getPackageName() + ".devtools.provider";
                    contentUri = FileProvider.getUriForFile(context, authority, file);
                } else {
                    contentUri = Uri.fromFile(file);
                }
                uris.add(contentUri);
            }
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        }

        Intent chooserIntent = Intent.createChooser(emailIntent, "Send mail...");
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chooserIntent);
    }
}
