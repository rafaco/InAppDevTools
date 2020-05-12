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
import android.widget.RelativeLayout;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.cardview.widget.CardView;
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
//#endif

import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.components.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexibleItemDescriptor;
import es.rafaco.inappdevtools.library.view.components.FlexibleLoader;
import es.rafaco.inappdevtools.library.view.components.FlexibleViewHolder;
import es.rafaco.inappdevtools.library.view.components.groups.ButtonGroupData;
import es.rafaco.inappdevtools.library.view.components.groups.LinearGroupData;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.MarginUtils;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;


public class FlatCardViewHolder extends FlexibleViewHolder {

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
    protected final RelativeLayout headerArea;

    public FlatCardViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.cardView = view.findViewById(R.id.flat_card_view);
        this.headerArea = view.findViewById(R.id.header_area);
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
        final FlatCardData data = (FlatCardData) abstractData;
        if (data!=null){

            itemView.setClickable(false);
            itemView.setActivated(false);

            cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(),
                    R.color.rally_orange_alpha));
            cardView.setCardElevation(UiUtils.getPixelsFromDp(cardView.getContext(), 2));
            MarginUtils.setHorizontalMargin(headerArea);
            MarginUtils.setHorizontalMargin(contentView);

            bindHeader(data);
            bindContent(data);
            bindExpandedState(position, data);

            bindInternalData(data);
            bindButtons(data);
        }
    }

    private void bindHeader(FlatCardData data) {
        int icon = data.getIcon();
        if (icon>0){
            IconUtils.markAsIconContainer(iconView, IconUtils.MATERIAL);
            iconView.setText(icon);
            iconView.setVisibility(View.VISIBLE);
        }else{
            iconView.setVisibility(View.GONE);
        }

        titleView.setText(data.getTitle());
        overviewView.setText(data.getOverview());

        UiUtils.setBackground(navIcon, null);
        navIcon.setVisibility(View.VISIBLE);
    }

    private void bindContent(FlatCardData data) {
        String content = data.entriesToString();
        //TODO: improve entriesToString to avoid next line
        content = Humanizer.trimNewlines(content);
        contentView.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);
        contentView.setText(content);
    }

    private void bindExpandedState(final int position, final FlatCardData data) {
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
                cardView.setCardElevation(UiUtils.getPixelsFromDp(itemView.getContext(), 0));
            }
        }
    }

    private void bindInternalData(FlatCardData data) {
        if (data.getInternalData()==null || data.getInternalData().isEmpty()){
            internalSeparator.setVisibility(View.GONE);
            internalContainer.setVisibility(View.GONE);
            internalContainer.removeAllViews();
            return;
        }
        internalSeparator.setVisibility(View.VISIBLE);
        internalContainer.setVisibility(View.VISIBLE);
        internalContainer.removeAllViews();
        LinearGroupData internalList = new LinearGroupData(data.getInternalData());
        internalList.setShowDividers(true);
        FlexibleItemDescriptor desc = FlexibleLoader.getDescriptor(LinearGroupData.class);
        desc.addToView(internalList, internalContainer);
    }


    private void bindButtons(FlatCardData data) {
        List<RunButton> buttons = data.getButtons();
        if (buttons == null || buttons.isEmpty()){
            buttonSeparator.setVisibility(View.GONE);
            buttonGroupContainer.setVisibility(View.GONE);
        }
        else{
            buttonSeparator.setVisibility(View.VISIBLE);
            buttonGroupContainer.setVisibility(View.VISIBLE);
            buttonGroupContainer.removeAllViews();
            for (RunButton button : buttons) {
                button.setColor(R.color.iadt_surface_bottom);
            }
            ButtonGroupData buttonGroupData = new ButtonGroupData(buttons);
            buttonGroupData.setWrapContent(true);
            buttonGroupData.setBorderless(true);
            FlexibleItemDescriptor desc = FlexibleLoader.getDescriptor(ButtonGroupData.class);
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
