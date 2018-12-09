package es.rafaco.inappdevtools.storage.files;

import android.os.Environment;
import androidx.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import es.rafaco.inappdevtools.DevTools;
import es.rafaco.inappdevtools.view.activities.PermissionActivity;

public class FileCreator {

    @NonNull
    public static File prepare(String subfolder, String filename) {
        File file = new File(getCategoryFolder(subfolder), filename);
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    public static String withContent(final String subfolder, final String filename, final String content) {

        if (!PermissionActivity.check(PermissionActivity.IntentAction.STORAGE)) {
            PermissionActivity.request(PermissionActivity.IntentAction.STORAGE,
                    new Runnable() {
                        @Override
                        public void run() {
                            withContent(subfolder, filename, content);
                        }
                    },
                    null); //TODO: handle failure
        }

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

    public static File getCategoryFolder(String category){
        File libDirectory = createDirIfNotExist(getLibDir());
        File categoryFolder = createDirIfNotExist(libDirectory + "/" + category);
        return categoryFolder;
    }

    private static String getLibDir(){
        return Environment.getExternalStorageDirectory() + "/" + DevTools.TAG;
    }

    private static File createDirIfNotExist(String path){
        File dir = new File(path);
        if( !dir.exists() ){
            dir.mkdir();
        }
        return dir;
    }
}
