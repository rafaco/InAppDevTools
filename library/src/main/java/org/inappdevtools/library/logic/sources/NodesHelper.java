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

package org.inappdevtools.library.logic.sources;

import android.content.Context;
import android.text.TextUtils;

import org.inappdevtools.library.logic.sources.nodes.AbstractNode;
import org.inappdevtools.library.logic.sources.nodes.AbstractNodeReader;
import org.inappdevtools.library.logic.sources.nodes.AssetsNodeReader;
import org.inappdevtools.library.logic.sources.nodes.RootNodeReader;
import org.inappdevtools.library.logic.sources.nodes.ZipNodeReader;
import org.inappdevtools.library.storage.files.IadtPath;
import org.inappdevtools.library.storage.files.utils.AssetFileReader;
import org.inappdevtools.library.storage.files.utils.ReactNativeHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

public class NodesHelper {


    public static AbstractNode populate(Context context) {

        AssetFileReader fileReader = new AssetFileReader(context);
        AbstractNodeReader nodeReader = null;

        boolean isReactEnabled = new ReactNativeHelper().isEnabled();

        //Prepare first level
        nodeReader = new RootNodeReader();
        if (isReactEnabled) ((RootNodeReader) nodeReader).addNode(IadtPath.REACT_NATIVE_SOURCES);
        ((RootNodeReader) nodeReader).addNode(IadtPath.SOURCES);
        ((RootNodeReader) nodeReader).addNode(IadtPath.GENERATED);
        ((RootNodeReader) nodeReader).addNode(IadtPath.RESOURCES);
        ((RootNodeReader) nodeReader).addNode(IadtPath.ASSETS);

        //Populate ASSETS
        nodeReader = new AssetsNodeReader(context, nodeReader, IadtPath.ASSETS);
        AbstractNode root = nodeReader.populate();

        //Populate SRC and RES
        AbstractNode ourAssetsNode = getNodeByFullPath(root,
                IadtPath.ASSETS + "/" + IadtPath.SUBFOLDER + "/");
        for (AbstractNode node: ourAssetsNode.getChildren().values()) {
            String prefix = null;
            if (node.getName().endsWith(IadtPath.RESOURCES_TAIL)) {
                prefix = IadtPath.RESOURCES;
            }
            else if (node.getName().endsWith(IadtPath.SOURCES_TAIL)) {
                if (isReactEnabled && node.getName().endsWith(IadtPath.REACT_NATIVE_SOURCES_TAIL)){
                    prefix = IadtPath.REACT_NATIVE_SOURCES;
                }
                else {
                    prefix = IadtPath.SOURCES;
                }
            }
            else if (node.getName().endsWith(IadtPath.GENERATED_TAIL)) {
                prefix = IadtPath.GENERATED;
            }

            if (!TextUtils.isEmpty(prefix)){
                ZipFile file = fileReader.getZipFile(node.getPath());
                if (file == null){

                    continue;
                }
                nodeReader = new ZipNodeReader(file, nodeReader, prefix);
                root = nodeReader.populate();
            }
        }

        return root;
    }

    public static AbstractNode getNodeByFullPath(AbstractNode root, String fullPath){
        if (TextUtils.isEmpty(fullPath)){
            return root;
        }

        AbstractNode current = root;
        String[] parts = fullPath.split("[/]");
        boolean isFile = !fullPath.endsWith("/");
        String fileName = isFile ? (parts[parts.length-1]) : "";

        if (parts.length>0){
            for (String part: parts){
                Map<String, AbstractNode> children = current.getChildren();
                if (children == null || children.isEmpty()) return null;

                String childrenSelector = (isFile && part.equals(fileName)) ? part : part + "/";
                current = children.get(childrenSelector);
                if (current == null) return null;
            }
        }
        return current;
    }

    public static List<SourceEntry> castToSourceEntry(Collection<AbstractNode> nodes) {
        List<SourceEntry> entries = new ArrayList<>();
        for (AbstractNode node : nodes){
            entries.add(new SourceEntry("TODO", node.getPath(), node.isDirectory()));
        }
        return entries;
    }

    public static String getFileNameAndExtensionFromPath(String path){
        String[] parts = path.split("[/]");
        boolean isFile = !path.endsWith("/");
        return isFile ? (parts[parts.length-1]) : "";
    }

    public static String getFileExtensionFromPath(String path){
        if (path.contains(".")){
            int lastFound = path.lastIndexOf(".");
            String extension = path.substring(lastFound + 1);
            return extension;
        }
        return "";
    }
}
