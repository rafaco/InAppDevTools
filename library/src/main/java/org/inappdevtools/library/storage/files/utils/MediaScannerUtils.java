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

package org.inappdevtools.library.storage.files.utils;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;
import java.io.File;

import org.inappdevtools.library.IadtController;

public class MediaScannerUtils {

    public static void scan(File file) {
        scan(file, null);
    }

    public static void scan(File file, MediaScannerConnection.OnScanCompletedListener callback) {
        String[] paths =  new String[]{file.toString()};
        scan(paths, callback);
    }

    // Tell the media scanner about the new file so that it is immediately available to the user.
    public static void scan(String[] paths, MediaScannerConnection.OnScanCompletedListener callback) {
        if (IadtController.get().isDebug() && callback == null){
            callback = new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Log.v("ExternalStorage", "Scanned " + path + " -> uri=" + uri);
                }
            };
        }
        MediaScannerConnection.scanFile(
                IadtController.get().getContext(),
                paths,
                null,
                callback);
    }
}
