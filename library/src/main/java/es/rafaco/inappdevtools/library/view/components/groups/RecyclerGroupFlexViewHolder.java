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

package es.rafaco.inappdevtools.library.view.components.groups;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.DividerItemDecoration;
//@import androidx.recyclerview.widget.LinearLayoutManager;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
//#endif

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.base.GroupFlexData;
import es.rafaco.inappdevtools.library.view.components.base.GroupFlexViewHolder;

public class RecyclerGroupFlexViewHolder extends GroupFlexViewHolder {

    RecyclerView groupContainer;
    FlexAdapter internalAdapter;

    public RecyclerGroupFlexViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        groupContainer = view.findViewById(R.id.group_container);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        super.bindTo(abstractData, position);
        
        final RecyclerGroupFlexData data = (RecyclerGroupFlexData) abstractData;
        if (data==null){
            groupContainer.setVisibility(View.GONE);
            return;
        }

        bindOrientation(data);
        bindDividers(data);
        bindAdapter(data);

        itemView.setClickable(false);
        itemView.setFocusable(false);
        groupContainer.setClickable(false);
        groupContainer.setFocusable(false);
    }

    private void bindOrientation(RecyclerGroupFlexData data) {
        int orientation = data.isHorizontal
                ? RecyclerView.HORIZONTAL
                : RecyclerView.VERTICAL;
        LinearLayoutManager lm = new LinearLayoutManager(getContext(),
                    orientation, false);
        groupContainer.setLayoutManager(lm);
    }

    private void bindDividers(RecyclerGroupFlexData data) {
        if (!data.showDividers)
            return;

        int orientation = data.isHorizontal
                ? LinearLayout.HORIZONTAL
                : LinearLayout.VERTICAL;
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                orientation);
        groupContainer.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void bindChildren(GroupFlexData data) {
        //Do nothing
    }

    @Override
    public ViewGroup getChildrenContainer() {
        return groupContainer;
    }

    private void bindAdapter(RecyclerGroupFlexData data) {
        if (data.getAdapter() != null){
            internalAdapter = data.getAdapter();
        }
        else if(data.getAdapterLayout()!=null
                && data.getAdapterSpanCount()>0
                && data.getChildren()!=null){
            internalAdapter = new FlexAdapter(data.adapterLayout,
                    data.getAdapterSpanCount(),
                    data.getChildren());
        }
        groupContainer.setAdapter(internalAdapter);
    }
}
