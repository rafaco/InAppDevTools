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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.cardview.widget.CardView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
//#endif

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexViewHolder;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;
import es.rafaco.inappdevtools.library.view.utils.ImageLoaderAsyncTask;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class CardViewHolder extends FlexViewHolder {

    private final LinearLayout itemContent;
    private final CardView cardView;
    private final TextView iconView;
    private final TextView titleView;
    private final TextView contentView;
    private final TextView navIcon;
    private final ImageView imageView;
    private final RelativeLayout mainContainer;
    private final LinearLayout navAddContainer;
    private final TextView navAddIcon;
    private View navAddSeparator;

    public CardViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        this.itemContent = view.findViewById(R.id.item_content);
        this.cardView = view.findViewById(R.id.card_view);
        this.mainContainer = view.findViewById(R.id.main_container);
        this.iconView = view.findViewById(R.id.icon);
        this.imageView = view.findViewById(R.id.image_left);
        this.titleView = view.findViewById(R.id.title);
        this.contentView = view.findViewById(R.id.content);
        this.navIcon = view.findViewById(R.id.nav_icon);
        this.navAddContainer = view.findViewById(R.id.nav_add_container);
        this.navAddSeparator = view.findViewById(R.id.nav_add_separator);
        this.navAddIcon = view.findViewById(R.id.nav_add_icon);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final CardData data = (CardData) abstractData;
        if (data!=null){

            itemView.setActivated(true);

            bindTitle(data);
            bindContent(data);
            bindImage(data);
            bindMainIcon(data);
            bindBackgroundColor(data);
            bindMainPerformer(data);
            bindExtraPerformer(data);
        }
    }

    private void bindTitle(CardData data) {
        titleView.setText(data.getTitle());
        if (data.getTitleColor()>0){
            titleView.setTextColor(ContextCompat.getColor(getContext(), data.getTitleColor()));
        }
    }

    private void bindContent(CardData data) {
        contentView.setVisibility(TextUtils.isEmpty(data.getContent()) ? View.GONE : View.VISIBLE);
        contentView.setText(data.getContent());
    }

    private void bindImage(CardData data) {
        if (!TextUtils.isEmpty(data.getImagePath())){
            new ImageLoaderAsyncTask(imageView).execute(data.getImagePath());
        }
        else {
            imageView.setVisibility(View.GONE);
        }
    }

    private void bindMainIcon(CardData data) {
        int icon = data.getIcon();
        if (icon>0){
            IconUtils.set(iconView, icon);
            iconView.setVisibility(View.VISIBLE);
            if (data.getTitleColor()>0){
                iconView.setTextColor(ContextCompat.getColor(getContext(), data.getTitleColor()));
            }
        }else{
            iconView.setVisibility(View.GONE);
        }
    }

    private void bindBackgroundColor(CardData data) {
        if (data.getBgColor()>0){
            cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), data.getBgColor()));
        }
        else if (data.getPerformer() == null){
            cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.iadt_surface_bottom));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setCardElevation(UiUtils.getPixelsFromDp(getContext(), 2));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mainContainer.setBackground(null);
            }
        }
    }

    private void bindMainPerformer(final CardData data) {
        if (data.getPerformer() != null){
            mainContainer.setClickable(true);
            mainContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.getPerformer().run();
                }
            });

            if (data.getNavCount()>-1){
                navIcon.setText(data.getNavCount()+"");
            }
            else{
                int navIconRes = data.getNavIcon()>0 ? data.getNavIcon()
                        : R.string.gmd_keyboard_arrow_right;
                IconUtils.set(navIcon, navIconRes);
            }
            navIcon.setVisibility(View.VISIBLE);
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setElevation(0);
            }
            mainContainer.setClickable(false);
            mainContainer.setOnClickListener(null);
            navIcon.setVisibility(View.GONE);
        }
    }

    private void bindExtraPerformer(final CardData data) {
        if (data.getNavAddRunnable() != null){
            int navAddIconRes = data.getNavAddIcon()>0 ? data.getNavAddIcon() : R.string.gmd_add;
            IconUtils.set(navAddIcon, navAddIconRes);
            navAddContainer.setClickable(true);
            navAddContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.getNavAddRunnable().run();
                }
            });
            navAddContainer.setVisibility(View.VISIBLE);
            navAddSeparator.setVisibility(View.VISIBLE);
        }
        else{
            navAddContainer.setVisibility(View.GONE);
            navAddSeparator.setVisibility(View.GONE);
        }
    }
}
