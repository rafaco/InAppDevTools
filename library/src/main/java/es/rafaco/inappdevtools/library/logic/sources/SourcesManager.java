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

import es.rafaco.inappdevtools.library.logic.sources.nodes.AbstractNode;
import es.rafaco.inappdevtools.library.logic.sources.nodes.NodesHelper;
import es.rafaco.inappdevtools.library.logic.sources.nodes.ZipNode;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class SourcesManager {

    public static final String ASSETS = "assets";

    Context context;
    AbstractNode root;

    public SourcesManager(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        root = NodesHelper.populate(context);
    }

    public List<SourceEntry> getFilteredItems(String filter){
        AbstractNode parent = NodesHelper.getNodeByFullPath(root, filter);
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

    public String getContent(String path){
        AbstractNode target = NodesHelper.getNodeByFullPath(root, path);
        if (target == null || target.isDirectory()){
            return null;
        }

        InputStream inputStream = getInputStream(target);
        return readToString(inputStream);
    }

    public File getLocalFile(String path){
        AbstractNode target = NodesHelper.getNodeByFullPath(root, path);
        if (target == null || target.isDirectory()){
            return null;
        }

        InputStream inputStream = getInputStream(target);
        return readToLocalFile(path, inputStream);
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

    public boolean canOpenClassName(String fullClassName) {
        String nodePath = getNodePathFromClassName(fullClassName);
        return !TextUtils.isEmpty(nodePath);
    }

    public String getNodePathFromClassName(String fullClassName){
        String filename = fullClassName.substring(fullClassName.lastIndexOf("/")+1);
        String path = fullClassName.substring(0, fullClassName.lastIndexOf("/")+1);
        path = path.replace(".", "/");

        String[] prefixs = new String[]{"src/", "gen/"};
        for (String prefix: prefixs){
            AbstractNode target = NodesHelper.getNodeByFullPath(root, prefix + path + filename);
            if (target != null){
                return target.getPath();
            }
        }
        return null;
    }
}
