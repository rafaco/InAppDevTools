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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.BuildConfig;
import es.rafaco.inappdevtools.library.logic.sources.nodes.AbstractNode;
import es.rafaco.inappdevtools.library.logic.sources.nodes.ZipNode;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class SourcesManager {

    public static final String ASSETS = "assets";

    Context context;
    AbstractNode root;
    private boolean initialized;

    public SourcesManager(Context context) {
        this.context = context;
        init();
    }

    public void init() {
        if (canSourceInspection()) {
            root = NodesHelper.populate(context);
            initialized = true;
        }
    }

    public boolean canSourceInspection() {
        if (IadtController.get().isEnabled()
                && IadtController.get().getConfig().getBoolean(BuildConfig.SOURCE_INSPECTION)){
            return true;
        }else{
            return false;
        }
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
            internalPath = internalPath.substring(0, internalPath.indexOf("$"));

        String[] prefixes = new String[]{"src/", "gen/"};
        for (String prefix: prefixes){
            AbstractNode candidate = NodesHelper.getNodeByFullPath(root, prefix + internalPath);
            if (candidate != null){
                return candidate.getPath();
            }
            candidate = NodesHelper.getNodeByFullPath(root, prefix + internalPath + ".java");
            if (candidate != null){
                return candidate.getPath();
            }
            candidate = NodesHelper.getNodeByFullPath(root, prefix + internalPath + ".kt");
            if (candidate != null){
                return candidate.getPath();
            }
        }
        return null;
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
                String realPath = obtainedPath.substring(obtainedPath.indexOf("/")+1);
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
        File f = new File(context.getCacheDir()+"/"+ path);
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
}
