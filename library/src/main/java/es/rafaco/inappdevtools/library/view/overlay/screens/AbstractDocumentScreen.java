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

package es.rafaco.inappdevtools.library.view.overlay.screens;

import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.view.components.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.components.listener.OnlyOneExpandedListener;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;

public abstract class AbstractDocumentScreen extends Screen {
    private FlexibleAdapter adapter;
    private OnlyOneExpandedListener helper;

    public AbstractDocumentScreen(ScreenManager manager) {
        super(manager);
    }

    public abstract String getTitle();

    protected abstract DocumentType getDocumentType();

    @Override
    public int getBodyLayoutId() { return R.layout.flexible_container; }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup view) {
        DocumentData document = DocumentRepository.getDocument(getDocumentType(), getDocumentParam());
        //getScreenManager().setTitle(document.getTitle());

        if (document == null){
            initAdapter(buildErrorData());
            return;
        }
        initAdapter(buildDataFromDocument(document));
    }

    private List<Object> buildErrorData() {
        List<Object> data = new ArrayList<>();
        data.add("");
        data.add("Unable to get document for the provided params:");
        data.add(" - DocumentType: " + getDocumentType().getName());
        data.add(" - DocumentParam: " + getDocumentParam());
        return data;
    }

    protected Object getDocumentParam(){
        return IadtController.get().getSessionManager().getCurrentUid();
    }

    protected List<Object> buildDataFromDocument(DocumentData reportData) {
        List<Object> objectList = new ArrayList<Object>(reportData.getSections());
        objectList.add(0, reportData.getOverviewData());
        objectList.add(1, "");
        return objectList;
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexibleAdapter(FlexibleAdapter.Layout.GRID, 3, data);
        helper = new OnlyOneExpandedListener(adapter);

        RecyclerView recyclerView = bodyView.findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        //Nothing needed
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }
}
