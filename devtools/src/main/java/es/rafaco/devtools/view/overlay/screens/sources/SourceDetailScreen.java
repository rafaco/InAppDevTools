package es.rafaco.devtools.view.overlay.screens.sources;

import android.view.ViewGroup;
import android.widget.TextView;

import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.sources.SourcesManager;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.adapters.Format;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;

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
        return false;
    }

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onStart(ViewGroup view) {

        SourcesManager manager = new SourcesManager(getContext());

        codeHeader = view.findViewById(R.id.code_header);
        codeViewer = view.findViewById(R.id.code_view);

        codeHeader.setText(getParam());
        codeViewer.setOptions(Options.Default.get(getContext())
                .withLanguage("java")
                .withCode(manager.getContent(getParam()))
                .withTheme(ColorTheme.MONOKAI)
                .withFormat(Format.Default.getCompact()));
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }
}
