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

package org.inappdevtools.library.view.components.listener;

import android.view.View;

import org.inappdevtools.library.logic.documents.data.DocumentSectionData;

import java.util.List;

import org.inappdevtools.library.view.components.FlexAdapter;
import org.inappdevtools.library.view.components.FlexViewHolder;

public class OnlyOneExpandedListener implements FlexAdapter.OnItemActionListener{

    public static final int NOTHING_EXPANDED = -1;
    FlexAdapter adapter;
    private int expandedPosition;

    public OnlyOneExpandedListener(FlexAdapter adapter) {
        this(adapter, NOTHING_EXPANDED);
    }

    public OnlyOneExpandedListener(FlexAdapter adapter, int initialExpandedPosition) {
        this.adapter = adapter;
        this.expandedPosition = initialExpandedPosition;
        adapter.setOnItemActionListener(this);
    }

    public void setNothingExpanded() {
        this.expandedPosition = NOTHING_EXPANDED;
    }

    public void setExpandedPosition(int expandedPosition) {
        this.expandedPosition = expandedPosition;
    }

    public void updateDataWithExpandedState(List<Object> flexibleData) {
        for (int i = 0; i < flexibleData.size(); i++) {
            Object current = flexibleData.get(i);
            if (current instanceof DocumentSectionData){
                boolean isExpanded = (i==expandedPosition);
                ((DocumentSectionData)current).setExpanded(isExpanded);
            }
        }
    }

    @Override
    public Object onItemAction(FlexViewHolder viewHolder, View view, int position, long id) {
        return toggleExpandedPosition(position);
    }

    public boolean toggleExpandedPosition(int clickedPosition){
        int previousPosition = expandedPosition;
        if (previousPosition == clickedPosition){
            //Collapse currently selected
            expandedPosition = -1;
            return false;
        }
        else{
            //Collapse previously selected
            if (previousPosition >= 0
                    && adapter.getItems().size()<previousPosition){
                Object previousObject = adapter.getItems().get(previousPosition);
                if (previousObject instanceof DocumentSectionData){
                    DocumentSectionData previousData = (DocumentSectionData) previousObject;
                    previousData.setExpanded(false);
                    adapter.notifyItemChanged(previousPosition);
                }
            }
            //Expand current selection
            expandedPosition = clickedPosition;
            return true;
        }
    }
}
