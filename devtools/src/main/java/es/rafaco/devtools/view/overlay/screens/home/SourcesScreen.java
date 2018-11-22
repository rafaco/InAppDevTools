package es.rafaco.devtools.view.overlay.screens.home;

import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import es.rafaco.devtools.R;
import es.rafaco.devtools.storage.files.DevToolsFiles;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.highlight.ColorTheme;

public class SourcesScreen extends OverlayScreen {

    private CodeView codeViewer;

    public SourcesScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Sources";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_sources_body; }

    @Override
    protected void onCreate() {
    }
    @Override
    protected void onStart(ViewGroup view) {

        codeViewer = (CodeView) view.findViewById(R.id.code_view);

        File file = extractFromResources();

        JarFile jar = null;
        try {
            jar = new JarFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Getting the files into the jar
        Enumeration<? extends JarEntry> enumeration = jar.entries();

        // Iterates into the files in the jar file
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = enumeration.nextElement();

            // Is this a class?
            if (entry.getName().endsWith("SampleApp.java")) {
                loadCode(jar, entry);
                return;
            }
        }
    }

    private void loadCode(JarFile jar, ZipEntry entry){
        StringBuilder codeStringBuilder = new StringBuilder();
        try {
            InputStream inputStream = jar.getInputStream(entry);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            for (String line; (line = r.readLine()) != null; ) {
                codeStringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        codeViewer.setCode("");
        codeViewer.getOptions()
                .withCode(codeStringBuilder.toString())
                .withLanguage("java")
                .withTheme(ColorTheme.MONOKAI);
        //codeViewer.setCode(codeStringBuilder.toString());
    }

    private File extractFromResources() {
        File file = DevToolsFiles.prepareSources();

        int resId = getContext().getResources().getIdentifier(
                "app_sources", "raw",
                getContext().getPackageName());
        InputStream input = getContext().getResources().openRawResource(resId);

        try {
            OutputStream output = new FileOutputStream(file);
            try {
                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                int read;

                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }

                output.flush();
            } finally {
                output.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }


    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }
}
