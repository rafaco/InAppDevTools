/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.view.overlay.screens.sources;

import android.os.AsyncTask;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import br.tiagohm.Theme;
import br.tiagohm.Language;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.sources.NodesHelper;

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
