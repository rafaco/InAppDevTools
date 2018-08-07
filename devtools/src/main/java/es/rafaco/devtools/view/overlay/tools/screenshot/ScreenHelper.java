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
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.db.errors.Screen;
import es.rafaco.devtools.db.errors.ScreenDao;
import es.rafaco.devtools.logic.PermissionActivity;
import es.rafaco.devtools.utils.FileUtils;
import es.rafaco.devtools.utils.ThreadUtils;
import es.rafaco.devtools.utils.ViewHierarchyUtils;

public class ScreenHelper {

    private static final String SCREENSHOTS_DIR_NAME = "Screenshots";
    private static final String SCREENSHOT_FILE_NAME_TEMPLATE = "Screenshot_%s_%s.jpg";
    private static final String SCREENSHOT_SHARE_SUBJECT_TEMPLATE = "Screenshot (%s)";

    private Context context;

    public ScreenHelper(Context context) {
        this.context = context;
    }

    public Screen takeAndSaveScreen(){
        final Screen screen = takeScreen();
        if (screen != null){
            Toast.makeText(context.getApplicationContext(), "Screen saved", Toast.LENGTH_LONG).show();
            ThreadUtils.runOnBackThread(new Runnable() {
                @Override
                public void run() {
                    DevTools.getDatabase().screenDao().insertAll(screen);
                }
            });
        }
        return screen;
    }
    public Screen takeScreen() {

        if (!PermissionActivity.isNeededWithAutoStart(context,
                PermissionActivity.IntentAction.STORAGE))
            return null;
        Log.d(DevTools.TAG, "isExternalStorageWritable?: " + FileUtils.isExternalStorageWritable());


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
            screen.setPath(imageFile.getAbsolutePath());

            return screen;

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
        return null;
    }

    public String buildReport(){

        //TODO: ThreadUtils.runOnBackThread(new Runnable() {
        ScreenDao screenDao = DevToolsDatabase.getInstance().screenDao();
        final Screen lastScreen = screenDao.getLast();

        if (lastScreen != null){
            return lastScreen.getPath();
        }else{
            return null;
        }
    }
}
