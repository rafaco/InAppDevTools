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

import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.appcompat.widget.SearchView;
//#else
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
//#endif

import com.google.gson.Gson;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import br.tiagohm.CodeView;
import br.tiagohm.Language;
import br.tiagohm.Theme;
import es.rafaco.compat.AppCompatButton;
import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.utils.ClipboardUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.storage.files.utils.FileProviderUtils;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.ToolbarSearchHelper;

public class SourceDetailScreen extends Screen implements CodeView.OnHighlightListener {

    public static final String SET_FIND_IS_UP = "setFindIsUp";

    enum OriginEnum { SOURCE, TRACE, INTERNAL}

    CodeView codeViewer;
    boolean[] tuneSelection;
    private boolean isSourceUnavailable;
    RelativeLayout traceContainer;
    TextView traceLabel;
    AppCompatButton prevButton;
    AppCompatButton nextButton;
    TextView wideLabel;

    public SourceDetailScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "SourceDetail";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_source_detail_body; }


    @Override
    public int getToolbarLayoutId() {
        return R.menu.source;
    }

    @Override
    public boolean needNestedScroll() {
        return false;
    }

    @Override
    protected void onCreate() {
        //Nothing to do
    }

    @Override
    protected void onStart(ViewGroup view) {
        traceContainer = bodyView.findViewById(R.id.trace_container);
        prevButton = bodyView.findViewById(R.id.prev_trace);
        traceLabel = bodyView.findViewById(R.id.trace_label);
        wideLabel = bodyView.findViewById(R.id.wide_label);
        nextButton = bodyView.findViewById(R.id.next_trace);
        codeViewer = bodyView.findViewById(R.id.code_view);

        initTuneSelection();
        loadFromParams();
    }

    @Override
    protected void onStop() {
        //Nothing needed
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }


    //region [ LOADS FROM ASYNC TASK ]

    private void loadFromParams() {
        loadScreenTitle();
        loadStandardHeader();

        if (getParams().origin.equals(SourceDetailScreen.OriginEnum.TRACE)){
            new FillTraceNavigationHeaderAsyncTask(this).execute();
        }else{
            removeTraceHeader();
        }

        new FillCodeAsyncTask(this).execute();
    }

    private void loadScreenTitle() {
        switch (getParams().origin){
            case SOURCE:
                getScreenManager().setTitle("Source Detail");
                break;
            case TRACE:
                getScreenManager().setTitle("Stacktrace");
                break;
            case INTERNAL:
                getScreenManager().setTitle("Internal");
                break;
        }
    }

    protected void update(String newParams) {
        getScreenManager().updateCurrentStepParams(newParams);
        loadFromParams();
    }

    protected void loadCodeViewEmpty() {
        final String noSources = Humanizer.fullStop() + "Source code not available." + Humanizer.fullStop();
        codeViewer.post(new Runnable() {
            @Override
            public void run() {
                setSourceUnavailable(true);
                codeViewer.setTheme(Theme.ANDROIDSTUDIO)
                        .setFontSize(12)
                        .setShowLineNumber(false)
                        .setWrapLine(false)
                        .setZoomEnabled(false)
                        .setCode(noSources)
                        .setLanguage(Language.PLAINTEXT)
                        .apply();
            }
        });
    }

    protected void loadCodeViewContent(final String content, final String contentPath, final int lineNumber) {
        codeViewer.post(new Runnable() {
            @Override
            public void run() {
                setSourceUnavailable(false);
                codeViewer.setOnHighlightListener(SourceDetailScreen.this)
                        .setTheme(Theme.ANDROIDSTUDIO)
                        .setFontSize(12)
                        .setShowLineNumber(isShowLineNumber())
                        .setWrapLine(isWrapLine())
                        .setZoomEnabled(false)
                        .setCode(content)
                        .setLanguage(Language.parseFromPathExtension(contentPath));

                if (lineNumber > 0) {
                    codeViewer.highlightLineNumber(lineNumber);

                    int scrollLine = lineNumber - 10;
                    if (scrollLine < 1) scrollLine = 1;
                    final int finalScrollLine = scrollLine;
                    codeViewer.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            codeViewer.scrollToLine(finalScrollLine);
                        }
                    });
                }
                codeViewer.apply();
            }
        });
    }

    private void loadStandardHeader() {
        removeTraceHeader();
        String message = getParams().path;
        if (getParams().lineNumber > 0) {
            message += Humanizer.newLine() + "Line: " + getParams().lineNumber;
        }
        wideLabel.setText(message);
    }

    protected void loadTraceNavigationHeader(ArrayList<Sourcetrace> groupTraces, int currentPosition) {
        Sourcetrace currentTrace = groupTraces.get(currentPosition);
        int totalTraces = groupTraces.size();

        String traceMessage = Humanizer.toCapitalCase(currentTrace.getExtra()) + " trace ";
        String positionMessage = currentPosition + "/" + totalTraces;
        String fileMessage = "File " + currentTrace.getFileName();
        String lineMessage = "Line " + currentTrace.getLineNumber();
        String codeMessage = currentTrace.getClassName() + "." + currentTrace.getMethodName() + "()";

        traceLabel.setText(traceMessage + positionMessage + Humanizer.newLine()
                + fileMessage + Humanizer.newLine()
                + lineMessage);
        wideLabel.setText(codeMessage);

        if (currentPosition > 1){
            prevButton.setAlpha(1f);
            final long previousTrace = groupTraces.get(currentPosition - 2).getUid();
            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    update(SourceDetailScreen.buildTraceParams(previousTrace));
                }
            });
        }
        else{
            prevButton.setAlpha(0.5f);
            prevButton.setOnClickListener(null);
        }

        if (currentPosition < totalTraces){
            nextButton.setAlpha(1f);
            final long nextTrace = groupTraces.get(currentPosition).getUid();
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    update(SourceDetailScreen.buildTraceParams(nextTrace));
                }
            });
        }
        else{
            nextButton.setAlpha(0.5f);
            nextButton.setOnClickListener(null);
        }

        traceContainer.setVisibility(View.VISIBLE);
    }

    protected void removeTraceHeader() {
        traceContainer.setVisibility(View.GONE);
    }

    public void setSourceUnavailable(boolean notAvailable) {
        isSourceUnavailable = notAvailable;
    }

    //endregion

    //region [ CodeView listener ]

    @Override
    public void onStartCodeHighlight() {
        //Empty initially
    }

    @Override
    public void onFinishCodeHighlight() {
        //Empty initially
    }

    @Override
    public void onLanguageDetected(Language language, int relevance) {
        Iadt.showMessage("Detected language: " + language + " relevance: " + relevance);
    }

    @Override
    public void onFontSizeChanged(int sizeInPx) {
        //Iadt.showMessage("CodeView font-size to " + sizeInPx + "px");
    }

    @Override
    public void onLineClicked(int lineNumber, String content) {
        String text = "Line " + lineNumber + " of " +  getParams().path + ":\n" + content;
        ClipboardUtils.save(getContext(), text);
        Iadt.showMessage("Line " + lineNumber + " copied to clipboard");
    }

    //endregion

    //region [ TOOL BAR ]

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ToolbarSearchHelper toolbarSearch;
        toolbarSearch = new ToolbarSearchHelper(getToolbar(), R.id.action_search);
        toolbarSearch.setHint("Search content...");
        toolbarSearch.setSubmitButtonEnabled(true);
        toolbarSearch.setOnChangeListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                codeViewer.findNext(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if (!query.isEmpty()) {
                    codeViewer.findAllAsync(query);
                    try {
                        Method m = WebView.class.getMethod(SET_FIND_IS_UP, Boolean.TYPE);
                        m.invoke(codeViewer, true);
                    } catch (Exception ignored) {
                        //TODO: Check if needed with support libs
                        // FriendlyLog.logException("onQueryTextChange with: " + query, ignored);
                    }
                } else {
                    codeViewer.findAllAsync(null);
                    try {
                        Method m = WebView.class.getMethod(SET_FIND_IS_UP, Boolean.TYPE);
                        m.invoke(codeViewer, false);
                    } catch (Exception ignored) {
                        //TODO: Check if needed with support libs
                        // FriendlyLog.logException("onQueryTextChange with: " + query, ignored);
                    }
                }
                return true;
            }
        });
        toolbarSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                codeViewer.findAllAsync(null);
                try {
                    Method m = WebView.class.getMethod(SET_FIND_IS_UP, Boolean.TYPE);
                    m.invoke(codeViewer, false);
                } catch (Exception ignored) {
                    //TODO: Check if needed with support libs
                    // FriendlyLog.logException("onClose", ignored);
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_tune) {
            onTuneButton();
        }else if (selected == R.id.action_share) {
            onShareButton();
        }
        else if (selected == R.id.action_copy) {
            onCopyButton();
        }
        return super.onMenuItemClick(item);
    }

    private void onTuneButton() {
        String[] options = getContext().getResources().getStringArray(R.array.source_tune);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext())
                .setTitle("Code view options")
                .setCancelable(true)
                .setMultiChoiceItems(options, tuneSelection, new DialogInterface.OnMultiChoiceClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (which == 0) {
                            codeViewer.setShowLineNumber(isChecked);
                        }
                        else if (which == 1) {
                            codeViewer.setWrapLine(isChecked);
                        }
                        tuneSelection[which] = isChecked;
                        codeViewer.apply();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(Layer.getLayoutType());
        alertDialog.show();
    }

    private void initTuneSelection() {
        switch (getParams().origin){
            case SOURCE:
            case TRACE:
                tuneSelection = new boolean[]{ true, false };
                break;
            case INTERNAL:
                tuneSelection = new boolean[]{ false, false };
                break;
        }
    }

    private boolean isWrapLine(){
        return tuneSelection[1];
    }

    private boolean isShowLineNumber(){
        return tuneSelection[0];
    }

    private void onShareButton() {
        final IadtController controller = IadtController.get();
        File localFile = controller.getSourcesManager().getLocalFile(getParams().path);

        if (isSourceUnavailable){
            Iadt.showMessage("Nothing to share");
        }
        else if (localFile==null) {
            Iadt.showMessage("Unable to get file path");
        }
        else{
            String title = "Source: " + getParams().path;
            String path = localFile.getAbsolutePath();
            if (path == null){
                Iadt.showMessage("Unable to get file path");
                return;
            }
            FileProviderUtils.sendExternally(title, path);
        }
    }

    private void onCopyButton() {
        if (isSourceUnavailable){
            Iadt.showMessage("Nothing to copy");
        }
        else{
            ClipboardUtils.save(getContext(), codeViewer.getCode());
            Iadt.showMessage("Content copied to clipboard");
        }
    }

    //endregion

    //region [ PARAMS ]

    public static String buildSourceParams(String path){
        return buildSourceParams(path, -1);
    }

    public static String buildSourceParams(String sourcePath, int lineNumber){
        InnerParams paramObject = new InnerParams(OriginEnum.SOURCE);
        paramObject.path = sourcePath;
        paramObject.lineNumber = lineNumber;
        Gson gson = new Gson();
        return gson.toJson(paramObject);
    }

    public static String buildTraceParams(long traceId){
        InnerParams paramObject = new InnerParams(OriginEnum.TRACE);
        paramObject.id = traceId;
        Gson gson = new Gson();
        return gson.toJson(paramObject);
    }

    public static String buildInternalParams(String internalsPath){
        InnerParams paramObject = new InnerParams(OriginEnum.INTERNAL);
        paramObject.path = internalsPath;
        Gson gson = new Gson();
        return gson.toJson(paramObject);
    }

    public InnerParams getParams(){
        Gson gson = new Gson();
        return gson.fromJson(getParam(), InnerParams.class);
    }

    public static class InnerParams {
        OriginEnum origin;
        long id;
        String path;
        int lineNumber;

        public InnerParams(OriginEnum origin) {
            this.origin = origin;
        }
    }

    //endregion
}
