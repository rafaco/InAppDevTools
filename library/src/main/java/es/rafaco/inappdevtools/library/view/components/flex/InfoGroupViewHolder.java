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

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.info.data.InfoGroupData;
import es.rafaco.inappdevtools.library.logic.runnables.ButtonGroupData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoScreen;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.cardview.widget.CardView;
//@import androidx.appcompat.widget.AppCompatButton;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.AppCompatButton;
//#endif

public class InfoGroupViewHolder extends FlexibleViewHolder {

    private final CardView cardView;
    private final TextView iconView;
    private final TextView titleView;
    private final TextView overviewView;
    private final TextView contentView;
    private final ImageView navIcon;
    private final LinearLayout collapsedContentView;
    private final FrameLayout buttonGroupContainer;
    private final View buttonSeparator;

    public InfoGroupViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.cardView = view.findViewById(R.id.card_view);
        this.iconView = view.findViewById(R.id.icon);
        this.titleView = view.findViewById(R.id.title);
        this.overviewView = view.findViewById(R.id.overview);
        this.collapsedContentView = view.findViewById(R.id.collapsedContent);
        this.contentView = view.findViewById(R.id.content);
        this.navIcon = view.findViewById(R.id.nav_icon);
        this.buttonGroupContainer = view.findViewById(R.id.button_group_container);
        this.buttonSeparator = view.findViewById(R.id.button_separator);
    }

    @Override
    public void bindTo(Object abstractData, final int position) {
        final InfoGroupData data = (InfoGroupData) abstractData;
        if (data!=null){

            itemView.setActivated(true);

            titleView.setText(data.getTitle());
            overviewView.setText(data.getOverview());

            String content = data.entriesToString();
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

            boolean isExpandable = true;
            if (isExpandable){

                navIcon.setBackground(null);

                applyExpandedState(data.getExpanded());
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleExpandedState(position);
                    }
                });
                
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.iadt_surface_top));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cardView.setElevation(UiUtils.getPixelsFromDp(itemView.getContext(), 3));
                }
                itemView.setClickable(true);
                navIcon.setVisibility(View.VISIBLE);

                if (data.getButtons() == null || data.getButtons().isEmpty()){
                    buttonSeparator.setVisibility(View.GONE);
                    buttonGroupContainer.setVisibility(View.GONE);
                }
                else{
                    FlexibleItemDescriptor desc = new FlexibleItemDescriptor(ButtonGroupData.class,
                            ButtonGroupViewHolder.class, R.layout.flexible_item_button_group);
                    ButtonGroupData buttonGroupData = new ButtonGroupData(data.getButtons());
                    desc.addToView(desc, buttonGroupData, buttonGroupContainer);
                    buttonSeparator.setVisibility(View.VISIBLE);
                    buttonGroupContainer.setVisibility(View.VISIBLE);
                }
            }
            else{
                //TODO: never used code
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.iadt_surface_bottom));
                cardView.setClickable(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cardView.setElevation(0);
                }
                navIcon.setVisibility(View.GONE);
                cardView.setOnClickListener(null);
                itemView.setClickable(false);
            }
        }
    }

    private void toggleExpandedState(int position) {
        boolean isExpanded = ((InfoScreen)adapter.getScreen()).toggleExpandedState(position);
        applyExpandedState(isExpanded);
    }

    private void applyExpandedState(boolean isExpanded) {
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
