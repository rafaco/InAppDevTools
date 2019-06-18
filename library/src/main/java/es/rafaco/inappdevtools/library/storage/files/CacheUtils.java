package es.rafaco.inappdevtools.library.storage.files;

import android.content.Context;

import java.io.File;

import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class CacheUtils {

    public static void deleteAll(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            FriendlyLog.logException("Exception cleaning cache: ", e);
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
