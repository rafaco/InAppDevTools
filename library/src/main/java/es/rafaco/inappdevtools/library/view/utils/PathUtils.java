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

package es.rafaco.inappdevtools.library.view.utils;

public class PathUtils {

    public static String removeLastSlash(String path) {
        if (path.lastIndexOf("/") == path.length()-1)
            return removeLastChar(path);
        return path;
    }

    public static String removeLastChar(String path) {
        return path.substring(0, path.length()-1);
    }

    public static String getFileNameWithExtension(String path){
        String[] parts = path.split("[/]");
        boolean isFile = !path.endsWith("/");
        return isFile ? (parts[parts.length-1]) : "";
    }

    public static String getFileExtension(String path){
        if (path.contains(".")){
            int lastFound = path.lastIndexOf(".");
            String extension = path.substring(lastFound + 1);
            return extension;
        }
        return "";
    }

    /**
     * Get the file name or folder name
     * @param path
     * @return
     */
    public static String getLastLevelName(String path){
        if(path ==null) return "";
        String[] parts = removeLastSlash(path).split("[/]");
        if (parts.length<1) return "";
        return parts[parts.length-1];
    }
}
