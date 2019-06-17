package es.rafaco.inappdevtools.library.view.overlay.screens.screenshots;

import android.graphics.Bitmap;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Screen;
import es.rafaco.inappdevtools.library.storage.db.entities.ScreenDao;
import es.rafaco.inappdevtools.library.storage.files.DevToolsFiles;
import es.rafaco.inappdevtools.library.storage.files.MediaScannerUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreenHelper;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.view.utils.ViewHierarchyUtils;

public class ScreenHelper extends OverlayScreenHelper {

    private static final String SCREENSHOT_SHARE_SUBJECT_TEMPLATE = "Screenshot (%s)";


    @Override
    public String getReportPath() {
        //TODO: ThreadUtils.runOnBackThread(new Runnable() {
        ScreenDao screenDao = DevToolsDatabase.getInstance().screenDao();
        final Screen lastScreen = screenDao.getLast();

        if (lastScreen != null){
            return lastScreen.getPath();
        }else{
            return null;
        }
    }

    @Override
    public String getReportContent() {
        return null;
    }


    public Screen takeAndSaveScreen(){

        final Screen screen = takeScreenIntoFile(false);
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

    public Screen takeScreenIntoFile(boolean isFromCrash) {

        List<Pair<String, View>> rootViews = ViewHierarchyUtils.getRootViews(false);
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

            long fileId = isFromCrash ?
                    DevToolsDatabase.getInstance().crashDao().count() :
                    DevToolsDatabase.getInstance().screenDao().count() + 1 ;
            File imageFile = DevToolsFiles.prepareScreen(fileId, isFromCrash);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 80;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            MediaScannerUtils.scan(imageFile);

            Screen screen = new Screen();
            screen.setSession(0);
            screen.setRootViewName(selectedName);
            screen.setActivityName(activityName);
            screen.setDate(new Date().getTime());
            screen.setPath(imageFile.getAbsolutePath());

            return screen;

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            FriendlyLog.logException("Exception", e);
        }
        return null;
    }

    private byte[] getByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }
}
