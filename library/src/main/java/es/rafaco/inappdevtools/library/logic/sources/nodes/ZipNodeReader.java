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

package es.rafaco.inappdevtools.library.logic.sources.nodes;

import android.text.TextUtils;
import android.util.Log;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import es.rafaco.inappdevtools.library.Iadt;

import static es.rafaco.inappdevtools.library.logic.sources.nodes.AbstractNode.FOLDER_SEP;

/**
 * Helper class for {@link ZipNode#fromZipFile}.
 * It helps creating a tree of ZipNodes from a ZipFile.
 */
public class ZipNodeReader extends AbstractNodeReader {
    /**
     * The file to be read.
     */
    private ZipFile file;

    /**
     * creates a new ZipNodeReader from a ZipFile.
     */
    public ZipNodeReader(ZipFile f) {
        super();
        this.file = f;
        root = new StandardNode("root", "/", true);
    }

    public ZipNodeReader(ZipFile f, AbstractNodeReader previousReader, String prefix) {
        super(previousReader, prefix);
        this.file = f;
    }

    /**
     * reads all entries, creates the corresponding Nodes and
     * returns the root node.
     */
    public AbstractNode populate() {
        for(Enumeration<? extends ZipEntry> entries = file.entries();
            entries.hasMoreElements(); ) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().startsWith("META-INF/")){
                Log.w(Iadt.TAG, "Excluded: " + entry.getName());
                continue;
            }
            addEntry(entry);
        }
        return root;
    }

    /**
     * adds an entry to our tree.
     *
     * This may create a new ZipNode and then connects
     * it to its parent node.
     * @returns the ZipNode corresponding to the entry.
     */
    private AbstractNode addEntry(ZipEntry entry) {
        String name = entry.getName();
        AbstractNode node = collected.get(name);
        if(node != null) {
            // already in the map
            return node;
        }
        node = new ZipNode(file, entry, prefix);
        collected.put(name, node);
        findParent(node);
        return node;
    }

    protected AbstractNode createParentNode(String parentName) {
        if (parentName.indexOf(FOLDER_SEP)==parentName.lastIndexOf(FOLDER_SEP)){
            AbstractNode node = collected.get(parentName);
            if(node != null) {
                return node;
            }
        }

        if (!TextUtils.isEmpty(prefix) && parentName.startsWith(prefix)){
            parentName = parentName.substring(parentName.indexOf(FOLDER_SEP)+1);
        }
        ZipEntry entry = file.getEntry(parentName);
        if (entry==null){
            Log.e("TAG", "no entry for " + parentName);
            return null;
        }
        return addEntry(entry);
    }
}
