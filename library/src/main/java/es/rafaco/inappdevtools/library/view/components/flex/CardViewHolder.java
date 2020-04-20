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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;
import es.rafaco.inappdevtools.library.view.utils.ImageLoaderAsyncTask;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.cardview.widget.CardView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
//#endif

public class CardViewHolder extends FlexibleViewHolder {

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

    public CardViewHolder(View view, FlexibleAdapter adapter) {
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

            titleView.setText(data.getTitle());
            if (data.getTitleColor()>0){
                titleView.setTextColor(ContextCompat.getColor(itemView.getContext(), data.getTitleColor()));
            }

            contentView.setVisibility(TextUtils.isEmpty(data.getContent()) ? View.GONE : View.VISIBLE);
            contentView.setText(data.getContent());

            if (!TextUtils.isEmpty(data.getImagePath())){
                new ImageLoaderAsyncTask(imageView).execute(data.getImagePath());
            }
            else {
                imageView.setVisibility(View.GONE);
            }

            int icon = data.getIcon();
            if (icon>0){
                IconUtils.markAsIconContainer(iconView, IconUtils.MATERIAL);
                iconView.setText(data.getIcon());
                iconView.setVisibility(View.VISIBLE);
                if (data.getTitleColor()>0){
                    iconView.setTextColor(ContextCompat.getColor(itemView.getContext(), data.getTitleColor()));
                }
            }else{
                iconView.setVisibility(View.GONE);
            }


            if (data.getBgColor()>0){
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), data.getBgColor()));
            }
            else{
                if (data.getPerformer() != null){
                    cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.material_surface_medium));
                }else{
                    cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.iadt_surface_bottom));
                }
            }

            if (data.getPerformer() != null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cardView.setElevation(UiUtils.getPixelsFromDp(itemView.getContext(), 3));
                }
                mainContainer.setClickable(true);
                mainContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.getPerformer().run();
                    }
                });

                if (data.getNavCount()>-1){
                    navIcon.setText(data.getNavCount()+"");
                    //navIcon.setTextSize(UiUtils.getPixelsFromDp(itemView.getContext(), 10));
                }
                else{
                    IconUtils.markAsIconContainer(navIcon, IconUtils.MATERIAL);
                    int navIconRes = data.getNavIcon()>0 ? data.getNavIcon()
                            : R.string.gmd_keyboard_arrow_right;
                    navIcon.setText(navIconRes);
                }

                navIcon.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.iadt_primary));
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

            if (data.getNavAddRunnable() != null){
                IconUtils.markAsIconContainer(navAddIcon, IconUtils.MATERIAL);
                int navAddIconRes = data.getNavAddIcon()>0 ? data.getNavAddIcon()
                        : R.string.gmd_add;
                navAddIcon.setText(navAddIconRes);
                navAddIcon.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.iadt_primary));

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
}
