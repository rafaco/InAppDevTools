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

package org.inappdevtools.library.view.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;

public class ImageLoaderAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private final ImageView imageView;
    private final Runnable callback;

    public ImageLoaderAsyncTask(ImageView view) {
        this(view, null);
    }

    public ImageLoaderAsyncTask(ImageView view, Runnable callback) {
        super();
        imageView = view;
        this.callback = callback;
    }

    @Override
    protected Bitmap doInBackground(String... paths) {
        File imgFile = new  File(paths[0]);
        if(imgFile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            Point screenDimensions = UiUtils.getDisplaySize(imageView.getContext());
            int sizeX = (screenDimensions.x /2) - 54;

            return getProportionalBitmap(bitmap, sizeX, "X");
        }
        return null;
    }

    public Bitmap getProportionalBitmap(Bitmap bitmap,
                                        int newDimensionXorY,
                                        String xory) {
        if (bitmap == null)
            return null;

        float xyRatio;
        int newWidth;
        int newHeight;

        if (xory.equalsIgnoreCase("x")) {
            xyRatio = (float) newDimensionXorY / bitmap.getWidth();
            newHeight = (int) (bitmap.getHeight() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, newDimensionXorY, newHeight, true);
        }
        else if (xory.equalsIgnoreCase("y")) {
            xyRatio = (float) newDimensionXorY / bitmap.getHeight();
            newWidth = (int) (bitmap.getWidth() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, newWidth, newDimensionXorY, true);
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result){
        if (result != null){
            imageView.setImageBitmap(result);
            if (callback!=null){
                callback.run();
            }
            imageView.requestLayout();
        }
    }
}

