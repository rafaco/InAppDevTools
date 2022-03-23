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

package org.inappdevtools.library.logic.sources.nodes;

import java.io.*;
import java.util.zip.*;

/**
 * Original ideas extracted from: https://stackoverflow.com/questions/5158961/unzip-into-treemap-in-java
 */
public class ZipNode extends AbstractNode{

    private final String prefix;
    /**
     * the corresponding Zip entry. If null, this is the root entry.
     */
    private ZipEntry entry;

    /** the ZipFile from where the nodes came. */
    private ZipFile file;


    protected ZipNode(ZipFile f, ZipEntry entry, String prefix) {
        super(entry == null || entry.isDirectory());
        this.file = f;
        this.entry = entry;
        this.prefix = prefix;
    }

    /**
     * returns the last component of the name of
     * the entry, i.e. the file name. If this is a directory node,
     * the name ends with '/'. If this is the root node, the name
     * is simply "/".
     */
    @Override
    public String getName() {
        if(entry == null)
            return "/";
        String longName = getPath();
        return longName.substring(longName.lastIndexOf(FOLDER_SEP,
                longName.length()-2)+1);
    }

    @Override
    public String getPath(){
        if (entry == null) {
            return FOLDER_SEP + "";
        }
        return prefix + "/" + entry.getName();
    }

    /**
     * gets the corresponding ZipEntry to this node.
     * @return {@code null} if this is the root node (which has no
     *    corresponding entry), else the corresponding ZipEntry.
     */
    public ZipEntry getEntry() {
        return entry;
    }

    /**
     * Gets the ZipFile, from where this ZipNode came.
     */
    public ZipFile getZipFile() {
        return file;
    }


    /**
     * opens an InputStream on this ZipNode. This only works when
     * this is not a directory node, and only before the corresponding
     * ZipFile is closed.
     */
    public InputStream openStream() throws IOException {
        return file.getInputStream(entry);
    }



    /**
     * creates a ZipNode tree from a ZipFile
     * and returns the root node.
     *
     * The nodes' {@link #openStream()} methods are only usable until the
     * ZipFile is closed, but the structure information remains valid.
     */
    public static AbstractNode fromZipFile(ZipFile zf) {
        return new ZipNodeReader(zf).populate();
    }




    /**
     * test method. Give name of zip file as command line argument.
     */
    public static void main(String[] params)
            throws IOException
    {
        if(params.length < 1) {
            System.err.println("Invocation:  java ZipNode zipFile.zip");
            return;
        }
        ZipFile file = new ZipFile(params[0]);
        ZipNode root = (ZipNode) ZipNode.fromZipFile(file);
        file.close();
        root.printTree("", " ", "");
    }

}
