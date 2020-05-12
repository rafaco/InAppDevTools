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
import es.rafaco.inappdevtools.library.view.components.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexibleItemDescriptor;
import es.rafaco.inappdevtools.library.view.components.FlexibleLoader;
import es.rafaco.inappdevtools.library.view.components.base.FlexGroupData;
import es.rafaco.inappdevtools.library.view.components.base.FlexGroupViewHolder;
import es.rafaco.inappdevtools.library.view.utils.MarginUtils;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.cardview.widget.CardView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
//#endif

public class CardGroupViewHolder extends FlexGroupViewHolder {

    private final CardView cardView;
    private final LinearLayout childrenContainer;

    public CardGroupViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.cardView = view.findViewById(R.id.card_view);
        this.childrenContainer = view.findViewById(R.id.children_container);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        super.bindTo(abstractData, position);

        final CardGroupData data = (CardGroupData) abstractData;
        if (data!=null){
            bindMargins(data);
            bindElevation(data);
            bindBgColor(data);
            bindPerformer(data);

            bindContentPadding(data);
            bindChildren(data);
        }
    }

    private void bindMargins(CardGroupData data) {
        if (data.isFullWidth()){
            cardView.setRadius(UiUtils.getPixelsFromDp(itemView.getContext(),0));
            cardView.setUseCompatPadding(false);
            int flatCardMargin = (int) cardView.getContext().getResources()
                    .getDimension(R.dimen.iadt_flat_card_horizontal_margin);
            MarginUtils.setHorizontalMargin(cardView, flatCardMargin);
        }
        else{
            MarginUtils.setHorizontalMargin(cardView);
        }
    }

    private void bindBgColor(CardGroupData data) {
        if (data.getBgColorResource()>0){
            cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), data.getBgColorResource()));
        }
        else if (data.getPerformer() == null){
            cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.iadt_surface_bottom));
        }
    }

    private void bindElevation(CardGroupData data) {
        if (data.getElevationDp()>=0){
            cardView.setCardElevation(UiUtils.getPixelsFromDp(itemView.getContext(), data.getElevationDp()));
        }
    }

    private void bindPerformer(final CardGroupData data) {
        if (data.getPerformer() != null){
            cardView.setClickable(true);
            cardView.setFocusable(true);
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
        childrenContainer.setBackgroundColor(ContextCompat.getColor(itemView.getContext(),
                R.color.material_green));
    }

    private void bindContentPadding(CardGroupData data) {
        if (data.isNoContentPadding()){
            cardView.setContentPadding(0,0,0,0);
            cardView.setPadding(0,0,0,0);
            //cardView.setContentPadding(-6,-6,-6,-6);
        }
    }

    private void bindChildren(CardGroupData data) {
        childrenContainer.removeAllViews();
        if (data.getChildren()==null && data.getChildren().isEmpty()) {
            return;
        }

        if (data.getChildren().size() == 1
                && data.getChildren().get(0) instanceof FlexGroupData){
            FlexibleItemDescriptor desc = FlexibleLoader.getDescriptor(data.getChildren().get(0).getClass());
            desc.addToView(data.getChildren().get(0), cardView);
        }
        else{
            LinearGroupData childrenGroup = new LinearGroupData(data.getChildren());
            childrenGroup.setBackgroundColor(R.color.rally_orange);
            FlexibleItemDescriptor desc = FlexibleLoader.getDescriptor(LinearGroupData.class);
            desc.addToView(childrenGroup, cardView);
        }
    }
}
