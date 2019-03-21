package es.rafaco.inappdevtools.library.logic.sources;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.sources.nodes.AbstractNode;
import es.rafaco.inappdevtools.library.logic.sources.nodes.AssetFileReader;
import es.rafaco.inappdevtools.library.logic.sources.nodes.NodesHelper;
import es.rafaco.inappdevtools.library.logic.sources.nodes.StandardNode;
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

        Collection<AbstractNode> children = parent.getChildren().values();
        return NodesHelper.castToSourceEntry(children);
    }

    public String getContent(String path){
        AbstractNode target = NodesHelper.getNodeByFullPath(root, path);
        if (!target.isDirectory()){
            return null;
        }

        InputStream inputStream;
        try {
            if (target instanceof ZipNode) {
                ZipFile zipfile = ((ZipNode)target).getZipFile();
                ZipEntry zipentry = ((ZipNode)target).getEntry();
                inputStream = zipfile.getInputStream(zipentry);
            }
            else{
                inputStream = context.getAssets().open(target.getPath());
            }
        }
        catch (IOException e) {
            FriendlyLog.logException("Exception", e);
            return null;
        }
        return readInputStream(inputStream);
    }

    @NotNull
    protected String readInputStream(InputStream inputStream) {
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

    public boolean canOpenClassName(String namespace) {
        String filename = namespace.substring(namespace.lastIndexOf("/"));
        String path = namespace.replace(".", "/");
        AbstractNode target = NodesHelper.getNodeByFullPath(root, path + filename);
        if (target != null){
            return true;
        }
        return false;
    }
}
