package es.rafaco.devtools.utils;

import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;

import es.rafaco.devtools.DevTools;

public class FileUtils {

    @NonNull
    public static File createNewFile(String subfolder, String filename) {
        File file = new File(getCategoryFolder(subfolder), filename);
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getCategoryFolder(String category){
        File libDirectory = createDirIfNotExist(getLibDir());
        File categoryFolder = createDirIfNotExist(libDirectory + "/" + category);
        return categoryFolder;
    }

    public static String getLibDir(){
        return Environment.getExternalStorageDirectory() + "/" + DevTools.TAG;
    }

    public static File createDirIfNotExist(String path){
        File dir = new File(path);
        if( !dir.exists() ){
            dir.mkdir();
        }
        return dir;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static String getMimeType(File file) {
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        return mime.getMimeTypeFromExtension(extension);
    }
}
