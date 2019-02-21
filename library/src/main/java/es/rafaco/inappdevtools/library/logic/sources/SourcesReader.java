package es.rafaco.inappdevtools.library.logic.sources;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public abstract class SourcesReader {

    protected final Context context;

    public SourcesReader(Context context) {
        this.context = context;
    }

    public abstract ZipFile getFile(String target);
    public abstract List<SourceEntry> getSourceEntries(String originName, ZipFile localZip);

    public File getLocalFile(String target ){
        File f = new File(context.getCacheDir()+"/"+ target);
        if (!f.exists()){
            try {
                f.getParentFile().mkdirs();
                InputStream is = context.getAssets().open(target);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                FileOutputStream fos = new FileOutputStream(f);
                fos.write(buffer);
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return f;
    }

    public static String extractContent(ZipFile zip, ZipEntry entry){
        StringBuilder codeStringBuilder = new StringBuilder();
        try {
            InputStream inputStream = zip.getInputStream(entry);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            for (String line; (line = r.readLine()) != null; ) {
                codeStringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return codeStringBuilder.toString();
    }
}
