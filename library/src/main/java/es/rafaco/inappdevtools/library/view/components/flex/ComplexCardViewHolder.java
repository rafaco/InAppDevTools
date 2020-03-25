/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.view.components.flex;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.cardview.widget.CardView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
//#endif

import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.runnables.ButtonGroupData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class ComplexCardViewHolder extends FlexibleViewHolder {

    private final CardView cardView;
    private final TextView iconView;
    private final TextView titleView;
    private final TextView overviewView;
    private final View contentSeparator;
    private final TextView contentView;
    private final ImageView navIcon;
    private final LinearLayout collapsedContentView;
    private final FrameLayout buttonGroupContainer;
    private final View buttonSeparator;

    public ComplexCardViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.cardView = view.findViewById(R.id.card_view);
        this.iconView = view.findViewById(R.id.icon);
        this.titleView = view.findViewById(R.id.title);
        this.overviewView = view.findViewById(R.id.overview);
        this.collapsedContentView = view.findViewById(R.id.collapsedContent);
        this.contentSeparator = view.findViewById(R.id.content_separator);
        this.contentView = view.findViewById(R.id.content);
        this.navIcon = view.findViewById(R.id.nav_icon);
        this.buttonGroupContainer = view.findViewById(R.id.button_group_container);
        this.buttonSeparator = view.findViewById(R.id.button_separator);
    }

    @Override
    public void bindTo(Object abstractData, final int position) {
        final DocumentSectionData data = (DocumentSectionData) abstractData;
        if (data!=null){

            itemView.setActivated(true);

            titleView.setText(data.getTitle());
            overviewView.setText(data.getOverview());

            String content = data.entriesToString();
            //TODO: improve entriesToString to avoid next line
            content = Humanizer.trimNewlines(content);
            contentView.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);
            contentView.setText(content);

            int icon = data.getIcon();
            if (icon>0){
                IconUtils.markAsIconContainer(iconView, IconUtils.MATERIAL);
                iconView.setText(icon);
                iconView.setVisibility(View.VISIBLE);
            }else{
                iconView.setVisibility(View.GONE);
            }

            if (data.isExpandable()) {
                contentSeparator.setVisibility(View.VISIBLE);
                applyExpandedState(data.isExpanded());
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleExpandedState(position);
                    }
                });

            }else {
                contentSeparator.setVisibility(View.GONE);
                collapsedContentView.setVisibility(View.VISIBLE);
                cardView.setOnClickListener(null);
            }
            cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.iadt_surface_top));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setElevation(UiUtils.getPixelsFromDp(itemView.getContext(), 3));
            }
            itemView.setClickable(true);
            navIcon.setBackground(null);
            navIcon.setVisibility(View.VISIBLE);

            List<RunButton> buttons = data.getButtons();
            if (!data.isExpandable() || buttons == null || buttons.isEmpty()){
                buttonSeparator.setVisibility(View.GONE);
            }
            else {
                buttonSeparator.setVisibility(View.VISIBLE);
            }

            if (buttons == null || buttons.isEmpty()){
                buttonGroupContainer.setVisibility(View.GONE);
            }
            else{
                buttonGroupContainer.removeAllViews();
                for (RunButton button :
                        buttons) {
                        button.setColor(R.color.iadt_surface_bottom);
                };
                ButtonGroupData buttonGroupData = new ButtonGroupData(buttons);
                buttonGroupData.setWrapContent(true);
                buttonGroupData.setBorderless(true);
                FlexibleItemDescriptor desc = new FlexibleItemDescriptor(ButtonGroupData.class,
                        ButtonGroupViewHolder.class, R.layout.flexible_item_button_group);
                desc.addToView(desc, buttonGroupData, buttonGroupContainer);
                buttonGroupContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private void toggleExpandedState(int position) {
        Boolean isExpanded = (Boolean) adapter.performItemAction(this, null, position, -1);
        applyExpandedState(isExpanded);
    }

    private void applyExpandedState(Boolean isExpanded) {
        if (!isExpanded){
            IconUtils.applyToImageView(navIcon, R.drawable.ic_arrow_down_white_24dp, R.color.rally_white);
            collapsedContentView.setVisibility(View.GONE);
        }
        else {
            IconUtils.applyToImageView(navIcon, R.drawable.ic_arrow_up_white_24dp, R.color.rally_white);
            collapsedContentView.setVisibility(View.VISIBLE);
        }
    }
}
