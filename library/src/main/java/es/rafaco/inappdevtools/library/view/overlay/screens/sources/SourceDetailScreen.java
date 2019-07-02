package es.rafaco.inappdevtools.library.view.overlay.screens.sources;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

//#ifdef MODERN
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.appcompat.widget.SearchView;
//#else
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
//#endif

import com.google.gson.Gson;

import java.lang.reflect.Method;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.sources.NodesHelper;
import es.rafaco.inappdevtools.library.logic.utils.ClipboardUtils;
import es.rafaco.inappdevtools.library.storage.files.FileProviderUtils;
import es.rafaco.inappdevtools.library.view.components.codeview.CodeView;
import es.rafaco.inappdevtools.library.view.components.codeview.Language;
import es.rafaco.inappdevtools.library.view.components.codeview.Theme;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.layers.OverlayLayer;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog.ToolBarHelper;
import es.rafaco.inappdevtools.library.view.utils.PathUtils;

import static android.content.Context.SEARCH_SERVICE;

public class SourceDetailScreen extends OverlayScreen implements CodeView.OnHighlightListener {

    private TextView codeHeader;
    private CodeView codeViewer;
    private ToolBarHelper toolbarHelper;
    boolean[] tuneSelection;

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
    public int getToolbarLayoutId() {
        return R.menu.source;
    }

    @Override
    public boolean needNestedScroll() {
        return false;
    }

    @Override
    protected void onCreate() {
        //TODO: subtitle
        //getToolbar().setSubtitle(PathUtils.getFileNameWithExtension(getParams().path));
    }

    @Override
    protected void onStart(ViewGroup view) {
        codeHeader = bodyView.findViewById(R.id.code_header);
        codeViewer = bodyView.findViewById(R.id.code_view);

        new AsyncTask<String, String, String>(){
            @Override
            protected String doInBackground(String... strings) {
                return IadtController.get().getSourcesManager().getContent(getParams().path);
            }

            @Override
            protected void onPostExecute(final String content) {
                super.onPostExecute(content);
                codeHeader.setText(getParams().path);

                if (content == null){
                    codeViewer.setCode("Unable to get content");
                    return;
                }

                codeViewer.post(new Runnable() {
                    @Override
                    public void run() {
                        codeViewer.setOnHighlightListener(SourceDetailScreen.this)
                                .setTheme(Theme.ANDROIDSTUDIO)
                                .setFontSize(12)
                                .setShowLineNumber(true)
                                .setWrapLine(false)
                                .setZoomEnabled(false)
                                .setCode(content)
                                .setLanguage(parseLanguage())
                                .apply();

                        if (getParams().lineNumber > 0){
                            codeViewer.highlightLineNumber(getParams().lineNumber);
                            codeViewer.scrollToLine(getParams().lineNumber);
                        }
                    }
                });

                tuneSelection = new boolean[]{ true, false };
            }
        }.execute();

        //initToolbar();
    }

    public Language parseLanguage() {
        String extension = NodesHelper.getFileExtensionFromPath(getParams().path);
        Language language = Language.getLanguageByName(extension.toLowerCase());
        if (language != null){
            return language;
        }
        return Language.AUTO;
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

        //TODO: subtitle
        //getToolbar().setSubtitle(PathUtils.getFileNameWithExtension(getParams().path));

        toolbarHelper = new ToolBarHelper(getToolbar());
        MenuItem menuItem = toolbarHelper.initSearchMenuItem(R.id.action_search, "Search content...");
        toolbarHelper.showAllMenuItem();
        
        final SearchView searchView = (SearchView) menuItem.getActionView();

        SearchManager searchManager = (SearchManager) getContext().getSystemService(SEARCH_SERVICE);
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

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
            ClipboardUtils.save(getContext(), codeViewer.getCode());
            Iadt.showMessage("Content copied to clipboard");
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
        alertDialog.getWindow().setType(OverlayLayer.getLayoutType());
        alertDialog.show();
    }

    private void onShareButton() {
        new AsyncTask<String, String, String>(){
            @Override
            protected String doInBackground(String... strings) {
                return IadtController.get().getSourcesManager().getLocalFile(getParams().path)
                        .getAbsolutePath();
            }

            @Override
            protected void onPostExecute(final String path) {
                super.onPostExecute(path);

                if (path == null){
                    Iadt.showMessage("Unable to get file path");
                    return;
                }
                FileProviderUtils.openFileExternally(Iadt.getAppContext(), path, Intent.ACTION_SEND);
            }
        }.execute();
    }
    //endregion


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
