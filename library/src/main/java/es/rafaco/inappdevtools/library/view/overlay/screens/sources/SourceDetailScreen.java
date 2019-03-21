package es.rafaco.inappdevtools.library.view.overlay.screens.sources;

import android.os.AsyncTask;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.sources.SourceAdapter;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import io.github.kbiakov.codeview.CodeView;

public class SourceDetailScreen extends OverlayScreen {

    private TextView codeHeader;
    private CodeView codeViewer;

    public SourceDetailScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "SourceDetail";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_source_detail_body; }

    @Override
    public boolean needNestedScroll() {
        return true;
    }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup view) {

        codeHeader = bodyView.findViewById(R.id.code_header);
        codeViewer = bodyView.findViewById(R.id.code_view);

        codeViewer.setCode("");

        new AsyncTask<String, String, String>(){
            @Override
            protected String doInBackground(String... strings) {
                return DevTools.getSourcesManager().getContent(getParams().path);
            }

            @Override
            protected void onPostExecute(String content) {
                super.onPostExecute(content);
                codeHeader.setText(getParams().path);

                if (content == null){
                    codeViewer.setCode("Unable to get content");
                    return;
                }

                final SourceAdapter codeAdapter = new SourceAdapter(SourceDetailScreen.this, content, getLanguage(), getParams());
                codeViewer.post(new Runnable() {
                    @Override
                    public void run() {
                        codeViewer.setAdapter(codeAdapter);
                    }
                });
            }
        }.execute();
    }

    @NotNull
    private String getLanguage() {
        if (getParams().path.contains(".")){
            int lastFound = getParams().path.lastIndexOf(".");
            return getParams().path.substring(lastFound + 1);
        }
        return "";
    }

    @Override
    protected void onStop() {
        //Nothing needed
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }

    public static String buildParams(String type, String path, int lineNumber){
        InnerParams paramObject = new InnerParams(type, path, lineNumber);
        Gson gson = new Gson();
        return gson.toJson(paramObject);
    }

    public InnerParams getParams(){
        Gson gson = new Gson();
        return gson.fromJson(getParam(), InnerParams.class);
    }

    public static class InnerParams {
        public String type;
        public String path;
        public int lineNumber;

        public InnerParams(String type, String path, int lineNumber) {
            this.type = type;
            this.path = path;
            this.lineNumber = lineNumber;
        }
    }
}
