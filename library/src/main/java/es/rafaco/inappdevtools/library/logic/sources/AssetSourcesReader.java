package es.rafaco.inappdevtools.library.logic.sources;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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