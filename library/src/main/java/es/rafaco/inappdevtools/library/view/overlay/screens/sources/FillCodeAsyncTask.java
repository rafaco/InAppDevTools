/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

import android.content.Context;
import android.os.AsyncTask;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.storage.files.utils.InternalFileReader;

public class FillCodeAsyncTask extends AsyncTask<String, String, String> {

    protected final SourceDetailScreen screen;
    protected SourceDetailScreen.InnerParams params;
    protected String finalPath;
    protected int finalLineNumber;

    public FillCodeAsyncTask() {
        throw new UnsupportedOperationException("You have to provide a SourceDetailScreen");
    }

    public FillCodeAsyncTask(SourceDetailScreen screen) {
        this.screen = screen;
    }

    @Override
    protected String doInBackground(String... strings) {
        params = screen.getParams();
        finalPath = initFinalPath();
        finalLineNumber = initFinalLineNumber();

        String result = getContent();
        return result;
    }

    private String initFinalPath() {
        return params.path;
    }

    private int initFinalLineNumber() {
        return params.lineNumber;
    }

    private String getContent() {

        if (params.origin.equals(SourceDetailScreen.OriginEnum.INTERNAL)) {
            Context context = IadtController.get().getContext();
            InternalFileReader internalFileReader = new InternalFileReader();
            return internalFileReader.getContent(finalPath);
        }

        if (params.origin.equals(SourceDetailScreen.OriginEnum.TRACE)) {
            //Recalculate finalPath and finalLineNumber from trace data
            long traceId;
            try {
                traceId = Long.parseLong(finalPath);
            }catch (Exception e){
                traceId = -1;
            }
            if (traceId>0){
                Sourcetrace sourcetrace = IadtController.getDatabase().sourcetraceDao()
                        .findById(traceId);
                finalPath = IadtController.get().getSourcesManager()
                        .getPathFromClassName(sourcetrace.getClassName());
                finalLineNumber = sourcetrace.getLineNumber();
            }
        }

        //For OriginEnum.SOURCE and OriginEnum.TRACE:
        return IadtController.get().getSourcesManager().getContent(finalPath);
    }


    @Override
    protected void onPostExecute(final String content) {
        super.onPostExecute(content);

        if (content == null) {
            screen.loadCodeViewEmpty();
        }else{
            screen.loadCodeViewContent(content, finalPath, params.lineNumber);
        }
    }
}
