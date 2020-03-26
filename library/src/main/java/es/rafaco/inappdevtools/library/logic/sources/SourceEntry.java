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

package es.rafaco.inappdevtools.library.logic.sources;

public class SourceEntry {

    String origin;
    String name;
    boolean isDirectory;

    public SourceEntry(String origin, String name, boolean isDirectory) {
        this.origin = origin;
        this.name = name;
        this.isDirectory = isDirectory;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getFileName() {
        int lastFound = name.lastIndexOf('/');
        return name.substring(lastFound + 1);
    }

    public int getDeepLevel(){
        if (name == null){
            return 0;
        }

        int slashCount = name.length() - name.replace("/", "").length();
        if (slashCount>0 && isDirectory){
            return slashCount - 1;
        }
        return slashCount;
    }
}
