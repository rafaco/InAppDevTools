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
import android.widget.LinearLayout;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexDescriptor;
import es.rafaco.inappdevtools.library.view.components.FlexLoader;
import es.rafaco.inappdevtools.library.view.components.base.GroupFlexData;
import es.rafaco.inappdevtools.library.view.components.base.GroupFlexViewHolder;
import es.rafaco.inappdevtools.library.view.utils.MarginUtils;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.cardview.widget.CardView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
//#endif

public class CardGroupFlexViewHolder extends GroupFlexViewHolder {

    private final CardView cardView;
    private final LinearLayout childrenContainer;

    public CardGroupFlexViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        this.cardView = view.findViewById(R.id.card_view);
        this.childrenContainer = view.findViewById(R.id.children_container);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        super.bindTo(abstractData, position);

        final CardGroupFlexData data = (CardGroupFlexData) abstractData;
        if (data!=null){
            bindMargins(data);
            bindElevation(data);
            bindBgColor(data);
            bindPerformer(data);

            bindContentPadding(data);
            bindChildren(data);
        }
    }

    private void bindMargins(CardGroupFlexData data) {
        if (data.isFullWidth()){
            cardView.setRadius(UiUtils.getPixelsFromDp(getContext(),0));
            cardView.setUseCompatPadding(false);
            int flatCardMargin = (int) getContext().getResources()
                    .getDimension(R.dimen.iadt_flat_card_horizontal_margin);
            MarginUtils.setHorizontalMargin(cardView, flatCardMargin);
        }
        else{
            MarginUtils.setHorizontalMargin(cardView);
        }
    }

    private void bindBgColor(CardGroupFlexData data) {
        if (data.getBgColorResource()>0){
            cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), data.getBgColorResource()));
        }
        else if (data.getPerformer() == null){
            cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.iadt_surface_bottom));
        }
    }

    private void bindElevation(CardGroupFlexData data) {
        if (data.getElevationDp()>=0){
            cardView.setCardElevation(UiUtils.getPixelsFromDp(getContext(), data.getElevationDp()));
        }
    }

    private void bindPerformer(final CardGroupFlexData data) {
        if (data.getPerformer() != null){
            cardView.setClickable(true);
            cardView.setActivated(true);
            cardView.setFocusable(false);
            cardView.setFocusableInTouchMode(false);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.getPerformer().run();
                }
            });
        }
        else{
            cardView.setOnClickListener(null);
            cardView.setClickable(false);
            cardView.setFocusable(false);
        }

        //TODO: remove
        childrenContainer.setBackgroundColor(ContextCompat.getColor(getContext(),
                R.color.material_green));
    }

    private void bindContentPadding(CardGroupFlexData data) {
        if (data.isNoContentPadding()){
            cardView.setContentPadding(0,0,0,0);
            cardView.setPadding(0,0,0,0);
            //cardView.setContentPadding(-6,-6,-6,-6);
        }
    }

    private void bindChildren(CardGroupFlexData data) {
        childrenContainer.removeAllViews();
        if (data.getChildren()==null && data.getChildren().isEmpty()) {
            return;
        }

        if (data.getChildren().size() == 1
                && data.getChildren().get(0) instanceof GroupFlexData){
            FlexDescriptor desc = FlexLoader.getDescriptor(data.getChildren().get(0).getClass());
            desc.addToView(data.getChildren().get(0), cardView);
        }
        else{
            LinearGroupFlexData childrenGroup = new LinearGroupFlexData(data.getChildren());
            childrenGroup.setBackgroundColor(R.color.rally_orange);
            FlexDescriptor desc = FlexLoader.getDescriptor(LinearGroupFlexData.class);
            desc.addToView(childrenGroup, cardView);
        }
    }
}
