package es.rafaco.inappdevtools.library.view.overlay.screens.sources;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.view.components.codeview.Language;
import es.rafaco.inappdevtools.library.view.components.codeview.Theme;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class FillTraceAsyncTask extends AsyncTask<Long, String, String> {

    private final SourceDetailScreen screen;
    private long traceId;
    private Sourcetrace currentTrace;
    private ArrayList<Sourcetrace> groupTraces = new ArrayList<>();
    private int currentPosition = -1;
    private int totalTraces;

    public FillTraceAsyncTask() {
        throw new UnsupportedOperationException("You have to provide a SourceDetailScreen");
    }

    public FillTraceAsyncTask(SourceDetailScreen screen) {
        this.screen = screen;
    }

    
    @Override
    protected String doInBackground(Long... longs) {
        traceId = longs[0];

        currentTrace = IadtController.getDatabase().sourcetraceDao()
                .findById(traceId);

        List<Sourcetrace> allTraces = IadtController.getDatabase().sourcetraceDao()
                .filterCrash(currentTrace.getLinkedId());

        //Reverse iteration
        for (int i = allTraces.size() - 1; i>=0; i--){
            Sourcetrace trace = allTraces.get(i);
            if (trace.getExtra().equals(currentTrace.getExtra())){
                groupTraces.add(trace);
                if (trace.getUid() == currentTrace.getUid()){
                    currentPosition = groupTraces.size();
                }
            }
        }
        totalTraces = groupTraces.size();

        return "";
    }

    @Override
    protected void onPostExecute(final String content) {
        super.onPostExecute(content);

        screen.traceContainer.setVisibility(View.VISIBLE);
        String traceMessage = Humanizer.toCapitalCase(currentTrace.getExtra()) + " trace ";
        String positionMessage = currentPosition + "/" + totalTraces;
        String fileMessage = "File " + currentTrace.getFileName();
        String lineMessage = "Line " + currentTrace.getLineNumber();
        String codeMessage = currentTrace.getClassName() + "." + currentTrace.getMethodName() + "()";

        screen.traceLabel.setText(traceMessage + positionMessage + Humanizer.newLine()
                + fileMessage + Humanizer.newLine()
                + lineMessage);
        screen.wideLabel.setText(codeMessage);

        if (currentPosition > 1){
            screen.prevButton.setAlpha(1f);
            final long previousTrace = groupTraces.get(currentPosition - 2).getUid();
            screen.prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    screen.loadTrace(previousTrace);
                }
            });
        }
        else{
            screen.prevButton.setAlpha(0.5f);
            screen.prevButton.setOnClickListener(null);
        }

        if (currentPosition < totalTraces){
            screen.nextButton.setAlpha(1f);
            final long nextTrace = groupTraces.get(currentPosition).getUid();
            screen.nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    screen.loadTrace(nextTrace);
                }
            });
        }
        else{
            screen.nextButton.setAlpha(0.5f);
            screen.nextButton.setOnClickListener(null);
        }

        if (currentTrace != null){
            String path = IadtController.get().getSourcesManager().getPathFromClassName(currentTrace.getClassName());

            if (TextUtils.isEmpty(path)) {
                String noSources = Humanizer.fullStop() + "Source code not available." + Humanizer.fullStop();

                screen.codeViewer.setTheme(Theme.ANDROIDSTUDIO)
                        .setFontSize(12)
                        .setShowLineNumber(false)
                        .setWrapLine(false)
                        .setZoomEnabled(false)
                        .setCode(noSources)
                        .setLanguage(Language.PLAINTEXT)
                        .apply();
            }
            else{
                screen.loadSource(path, currentTrace.getLineNumber() + "");
            }
        }
    }
}
