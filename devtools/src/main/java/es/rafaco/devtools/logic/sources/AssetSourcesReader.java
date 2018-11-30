package es.rafaco.devtools.logic.sources;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import es.rafaco.devtools.storage.files.DevToolsFiles;


public class AssetSourcesReader {

    private final Context context;

    public AssetSourcesReader(Context context) {
        this.context = context;
    }


    public List<SourceEntry> populateItems(String origin) {
        AssetManager aMan = context.getApplicationContext().getAssets();

        List<String> categories = new ArrayList<>();
        try {
            categories =  Arrays.asList(aMan.list(""));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<SourceEntry> result = new ArrayList<>();
        for (String category: categories) {
            result.add(new SourceEntry(origin, category, true));

            List<String> resources = new ArrayList<>();
            try {
                resources =  Arrays.asList(aMan.list(category));
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (String resource: resources) {
                if (!isExcluded(resource))
                    result.add(new SourceEntry(origin, category + "/" + resource, false));
            }
        }
        return result;
    }

    private boolean isExcluded(String resource) {
        return false;
    }

    public static String extractContent(String entry){

        //TODO
        return "commming sooooon!";
    }
}
