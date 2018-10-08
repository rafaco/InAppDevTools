package es.rafaco.devtools.storage.files;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.io.File;

import es.rafaco.devtools.DevTools;

// Tell the media scanner about the new file so that it is immediately available to the user.
public class MediaScannerUtils {

    public static void scan(File file) {
        String[] paths =  new String[]{file.toString()};
        scan(paths);
    }

    public static void scan(String[] paths) {
        MediaScannerConnection.scanFile(
                DevTools.getAppContext(),
                paths,
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + " -> uri=" + uri);
                    }
                });
    }
}
