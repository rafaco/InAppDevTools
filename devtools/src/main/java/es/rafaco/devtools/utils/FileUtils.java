package es.rafaco.devtools.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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

    @NonNull
    public static String createFileWithContent(String subfolder, String filename, String content) {

        if(FileUtils.isExternalStorageWritable()){
            File file = createNewFile(subfolder, filename);
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

                return file.getPath();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

        return null;
    }

    public static void openFileExternally(Context context, String filePath) {
        File file = new File(filePath);
        String type = FileUtils.getMimeType(file);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = context.getApplicationContext().getPackageName() + ".devtools.provider";
            Uri contentUri = FileProvider.getUriForFile(context, authority, file);
            intent.setDataAndType(contentUri, type);
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
        context.startActivity(intent);
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



    private static File getCategoryFolder(String category){
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

    private static String getMimeType(File file) {
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        return mime.getMimeTypeFromExtension(extension);
    }

    public static void scanMediaFile(Context context, File file) {
        // Tell the media scanner about the new file so that it is immediately available to the user.
        MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }
}
