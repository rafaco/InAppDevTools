/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.view.components.items;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import es.rafaco.inappdevtools.library.view.components.base.FlexData;

public class ImageData extends FlexData {

    String imagePath;
    Drawable imageDrawable;
    Bitmap imageBitmap;

    String title;
    int height = -1;
    int icon;
    Runnable performer;

    public ImageData(String imagePath) {
        super();
        this.imagePath = imagePath;
    }

    public ImageData(Drawable imageDrawable) {
        super();
        this.imageDrawable = imageDrawable;
    }

    public ImageData(Bitmap imageBitmap) {
        super();
        this.imageBitmap = imageBitmap;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public Runnable getPerformer() {
        return performer;
    }

    public void setPerformer(Runnable performer) {
        this.performer = performer;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Drawable getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(Drawable imageDrawable) {
        this.imageDrawable = imageDrawable;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}
