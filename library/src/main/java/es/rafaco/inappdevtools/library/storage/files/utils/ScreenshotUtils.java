/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.storage.files.utils;

import android.graphics.Bitmap;
import android.util.Pair;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.view.utils.ViewHierarchyUtils;

public class ScreenshotUtils {

    public static Screenshot takeAndSave(boolean isFromCrash) {
        final Screenshot screenshot = take(isFromCrash);
        saveDatabaseEntry(screenshot);
        return screenshot;
    }

    public static Screenshot take(boolean isFromCrash) {
        String subfolder;
        String filename;

        if (isFromCrash){
            long sessionId = DevToolsDatabase.getInstance().sessionDao().count();
            subfolder = "session/" + sessionId;
            long fileId = DevToolsDatabase.getInstance().crashDao().count();
            filename = "crash_" + fileId + "_screenshot";
        }
        else{
            long sessionId = DevToolsDatabase.getInstance().sessionDao().count();
            subfolder = "session/" + sessionId;
            long fileId = DevToolsDatabase.getInstance().screenshotDao().count() + 1 ;
            filename = "screenshot_" + fileId;
        }
        return grabAndSaveFile(subfolder, filename);
    }

    private static Screenshot grabAndSaveFile(String subfolder, String filename) {
        Pair<String, View> selectedRootView = ViewHierarchyUtils.getAppRootView();
        if (selectedRootView == null){
            return null;
        }

        String selectedName = selectedRootView.first;
        View selectedView = selectedRootView.second;
        //ViewHierarchyUtils.getWindowName(selectedView);

        String activityName = ViewHierarchyUtils.getActivityNameFromRootView(selectedView);

        try {
            selectedView.setDrawingCacheEnabled(true);
            selectedView.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(selectedView.getDrawingCache());
            selectedView.setDrawingCacheEnabled(false);

            File imageFile = FileCreator.prepare(subfolder, filename + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 80;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            MediaScannerUtils.scan(imageFile);

            return fillDatabaseObject(selectedName, activityName, imageFile.getAbsolutePath());

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            FriendlyLog.logException("Exception", e);
        }
        return null;
    }

    private static Screenshot fillDatabaseObject(String selectedName, String activityName, String imageFilePath) {
        long currentSessionId = IadtController.get().getSessionManager().getCurrent().getUid();
        Screenshot screenshot = new Screenshot();
        screenshot.setSessionId(currentSessionId);
        screenshot.setRootViewName(selectedName);
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
                            IadtController.get().getDatabase().screenshotDao().insert(screenshot);
                        }
                    });
        }
    }
}
