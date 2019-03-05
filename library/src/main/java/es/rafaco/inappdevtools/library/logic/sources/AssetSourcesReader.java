package es.rafaco.inappdevtools.library.logic.sources;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;


public class AssetSourcesReader extends SourcesReader{

    public AssetSourcesReader(Context context) {
        super(context);
    }

    @Override
    public ZipFile getFile(String target) {
        return null;
    }

    @Override
    public List<SourceEntry> getSourceEntries(String origin, ZipFile localZip) {
        AssetManager aMan = context.getApplicationContext().getAssets();

        List<String> categories = new ArrayList<>();
        try {
            categories =  Arrays.asList(aMan.list(""));
        } catch (IOException e) {
            FriendlyLog.logException("Exception", e);
        }

        List<SourceEntry> result = new ArrayList<>();
        for (String category: categories) {
            result.add(new SourceEntry(origin, category, true));

            List<String> resources = new ArrayList<>();
            try {
                resources =  Arrays.asList(aMan.list(category));
            } catch (IOException e) {
                FriendlyLog.logException("Exception", e);
            }

            for (String resource: resources) {
                if (!isExcluded(resource))
                    result.add(new SourceEntry(origin, category + "/" + resource, false));
            }
        }
        return result;
    }

    private boolean isExcluded(String resource) {
        //TODO: detect binary files
        return false;
    }

    protected InputStream getInputStream(ZipFile zip, String entryName) throws IOException {
        return context.getAssets().open(entryName);
    }
}
