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

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.BuildConfigField;
import es.rafaco.inappdevtools.library.logic.sources.nodes.AbstractNode;
import es.rafaco.inappdevtools.library.logic.sources.nodes.ZipNode;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.files.IadtPath;
import es.rafaco.inappdevtools.library.view.utils.PathUtils;

public class SourcesManager {

    Context context;
    AbstractNode root;

    public SourcesManager(Context context) {
        this.context = context;
        init();
    }

    public void init() {
        if (canSourceInspection()) {
            root = NodesHelper.populate(context);
        }
    }

    public boolean canSourceInspection() {
        return IadtController.get().isEnabled()
                && IadtController.get().getConfig().getBoolean(BuildConfigField.SOURCE_INSPECTION);
    }


    public String getContent(String path){
        if (!canSourceInspection())
            return null;

        AbstractNode target = NodesHelper.getNodeByFullPath(root, path);
        if (target == null || target.isDirectory()){
            return null;
        }

        InputStream inputStream = getInputStream(target);
        return readToString(inputStream);
    }

    public File getLocalFile(String path){
        if (!canSourceInspection())
            return null;

        AbstractNode target = NodesHelper.getNodeByFullPath(root, path);
        if (target == null || target.isDirectory()){
            return null;
        }

        InputStream inputStream = getInputStream(target);
        return readToLocalFile(path, inputStream);
    }

    public List<SourceEntry> getSearchItems(String searchText){
        if (!canSourceInspection())
            return new ArrayList<>();

        List<AbstractNode> results = root.filterFilesName(searchText);
        if (results == null){
            return new ArrayList<>();
        }

        return NodesHelper.castToSourceEntry(results);
    }

    public List<SourceEntry> getChildItems(String parentPath){
        if (!canSourceInspection())
            return new ArrayList<>();

        AbstractNode parent = NodesHelper.getNodeByFullPath(root, parentPath);
        if (parent == null){
            return new ArrayList<>();
        }

        List<AbstractNode> children = new ArrayList<>(parent.getChildren().values());
        for (int i = 0 ; i<children.size() ; i++){
            children.set(i, flattenEmptyFolders(children.get(i)));
        }

        return NodesHelper.castToSourceEntry(children);
    }


    private AbstractNode flattenEmptyFolders(AbstractNode current) {
        if (current.getChildren().size() == 1){
            AbstractNode onlyChild = current.getChildren().values().iterator().next();
            if (onlyChild.isDirectory()){
                return flattenEmptyFolders(onlyChild);
            }
        }
        return current;
    }

    private InputStream getInputStream(AbstractNode target) {
        InputStream inputStream;
        try {
            if (target instanceof ZipNode) {
                ZipFile zipfile = ((ZipNode)target).getZipFile();
                ZipEntry zipentry = ((ZipNode)target).getEntry();
                inputStream = zipfile.getInputStream(zipentry);
            }
            else{
                String obtainedPath = target.getPath();
                String realPath = obtainedPath.substring(obtainedPath.indexOf('/')+1);
                inputStream = context.getAssets().open(realPath);
            }
        }
        catch (IOException e) {
            FriendlyLog.logException("Exception", e);
            return null;
        }
        return inputStream;
    }

    private String readToString(InputStream inputStream) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            for (String line; (line = r.readLine()) != null; ) {
                builder.append(line).append('\n');
            }
        } catch (IOException e) {
            FriendlyLog.logException("Exception reading content of file", e);
        }

        return builder.toString();
    }

    private File readToLocalFile(String path, InputStream inputStream) {
        if (inputStream == null){
            return null;
        }
        File f = new File(PathUtils.join(context.getCacheDir().toString(), path));
        if (!f.exists()){
            FileOutputStream fos = null;
            try {
                f.getParentFile().mkdirs();
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();

                fos = new FileOutputStream(f);
                fos.write(buffer);
            } catch (Exception e) {
                FriendlyLog.logException("SourceReader exception", e);
            }finally {
                if (fos!=null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        FriendlyLog.logException("Exception", e);
                    }
                }
            }
        }
        return f;
    }


    //region [ SEARCH SOURCES ]

    public boolean canOpenClassName(String fullClassName) {
        String nodePath = getPathFromClassName(fullClassName);
        return !TextUtils.isEmpty(nodePath);
    }

    /**
     * Get the internal path to the source file of a class, that can be used to request the file to
     * SourceManager, or null if not found or !canSourceInspection
     *
     * @param className fully qualified name of the class (output string of calling getClassName()
     *                  in the class)
     * @return          the internal path to the source to request
     */
    public String getPathFromClassName(String className){
        if (!canSourceInspection())
            return null;

        String internalPath = className.replace(".", "/");
        if (internalPath.contains("$"))
            internalPath = internalPath.substring(0, internalPath.indexOf('$'));

        String[] prefixes = new String[]{ IadtPath.SOURCES, IadtPath.GENERATED };
        for (String prefix: prefixes){
            AbstractNode candidate = NodesHelper.getNodeByFullPath(root, prefix + "/" + internalPath);
            if (candidate != null){
                return candidate.getPath();
            }
            candidate = NodesHelper.getNodeByFullPath(root, prefix + "/" + internalPath + ".java");
            if (candidate != null){
                return candidate.getPath();
            }
            candidate = NodesHelper.getNodeByFullPath(root, prefix + "/" + internalPath + ".kt");
            if (candidate != null){
                return candidate.getPath();
            }
        }
        return null;
    }

    public String getLayoutNameFromClassName(String pathToActivitySource) {
        String content = IadtController.get().getSourcesManager().getContent(pathToActivitySource);
        if (TextUtils.isEmpty(content))
            return "";

        //Detect layout included using setContentView (activities)
        String layoutName = getFirstMatch(content, "setContentView\\(R\\.layout\\.(\\w+)\\)");
        if (!TextUtils.isEmpty(layoutName))
            return layoutName;

        //Detect layout used with inflate (fragments and programmatic views)
        layoutName = getFirstMatch(content, "inflate\\(R\\.layout\\.(\\w+),");
        if (!TextUtils.isEmpty(layoutName))
            return layoutName;

        //Fallback to any R.layout in source file
        layoutName = getFirstMatch(content, "R\\.layout\\.(\\w+)[ ,;)]");
        if (!TextUtils.isEmpty(layoutName))
            return layoutName;

        return "";
    }

    private String getFirstMatch(String content, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public String getLayoutPathFromLayoutName(String layoutName) {
        if (TextUtils.isEmpty(layoutName))
            return "";

        return IadtPath.RESOURCES + "/" + "layout" + "/" + layoutName + ".xml";
    }

    //endregion
}
