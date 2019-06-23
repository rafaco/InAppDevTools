package es.rafaco.inappdevtools.library.view.overlay.screens.report;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;

//#ifdef MODERN
//@import androidx.core.content.FileProvider;
//#else
import android.support.v4.content.FileProvider;
//#endif

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.storage.files.FileProviderUtils;

public class EmailUtils {

    public static void sendEmailIntent(Context context,
                                String emailTo, String emailCC,
                                 String subject, String emailText,
                                List<String> filePaths, boolean isHtml) {

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
                    Iadt.showMessage("Attachment Error");
                    //return;
                }

                Uri contentUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    String authority = FileProviderUtils.getAuthority(context);
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
