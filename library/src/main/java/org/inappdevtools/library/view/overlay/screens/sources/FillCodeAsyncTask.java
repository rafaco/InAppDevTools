/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.view.overlay.screens.sources;

import android.os.AsyncTask;

import org.inappdevtools.library.storage.db.IadtDatabase;
import org.inappdevtools.library.storage.db.entities.Sourcetrace;
import org.inappdevtools.library.storage.files.utils.InternalFileReader;
import org.inappdevtools.library.IadtController;

public class FillCodeAsyncTask extends AsyncTask<String, String, String> {

    protected final SourceDetailScreen screen;
    protected SourceDetailScreen.InnerParams params;

    public FillCodeAsyncTask() {
        throw new UnsupportedOperationException("You have to provide a SourceDetailScreen");
    }

    public FillCodeAsyncTask(SourceDetailScreen screen) {
        this.screen = screen;
    }

    @Override
    protected String doInBackground(String... strings) {
        params = screen.getParams();
        return getContent();
    }

    private String getContent() {

        if (params.origin.equals(SourceDetailScreen.OriginEnum.INTERNAL)) {
            InternalFileReader internalFileReader = new InternalFileReader();
            return internalFileReader.getContent(params.path);
        }

        if (params.origin.equals(SourceDetailScreen.OriginEnum.TRACE)) {
            long traceId = params.id;
            if (traceId>0){
                //Calculate finalPath and finalLineNumber from traceId
                Sourcetrace sourcetrace = IadtDatabase.get().sourcetraceDao()
                        .findById(traceId);
                params.path = IadtController.get().getSourcesManager()
                        .getPathFromClassName(sourcetrace.getClassName());
                params.lineNumber = sourcetrace.getLineNumber();
            }
        }

        //For OriginEnum.SOURCE and OriginEnum.TRACE:
        return IadtController.get().getSourcesManager().getContent(params.path);
    }


    @Override
    protected void onPostExecute(final String content) {
        super.onPostExecute(content);

        if (content == null) {
            screen.loadCodeViewEmpty();
        }else{
            screen.loadCodeViewContent(content, params.path, params.lineNumber);
        }
    }
}
