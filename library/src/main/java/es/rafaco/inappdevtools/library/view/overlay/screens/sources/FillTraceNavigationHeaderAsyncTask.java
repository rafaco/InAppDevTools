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

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.storage.db.IadtDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;

public class FillTraceNavigationHeaderAsyncTask extends AsyncTask<Long, String, String> {

    private final SourceDetailScreen screen;
    private ArrayList<Sourcetrace> groupTraces = new ArrayList<>();
    private int currentPosition = -1;

    public FillTraceNavigationHeaderAsyncTask() {
        throw new UnsupportedOperationException("You have to provide a SourceDetailScreen");
    }

    public FillTraceNavigationHeaderAsyncTask(SourceDetailScreen screen) {
        this.screen = screen;
    }
    
    @Override
    protected String doInBackground(Long... longs) {
        long traceId = screen.getParams().id;

        Sourcetrace currentTrace = IadtDatabase.get().sourcetraceDao().findById(traceId);

        //Filter group traces and detect currentPosition
        List<Sourcetrace> allTraces = IadtDatabase.get().sourcetraceDao()
                .filterCrash(currentTrace.getLinkedId());
        for (int i = allTraces.size() - 1; i>=0; i--){
            Sourcetrace trace = allTraces.get(i);
            if (trace.getExtra().equals(currentTrace.getExtra())){
                groupTraces.add(trace);
                if (trace.getUid() == currentTrace.getUid()){
                    currentPosition = groupTraces.size();
                }
            }
        }
        return "";
    }

    @Override
    protected void onPostExecute(final String content) {
        super.onPostExecute(content);
        screen.loadTraceNavigationHeader(groupTraces, currentPosition);
    }
}
