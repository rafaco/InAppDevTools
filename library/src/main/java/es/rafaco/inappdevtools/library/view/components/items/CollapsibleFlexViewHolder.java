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

package es.rafaco.inappdevtools.library.view.components.items;

import android.view.View;
import android.widget.FrameLayout;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexDescriptor;
import es.rafaco.inappdevtools.library.view.components.FlexLoader;
import es.rafaco.inappdevtools.library.view.components.FlexViewHolder;
import es.rafaco.inappdevtools.library.view.components.base.FlexItemViewHolder;
import es.rafaco.inappdevtools.library.view.components.cards.CardHeaderFlexData;
import es.rafaco.inappdevtools.library.view.components.cards.CardHeaderFlexViewHolder;

public class CollapsibleFlexViewHolder extends FlexItemViewHolder {

    private final FrameLayout headerContainer;
    private FlexViewHolder headerViewHolder;
    private final FrameLayout separatorContainer;
    private final FrameLayout contentContainer;

    public CollapsibleFlexViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        this.headerContainer = view.findViewById(R.id.header_container);
        this.separatorContainer = view.findViewById(R.id.separator_container);
        this.contentContainer = view.findViewById(R.id.content_container);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        super.bindTo(abstractData, position);
        CollapsibleFlexData data = (CollapsibleFlexData) abstractData;

        bindHeader(data);
        bindSeparator(data);
        bindContent(data);

        applyExpandedState(data, data.isExpanded());
    }

    private void bindHeader(final CollapsibleFlexData data) {
        if (data.getHeader()==null)
            headerContainer.setVisibility(View.GONE);
        else{
            headerContainer.removeAllViews();
            //TODO: implement expandable
            if (data.getHeader() instanceof CardHeaderFlexData){
                ((CardHeaderFlexData)data.getHeader()).setExpanded(data.isExpanded());
            }
            FlexDescriptor desc = FlexLoader.getDescriptor(CardHeaderFlexData.class);
            if (desc != null){
                headerViewHolder = desc.addToView(data.getHeader(), headerContainer);
            }

            itemView.setActivated(true);
            itemView.setClickable(true);
            itemView.setFocusable(false);
            itemView.setFocusableInTouchMode(false);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean newState = !data.isExpanded();
                    data.setExpanded(newState);
                    applyExpandedState(data, newState);
                }
            });
        }
    }

    private void bindSeparator(CollapsibleFlexData data) {
        if (data.isSeparator()){
            separatorContainer.removeAllViews();
            FlexDescriptor desc = FlexLoader.getDescriptor(SeparatorFlexData.class);
            if (desc != null){
                desc.addToView(new SeparatorFlexData(true), separatorContainer);
            }
        }
    }

    private void bindContent(CollapsibleFlexData data) {
        if (data.getContent()==null)
            contentContainer.setVisibility(View.GONE);
        else{
            contentContainer.removeAllViews();
            FlexDescriptor desc = FlexLoader.getDescriptor(data.getContent().getClass());
            if (desc != null){
                desc.addToView(data.getContent(), contentContainer);
            }
        }
    }

    private void applyExpandedState(CollapsibleFlexData data, Boolean isExpanded) {
        //data.getHeader().setExpanded(isExpanded);
        //headerViewHolder.bindTo(data.getHeader(), -1);

        //TODO: implement expandable
        if (headerViewHolder instanceof CardHeaderFlexViewHolder){
            CardHeaderFlexViewHolder cardHeaderViewHolder = (CardHeaderFlexViewHolder) headerViewHolder;
            cardHeaderViewHolder.applyExpandedStateToNavIcon(isExpanded);
            cardHeaderViewHolder.applyExpandedStateToText(isExpanded);
        }

        if (!isExpanded){
            separatorContainer.setVisibility(View.GONE);
            contentContainer.setVisibility(View.GONE);
        }
        else {
            if (data.isSeparator()) separatorContainer.setVisibility(View.VISIBLE);
            contentContainer.setVisibility(View.VISIBLE);
        }
    }
}
