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

package es.rafaco.inappdevtools.library.view.components.cards;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.cardview.widget.CardView;
//#else
import android.support.v7.widget.CardView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.view.components.base.FlexData;
import es.rafaco.inappdevtools.library.view.components.items.ButtonFlexData;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexDescriptor;
import es.rafaco.inappdevtools.library.view.components.FlexLoader;
import es.rafaco.inappdevtools.library.view.components.FlexViewHolder;
import es.rafaco.inappdevtools.library.view.components.groups.LinearGroupFlexData;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class ComplexCardViewHolder extends FlexViewHolder {

    protected final CardView cardView;
    protected final TextView iconView;
    protected final TextView titleView;
    protected final TextView overviewView;
    protected final ImageView navIcon;
    protected final LinearLayout collapsedContentView;
    protected final View contentSeparator;
    protected final TextView contentView;
    protected final FrameLayout internalContainer;
    protected final View internalSeparator;
    protected final FrameLayout buttonGroupContainer;
    protected final View buttonSeparator;

    public ComplexCardViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        this.cardView = view.findViewById(R.id.card_view);
        this.iconView = view.findViewById(R.id.icon);
        this.titleView = view.findViewById(R.id.title);
        this.overviewView = view.findViewById(R.id.overview);
        this.navIcon = view.findViewById(R.id.nav_icon);
        this.collapsedContentView = view.findViewById(R.id.collapsedContent);
        this.contentSeparator = view.findViewById(R.id.content_separator);
        this.contentView = view.findViewById(R.id.content);
        this.internalContainer = view.findViewById(R.id.internal_container);
        this.internalSeparator = view.findViewById(R.id.internal_separator);
        this.buttonGroupContainer = view.findViewById(R.id.button_group_container);
        this.buttonSeparator = view.findViewById(R.id.button_separator);
    }

    @Override
    public void bindTo(Object abstractData, final int position) {
        final DocumentSectionData data = (DocumentSectionData) abstractData;
        if (data!=null){

            itemView.setClickable(true);
            itemView.setActivated(true);

            bindHeader(data);
            bindContent(data);
            bindExpandedState(position, data);

            bindInternalData(data);
            bindButtons(data);
        }
    }

    private void bindHeader(DocumentSectionData data) {
        int icon = data.getIcon();
        if (icon>0){
            IconUtils.set(iconView, icon);
            iconView.setVisibility(View.VISIBLE);
        }else{
            iconView.setVisibility(View.GONE);
        }

        titleView.setText(data.getTitle());
        overviewView.setText(data.getOverview());

        UiUtils.setBackground(navIcon, null);
        navIcon.setVisibility(View.VISIBLE);
    }

    private void bindContent(DocumentSectionData data) {
        String content = data.entriesToString();
        //TODO: improve entriesToString to avoid next line
        content = Humanizer.trimNewlines(content);
        contentView.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);
        contentView.setText(content);
    }

    private void bindExpandedState(final int position, final DocumentSectionData data) {
        if (data.isExpandable() && !data.getEntries().isEmpty()) {
            contentSeparator.setVisibility(View.VISIBLE);
            applyExpandedState(data.isExpanded());
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.setExpanded(!data.isExpanded());
                    toggleExpandedState(position);
                }
            });
        }
        else {
            contentSeparator.setVisibility(View.GONE);
            collapsedContentView.setVisibility(View.VISIBLE);
            cardView.setOnClickListener(null);
            cardView.setClickable(false);
            cardView.setFocusable(false);
            cardView.setForeground(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setCardElevation(UiUtils.getPixelsFromDp(getContext(), 0));
            }
        }
    }

    private void bindInternalData(DocumentSectionData data) {
        if (data.getInternalData()==null || data.getInternalData().isEmpty()){
            internalSeparator.setVisibility(View.GONE);
            internalContainer.setVisibility(View.GONE);
            internalContainer.removeAllViews();
            return;
        }
        internalSeparator.setVisibility(View.VISIBLE);
        internalContainer.setVisibility(View.VISIBLE);
        internalContainer.removeAllViews();
        LinearGroupFlexData internalList = new LinearGroupFlexData(data.getInternalData());
        internalList.setShowDividers(true);
        FlexDescriptor desc = FlexLoader.getDescriptor(LinearGroupFlexData.class);
        desc.addToView(internalList, internalContainer);
    }


    private void bindButtons(DocumentSectionData data) {
        List<FlexData> buttons = data.getButtons();
        if (buttons == null || buttons.isEmpty()){
            buttonSeparator.setVisibility(View.GONE);
            buttonGroupContainer.setVisibility(View.GONE);
        }
        else{
            buttonSeparator.setVisibility(View.VISIBLE);
            buttonGroupContainer.setVisibility(View.VISIBLE);
            buttonGroupContainer.removeAllViews();
            List<Object> castedButtons = new ArrayList<>();
            for (FlexData button : buttons) {
                if (button instanceof ButtonFlexData){
                    ((ButtonFlexData)button).setColor(R.color.iadt_surface_bottom);
                }
                castedButtons.add(button);
            }
            LinearGroupFlexData buttonGroupData = new LinearGroupFlexData(castedButtons);
            buttonGroupData.setHorizontal(true);
            buttonGroupData.setChildLayout(FlexData.LayoutType.WRAP_BOTH);
            FlexDescriptor desc = FlexLoader.getDescriptor(LinearGroupFlexData.class);
            desc.addToView(buttonGroupData, buttonGroupContainer);
        }
    }

    private void toggleExpandedState(int position) {
        Boolean isExpandedFromAdapter = (Boolean) parentAdapter.performItemAction(this, null, position, -1);

        applyExpandedState( isExpandedFromAdapter!=null
                ? isExpandedFromAdapter
                : !isExpandedFromView());
    }


    private boolean isExpandedFromView(){
        return collapsedContentView.getVisibility() == View.VISIBLE;
    }

    private void applyExpandedState(Boolean isExpanded) {
        if (!isExpanded){
            IconUtils.applyToImageView(navIcon, R.drawable.ic_arrow_down_white_24dp, R.color.iadt_primary);
            collapsedContentView.setVisibility(View.GONE);
        }
        else {
            IconUtils.applyToImageView(navIcon, R.drawable.ic_arrow_up_white_24dp, R.color.iadt_primary);
            collapsedContentView.setVisibility(View.VISIBLE);
        }
    }
}
