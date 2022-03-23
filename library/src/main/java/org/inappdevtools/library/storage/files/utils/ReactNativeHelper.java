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

import android.content.Context;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.storage.files.IadtPath;

public class ReactNativeHelper {

    static JsonHelper helper;

    public ReactNativeHelper() {
        if (helper == null){
            helper = buildJsonHelper();
        }
    }

    private static JsonHelper buildJsonHelper() {
        AssetFileReader reader = new AssetFileReader(getContext());
        if (!reader.exists(IadtPath.REACT_NATIVE)){
            return null;
        }
        String fileContents = reader.getFileContents(IadtPath.REACT_NATIVE);
        return new JsonHelper(fileContents);
    }

    private static Context getContext(){
        return IadtController.get().getContext();
    }

    public boolean isEnabled() {
        if (helper == null) return false;
        return helper.getBoolean("enabled");
    }

    public String getVersion() {
        if (helper == null) return "";
        return helper.getString("version");
    }
}
