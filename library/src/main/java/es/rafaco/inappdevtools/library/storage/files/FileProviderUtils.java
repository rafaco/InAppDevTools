package es.rafaco.inappdevtools.library.storage.files;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;

//#ifdef MODERN
//@import androidx.core.content.FileProvider;
//#else
import android.support.v4.content.FileProvider;
//#endif

import java.io.File;

public class FileProviderUtils {

    public static String getAuthority(Context context) {
        return context.getApplicationContext().getPackageName() + ".iadt.files";
    }

    public static void openFileExternally(Context context, String filePath) {
        openFileExternally(context, filePath, Intent.ACTION_VIEW);
    }

    public static void openFileExternally(Context context, String filePath, String action ) {
        File file = new File(filePath);
        String type = getMimeType(file);

        Intent intent = new Intent();
        intent.setAction(action);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = getAuthority(context);
            Uri contentUri = FileProvider.getUriForFile(context, authority, file);
            intent.setDataAndType(contentUri, type);
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try{
            context.startActivity(intent);
        }catch (ActivityNotFoundException exception){

        }
    }

    private static String getMimeType(File file) {
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        return mime.getMimeTypeFromExtension(extension);
    }

    public static void shareText(Context context, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);

        Intent chooserIntent = Intent.createChooser(intent, "Share with");
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chooserIntent);
    }
}
