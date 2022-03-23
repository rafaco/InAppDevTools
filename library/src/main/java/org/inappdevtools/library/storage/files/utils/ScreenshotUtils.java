/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.inappdevtools.library.storage.files.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Pair;
import android.view.View;

import com.jraska.falcon.Falcon;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.logic.log.FriendlyLog;
import org.inappdevtools.library.logic.session.ActivityTracker;
import org.inappdevtools.library.logic.utils.ThreadUtils;
import org.inappdevtools.library.storage.db.IadtDatabase;
import org.inappdevtools.library.storage.db.entities.Screenshot;
import org.inappdevtools.library.view.overlay.layers.ScreenLayer;
import org.inappdevtools.library.view.utils.ViewHierarchyUtils;

public class ScreenshotUtils {

    private ScreenshotUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Screenshot takeAndSave(boolean isFromCrash) {
        final Screenshot screenshot = take(isFromCrash);
        saveDatabaseEntry(screenshot);
        return screenshot;
    }

    public static Screenshot take(boolean isFromCrash) {
        String subfolder;
        String filename;
        if (isFromCrash){
            long sessionId = getDb().sessionDao().count();
            subfolder = "session/" + sessionId;
            long fileId = getDb().crashDao().count();
            filename = "crash_" + fileId + "_screenshot";
        }
        else{
            long sessionId = getDb().sessionDao().count();
            subfolder = "session/" + sessionId;
            long fileId = getDb().screenshotDao().count() + 1L ;
            filename = "screenshot_" + fileId;
        }

        return grabAndSaveFile(subfolder, filename);
    }

    private static IadtDatabase getDb() {
        return IadtDatabase.get();
    }

    private static Screenshot grabAndSaveFile(String subfolder, String filename) {

        ActivityTracker activityTracker = IadtController.get().getActivityTracker();
        File imageFile = FileCreator.prepare(subfolder, filename + ".jpg");

        try {
            //Take with Falcon but save our way (more compression)
            Bitmap bitmap = Falcon.takeScreenshotBitmap(activityTracker.getCurrent());
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 80;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            MediaScannerUtils.scan(imageFile);

            return fillDatabaseObject(activityTracker.getCurrentName(), imageFile.getAbsolutePath());

        } catch (Exception e) {
            if (IadtController.get().isDebug()){
                // Several error may come out with file handling or DOM
                FriendlyLog.logException("Exception", e);
            }
        }
        return null;
    }

    public static Screenshot takeAndSaveOverlay() {
        long sessionId = getDb().sessionDao().count();
        String subfolder = "session/" + sessionId;
        long fileId = getDb().screenshotDao().count() + 1L ;
        String filename = "screen_" + fileId;

        Screenshot screenshot = grabAndSaveOverlay(subfolder, filename);
        saveDatabaseEntry(screenshot);
        return screenshot;
    }

    private static Screenshot grabAndSaveOverlay(String subfolder, String filename) {
        Pair<String, View> selectedRootView = ViewHierarchyUtils.getLayerRootView(ScreenLayer.class);
        if (selectedRootView == null){
            return null;
        }

        String selectedName = (String) selectedRootView.second.getTag();
        View selectedView = selectedRootView.second;

        String screenName = IadtController.get().getNavigationManager()
                .getCurrent().getStringClassName();

        try {
            Bitmap bitmap = Bitmap.createBitmap(selectedView.getWidth(), selectedView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            selectedView.draw(c);

            File imageFile = FileCreator.prepare(subfolder, filename + ".png");
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 0 ;/* ignored for PNG */
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            MediaScannerUtils.scan(imageFile);

            return fillDatabaseObject(screenName, imageFile.getAbsolutePath());
        } catch (Exception e) {
            // Several error may come out with file handling or DOM
            FriendlyLog.logException("Exception", e);
        }
        return null;
    }

    public static Bitmap getBitmap(boolean fullSize){
        Activity currentActivity = IadtController.get().getActivityTracker().getCurrent();
        View targetView = currentActivity.getWindow().getDecorView().findViewById(android.R.id.content).getRootView();
        Bitmap bitmap = null;
        try {
            if (fullSize){
                bitmap = getBitmapFromView(targetView);
                //TODO: Falcon grab our OverlayUI also
                //bitmap = Falcon.takeScreenshotBitmap(currentActivity);
            }
            else{
                bitmap = getThumbnailFromView(targetView);
            }
        }
        catch (Exception e) {
            if (IadtController.get().isDebug()){
                FriendlyLog.logException("Exception on getBitmap()", e);
            }
        }
        return bitmap;
    }

    public static Bitmap getThumbnailFromView(View view){
        int width = 300;
        int height = view.getHeight()*width/view.getWidth();
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createScaledBitmap(view.getDrawingCache(), width, height, true);
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static Bitmap getBitmapFromView(View view){
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private static Screenshot fillDatabaseObject(String activityName, String imageFilePath) {
        long currentSessionId = IadtController.get().getSessionManager().getCurrentUid();
        Screenshot screenshot = new Screenshot();
        screenshot.setSessionId(currentSessionId);
        screenshot.setActivityName(activityName);
        screenshot.setDate(new Date().getTime());
        screenshot.setPath(imageFilePath);

        return screenshot;
    }

    private static void saveDatabaseEntry(final Screenshot screenshot) {
        if (screenshot != null) {
            ThreadUtils.runOnBack("Iadt-SaveScreenshot",
                    new Runnable() {
                        @Override
                        public void run() {
                            IadtDatabase.get().screenshotDao().insert(screenshot);
                        }
                    });
        }
    }
}
