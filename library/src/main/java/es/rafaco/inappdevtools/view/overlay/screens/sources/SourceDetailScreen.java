package es.rafaco.inappdevtools.view.overlay.screens.sources;

import android.os.AsyncTask;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import es.rafaco.inappdevtools.DevTools;
import es.rafaco.inappdevtools.R;
import es.rafaco.inappdevtools.logic.sources.SourceAdapter;
import es.rafaco.inappdevtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.view.overlay.screens.OverlayScreen;
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
    }

    @Override
    protected void onStart(ViewGroup view) {

        codeHeader = bodyView.findViewById(R.id.code_header);
        codeViewer = bodyView.findViewById(R.id.code_view);
        codeViewer.setCode("");

        new AsyncTask<String, String, String>(){

            @Override
            protected String doInBackground(String... strings) {
                String content = DevTools.getSourcesManager().getContent(getParams().type, getParams().path);
                return content;
            }

            @Override
            protected void onPostExecute(String content) {
                super.onPostExecute(content);
                codeHeader.setText(getParams().path);

                final SourceAdapter codeAdapter = new SourceAdapter(getContext(), content, "java");
                codeViewer.post(new Runnable() {
                    @Override
                    public void run() {
                        codeViewer.setAdapter(codeAdapter);
                    }
                });
            }
        }.execute();
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
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
