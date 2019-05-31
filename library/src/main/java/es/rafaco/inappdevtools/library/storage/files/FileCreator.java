package es.rafaco.inappdevtools.library.storage.files;

import android.util.Log;

//#ifdef MODERN
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class FileCreator {

    @NonNull
    public static String withContent(final String subfolder, final String filename, final String content) {

        File file = prepare(subfolder, filename);
        if (file == null){
            return null;
        }

        try {
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(content);

            myOutWriter.close();
            fOut.flush();
            fOut.close();

            MediaScannerUtils.scan(file);
            return file.getPath();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        return null;
    }

    public static File prepare(String subfolder, String filename) {
        File file = new File(getCategoryFolder(subfolder), filename);
        //TODO: check if file exists and skip recreation
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            FriendlyLog.logException("Error preparing file " + subfolder + ":" + filename, e);
            FriendlyLog.logException("Exception", e);
            return null;
        }
    }

    public static File getCategoryFolder(String category){
        File libDirectory = createDirIfNotExist(getLibDir());
        File categoryFolder = createDirIfNotExist(libDirectory + "/" + category);
        return categoryFolder;
    }

    private static String getLibDir(){
        return DevTools.getAppContext().getFilesDir() + "/" + DevTools.TAG.toLowerCase();
    }

    private static File createDirIfNotExist(String path){
        File dir = new File(path);
        if( !dir.exists() ){
            dir.mkdir();
        }
        return dir;
    }
}
