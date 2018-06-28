package es.rafaco.devtools.view.overlay.tools.screenshot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.db.errors.Screen;
import es.rafaco.devtools.logic.PermissionActivity;
import es.rafaco.devtools.view.overlay.tools.log.LogTool;
import es.rafaco.devtools.utils.ViewHierarchyUtils;

public class ScreenHelper {

    private static final String SCREENSHOTS_DIR_NAME = "Screenshots";
    private static final String SCREENSHOT_FILE_NAME_TEMPLATE = "Screenshot_%s_%s.jpg";
    private static final String SCREENSHOT_SHARE_SUBJECT_TEMPLATE = "Screenshot (%s)";

    private Context context;

    public ScreenHelper(Context context) {
        this.context = context;
    }

    public Screen takeScreen() {

        if (!PermissionActivity.isNeededWithAutoStart(context,
                PermissionActivity.IntentAction.STORAGE))
            return null;
        Log.d(DevTools.TAG, "isExternalStorageWritable?: " + LogTool.isExternalStorageWritable());


        List<Pair<String, View>> rootViews = ViewHierarchyUtils.getRootViews(true);
        Pair<String, View> selectedRootView = rootViews.get(0);
        String selectedName = selectedRootView.first;
        View selectedView = selectedRootView.second;
        //ViewHierarchyUtils.getWindowName(selectedView);

        String activityName = ViewHierarchyUtils.getActivityNameFromRootView(selectedView);

        try {
            selectedView.setDrawingCacheEnabled(true);
            selectedView.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(selectedView.getDrawingCache());
            selectedView.setDrawingCacheEnabled(false);

            //File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File folder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), SCREENSHOTS_DIR_NAME);
            if (!folder.exists())
                folder.mkdir();

            long mImageTime = System.currentTimeMillis();
            String imageDate = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(mImageTime));
            String fileName = String.format(SCREENSHOT_FILE_NAME_TEMPLATE, imageDate, "DT");
            File imageFile = new File(folder, fileName);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            //TODO: update MediaStore
            //ContentResolver cr = context.getContentResolver();
            //MediaStoreUtils.insertImage(cr, )

            Screen screen = new Screen();
            screen.setSession(0);
            screen.setRootViewName(selectedName);
            screen.setActivityName(activityName);
            screen.setDate(mImageTime);
            screen.setAbsolutePath(imageFile.getAbsolutePath());
            return screen;

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
        return null;
    }

    public void openFileExternally(String filePath) {
        File file = new File(filePath);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String type = mime.getMimeTypeFromExtension(extension);

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
}
