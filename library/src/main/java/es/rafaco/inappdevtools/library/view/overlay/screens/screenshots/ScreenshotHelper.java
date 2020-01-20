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

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.storage.db.entities.ScreenshotDao;
import es.rafaco.inappdevtools.library.storage.files.DevToolsFiles;
import es.rafaco.inappdevtools.library.storage.files.utils.MediaScannerUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.ScreenHelper;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.view.utils.ViewHierarchyUtils;

public class ScreenshotHelper extends ScreenHelper {

    private static final String SCREENSHOT_SHARE_SUBJECT_TEMPLATE = "Screenshot (%s)";


    @Override
    public String getReportPath() {
        //TODO: ThreadUtils.runOnBack(new Runnable() {
        ScreenshotDao screenshotDao = DevToolsDatabase.getInstance().screenshotDao();
        final Screenshot lastScreenshot = screenshotDao.getLast();

        if (lastScreenshot != null){
            return lastScreenshot.getPath();
        }else{
            return null;
        }
    }

    @Override
    public String getReportContent() {
        return null;
    }


    public Screenshot takeAndSaveScreen(){

        final Screenshot screenshot = takeScreenIntoFile(false);
        if (screenshot != null){
            Toast.makeText(context.getApplicationContext(), "Screenshot saved", Toast.LENGTH_LONG).show();
            ThreadUtils.runOnBack("Iadt-SaveScreenshot",
                    new Runnable() {
                @Override
                public void run() {
                    IadtController.get().getDatabase().screenshotDao().insertAll(screenshot);
                }
            });
        }
        return screenshot;
    }

    public Screenshot takeScreenIntoFile(boolean isFromCrash) {

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
                    DevToolsDatabase.getInstance().screenshotDao().count() + 1 ;
            File imageFile = DevToolsFiles.prepareScreen(fileId, isFromCrash);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 80;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            MediaScannerUtils.scan(imageFile);

            Screenshot screenshot = new Screenshot();
            screenshot.setSession(0);
            screenshot.setRootViewName(selectedName);
            screenshot.setActivityName(activityName);
            screenshot.setDate(new Date().getTime());
            screenshot.setPath(imageFile.getAbsolutePath());

            return screenshot;

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
