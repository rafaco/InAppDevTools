package es.rafaco.inappdevtools.library.view.overlay.screens.sources;

import android.os.AsyncTask;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.sources.NodesHelper;
import es.rafaco.inappdevtools.library.view.components.codeview.Language;
import es.rafaco.inappdevtools.library.view.components.codeview.Theme;

public class FillSourceAsyncTask extends AsyncTask<String, String, String> {

    private final SourceDetailScreen screen;
    private String path;
    private int lineNumber;

    public FillSourceAsyncTask() {
        throw new UnsupportedOperationException("You have to provide a SourceDetailScreen");
    }

    public FillSourceAsyncTask(SourceDetailScreen screen) {
        this.screen = screen;
    }

    @Override
    protected String doInBackground(String... strings) {
        path = strings[0];
        lineNumber = Integer.parseInt(strings[1]);
        return IadtController.get().getSourcesManager().getContent(path);
    }

    @Override
    protected void onPostExecute(final String content) {
        super.onPostExecute(content);

        if (content == null) {
            screen.codeViewer.setCode("Unable to get content");
            return;
        }

        screen.codeViewer.post(new Runnable() {
            @Override
            public void run() {
                screen.codeViewer.setOnHighlightListener(screen)
                        .setTheme(Theme.ANDROIDSTUDIO)
                        .setFontSize(12)
                        .setShowLineNumber(true)
                        .setWrapLine(false)
                        .setZoomEnabled(false)
                        .setCode(content)
                        .setLanguage(parseLanguage());

                if (lineNumber > 0) {
                    screen.codeViewer.highlightLineNumber(lineNumber);

                    int scrollLine = lineNumber - 10;
                    if (scrollLine < 1) scrollLine = 1;

                    final int finalScrollLine = scrollLine;
                    screen.codeViewer.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            screen.codeViewer.scrollToLine(finalScrollLine);
                        }
                    });
                }
                screen.codeViewer.apply();
            }
        });

        screen.tuneSelection = new boolean[]{true, false};
    }

    public Language parseLanguage() {
        String extension = NodesHelper.getFileExtensionFromPath(path);
        Language language = Language.getLanguageByName(extension.toLowerCase());
        if (language != null){
            return language;
        }
        return Language.AUTO;
    }
}