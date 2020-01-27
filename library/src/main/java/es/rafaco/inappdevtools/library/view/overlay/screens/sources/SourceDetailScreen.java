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

import br.tiagohm.CodeView;
import br.tiagohm.Language;
import es.rafaco.compat.AppCompatButton;
import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.utils.ClipboardUtils;
import es.rafaco.inappdevtools.library.storage.files.utils.FileProviderUtils;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.ToolBarHelper;

public class SourceDetailScreen extends Screen implements CodeView.OnHighlightListener {

    CodeView codeViewer;
    ToolBarHelper toolbarHelper;
    boolean[] tuneSelection;
    RelativeLayout traceContainer;
    TextView traceLabel;
    AppCompatButton prevButton;
    AppCompatButton nextButton;
    TextView wideLabel;
    private boolean isSourceUnavailable;

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
    }

    @Override
    protected void onStart(ViewGroup view) {
        traceContainer = bodyView.findViewById(R.id.trace_container);
        prevButton = bodyView.findViewById(R.id.prev_trace);
        traceLabel = bodyView.findViewById(R.id.trace_label);
        wideLabel = bodyView.findViewById(R.id.wide_label);
        nextButton = bodyView.findViewById(R.id.next_trace);
        codeViewer = bodyView.findViewById(R.id.code_view);

        if (!getParams().isTrace){
            traceContainer.setVisibility(View.GONE);
            String message = getParams().path;
            if (getParams().lineNumber>0){
                message += Humanizer.newLine()
                        + "Line: " + getParams().lineNumber;
            }
            wideLabel.setText(message);
            loadSource(getParams().path, getParams().lineNumber + "");
        }
        else{
            getScreenManager().setTitle("Stacktrace");
            loadTrace(getParams().traceId);
        }
    }

    protected void loadSource(String path, String line) {
        new FillSourceAsyncTask(this).execute(path, line);
    }

    private void loadSourceUnavailable() {
        isSourceUnavailable = true;
    }

    protected void loadTrace(long traceId) {
        new FillTraceAsyncTask(this).execute(traceId);
    }

    @Override
    protected void onStop() {
        //Nothing needed
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }

    //region [ CodeView listener ]

    @Override
    public void onStartCodeHighlight() {
    }

    @Override
    public void onFinishCodeHighlight() {
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

        toolbarHelper = new ToolBarHelper(getToolbar());
        MenuItem menuItem = toolbarHelper.initSearchMenuItem(R.id.action_search, "Search content...");
        toolbarHelper.showAllMenuItem();
        
        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
                        Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
                        m.invoke(codeViewer, true);
                    } catch (Throwable ignored) {
                        //TODO: Check if needed with support libs
                        // FriendlyLog.logException("onQueryTextChange with: " + query, ignored);
                    }
                }
                else {
                    codeViewer.findAllAsync(null);
                    try {
                        Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
                        m.invoke(codeViewer, false);
                    } catch (Throwable ignored) {
                        //TODO: Check if needed with support libs
                        // FriendlyLog.logException("onQueryTextChange with: " + query, ignored);
                    }
                }
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                codeViewer.findAllAsync(null);
                try {
                    Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
                    m.invoke(codeViewer, false);
                } catch (Throwable ignored) {
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

    private void onShareButton() {
        final IadtController controller = IadtController.get();
        File localFile = controller.getSourcesManager().getLocalFile(getParams().path);

        if (isSourceUnavailable){
            Iadt.showMessage("Nothing to share");
        }
        else if (localFile==null) {
            Iadt.showMessage("Unable to get file path");
            return;
        }
        else{
            String path = localFile.getAbsolutePath();
            if (path == null){
                Iadt.showMessage("Unable to get file path");
                return;
            }
            FileProviderUtils.sendExternally(controller.getContext(), path);
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

    public static String buildParams(String path){
        InnerParams paramObject = new InnerParams(path, -1);
        Gson gson = new Gson();
        return gson.toJson(paramObject);
    }

    public static String buildParams(String path, int lineNumber){
        InnerParams paramObject = new InnerParams(path, lineNumber);
        Gson gson = new Gson();
        return gson.toJson(paramObject);
    }

    public static String buildParams(long traceId){
        InnerParams paramObject = new InnerParams(traceId);
        Gson gson = new Gson();
        return gson.toJson(paramObject);
    }

    public InnerParams getParams(){
        Gson gson = new Gson();
        return gson.fromJson(getParam(), InnerParams.class);
    }

    public void setSourceUnavailable(boolean notAvailable) {
        isSourceUnavailable = notAvailable;
    }

    public static class InnerParams {
        long traceId;
        boolean isTrace;
        String path;
        int lineNumber;
        long crashId;

        public InnerParams(String path, int lineNumber) {
            this.isTrace = false;
            this.path = path;
            this.lineNumber = lineNumber;
        }

        public InnerParams(long traceId) {
            this.isTrace = true;
            this.traceId = traceId;
        }
    }

    //endregion
}
