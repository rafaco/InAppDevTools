package es.rafaco.devtools.utils;

import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    @NonNull
    public static File createNewFile(String subfolder, String filename) {
        File libDirectory = new File(Environment.getExternalStorageDirectory() + "/DevTools");
        File logDirectory = new File(libDirectory + "/" + subfolder);
        File file = new File(logDirectory, filename);

        // create app folder
        if (!libDirectory.exists()) {
            libDirectory.mkdir();
        }

        // create log folder
        if (!logDirectory.exists()) {
            logDirectory.mkdir();
        }
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }
}
