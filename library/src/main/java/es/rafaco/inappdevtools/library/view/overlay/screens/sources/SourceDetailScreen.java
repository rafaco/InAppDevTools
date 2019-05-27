package es.rafaco.inappdevtools.library.view.overlay.screens.sources;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

//#ifdef MODERN
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
//#else
//@import android.support.v7.app.AlertDialog;
//@import android.support.v7.widget.SearchView;
//@import import androidx.core.view.MenuItemCompat;
//#endif

import com.google.gson.Gson;

import java.lang.reflect.Method;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.codeview.CodeView;
import es.rafaco.inappdevtools.library.view.components.codeview.Language;
import es.rafaco.inappdevtools.library.view.components.codeview.Theme;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.layers.OverlayLayer;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog.ToolBarHelper;

import static android.content.Context.SEARCH_SERVICE;

public class SourceDetailScreen extends OverlayScreen implements CodeView.OnHighlightListener {

    private TextView codeHeader;
    private CodeView codeViewer;
    private ToolBarHelper toolbarHelper;
    boolean[] tuneSelection;
    private boolean flag = false;

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
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup view) {
        codeHeader = bodyView.findViewById(R.id.code_header);
        codeViewer = bodyView.findViewById(R.id.code_view);

        new AsyncTask<String, String, String>(){
            @Override
            protected String doInBackground(String... strings) {
                return DevTools.getSourcesManager().getContent(getParams().path);
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
                                .setCode(content)
                                .setLanguage(Language.AUTO)
                                .setFontSize(12)
                                .setShowLineNumber(true)
                                .setWrapLine(false)
                                .setZoomEnabled(false)
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

    private String getLanguage() {
        if (getParams().path.contains(".")){
            int lastFound = getParams().path.lastIndexOf(".");
            return getParams().path.substring(lastFound + 1);
        }
        return "";
    }




    //region [ CodeView HighlightListener ]

    @Override
    public void onStartCodeHighlight() {
    }

    @Override
    public void onFinishCodeHighlight() {
        Toast.makeText(getContext(), "line count: " + codeViewer.getLineCount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLanguageDetected(Language language, int relevance) {
        Toast.makeText(getContext(), "language: " + language + " relevance: " + relevance, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFontSizeChanged(int sizeInPx) {
        Log.d("TAG", "font-size: " + sizeInPx + "px");
    }

    @Override
    public void onLineClicked(int lineNumber, String content) {
        Toast.makeText(getContext(), "line: " + lineNumber + " html: " + content, Toast.LENGTH_SHORT).show();
    }

    //endregion


    //region [ TOOL BAR ]

    private void initToolbar() {
        toolbarHelper = new ToolBarHelper(getToolbar());
        toolbarHelper.initSearchButtons(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //TODO
                return false;
            }
        });

        toolbarHelper.initSearchMenuItem(R.id.action_search, "Search content...");
        toolbarHelper.showAllMenuItem();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

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
