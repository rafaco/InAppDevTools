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

package org.inappdevtools.library.logic.sources.nodes;

public class StandardNode extends AbstractNode{

    protected StandardNode(String name, String path, boolean isDirectory) {
        super(isDirectory);
        this.name = name;
        this.path = path;
    }

    @Override
    public String getName() {
        if(name == null)
            return "/";
        return name.substring(name.lastIndexOf(FOLDER_SEP,
                name.length()-2)+1);
    }

    public String getEntryName() {
        return name;
    }

    public String getPath(){
        if (name == null) {
            return FOLDER_SEP + "";
        }
        return path;
    }

    /*
    public static StandardNode fromAssets(Context context) {
        return (StandardNode) new AssetsNodeReader(context, "assets").populate();
    }*/
}
