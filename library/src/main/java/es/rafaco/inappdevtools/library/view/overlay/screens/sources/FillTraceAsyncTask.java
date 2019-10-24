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
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import br.tiagohm.Theme;
import br.tiagohm.Language;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
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
            screen.loadSource(path, currentTrace.getLineNumber() + "");
        }
    }
}
