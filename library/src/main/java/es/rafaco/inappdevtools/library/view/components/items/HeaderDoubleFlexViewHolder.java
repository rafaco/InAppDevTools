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

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.base.FlexItemViewHolder;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;
import es.rafaco.inappdevtools.library.view.utils.ImageLoaderAsyncTask;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
//#endif

public class HeaderDoubleFlexViewHolder extends FlexItemViewHolder
        implements CollapsibleFlexViewHolder.ICollapsibleViewHolder {

    private final TextView iconView;
    private final TextView titleView;
    private final TextView contentView;
    private final TextView navIcon;
    private final ImageView imageView;
    private final RelativeLayout mainContainer;
    private final LinearLayout navAddContainer;
    private final TextView navAddIcon;
    private View navAddSeparator;
    private boolean overwrittenCollapsableIcon;

    public HeaderDoubleFlexViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
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
        super.bindTo(abstractData, position);

        final HeaderDoubleFlexData data = (HeaderDoubleFlexData) abstractData;
        if (data!=null){

            bindTitle(data);
            bindContent(data);
            bindImage(data);
            bindHeaderIcon(data);
            bindMainIcon(data);
            bindMainPerformer(data);
            bindExtraPerformer(data);

            applyExpandedState(data.isExpanded());
        }
    }

    private void bindTitle(HeaderDoubleFlexData data) {
        titleView.setText(data.getTitle());
        if (data.getTitleColor()>0){
            titleView.setTextColor(ContextCompat.getColor(getContext(), data.getTitleColor()));
        }
    }

    private void bindContent(HeaderDoubleFlexData data) {
        contentView.setVisibility(TextUtils.isEmpty(data.getContent()) ? View.GONE : View.VISIBLE);
        contentView.setText(data.getContent());
    }

    private void bindImage(HeaderDoubleFlexData data) {
        if (!TextUtils.isEmpty(data.getImagePath())){
            new ImageLoaderAsyncTask(imageView).execute(data.getImagePath());
        }
        else {
            imageView.setVisibility(View.GONE);
        }
    }

    private void bindHeaderIcon(HeaderDoubleFlexData data) {
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

    private void bindMainIcon(final HeaderDoubleFlexData data) {
        if (data.getNavCount()>-1){
            navIcon.setText(data.getNavCount()+"");
        }
        else{
            int navIconRes = data.getNavIcon()>0 ? data.getNavIcon()
                    : R.string.gmd_keyboard_arrow_right;
            IconUtils.applyToTextView(navIcon, navIconRes, R.color.iadt_primary);
        }
        navIcon.setVisibility(View.VISIBLE);

        if (data.getNavCount()>-1 || data.getNavIcon()>0){
            overwrittenCollapsableIcon = true;
        }
    }

    private void bindMainPerformer(final HeaderDoubleFlexData data) {
        if (data.getPerformer() != null){
            mainContainer.setClickable(true);
            mainContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.getPerformer().run();
                }
            });
        }
        else{
            mainContainer.setClickable(false);
            mainContainer.setFocusable(false);
            mainContainer.setFocusableInTouchMode(false);
        }
    }

    private void bindExtraPerformer(final HeaderDoubleFlexData data) {
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

    @Override
    public void applyExpandedState(Boolean isExpanded) {
        if (overwrittenCollapsableIcon)
            return;

        if (navIcon.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) navIcon.getLayoutParams();
            int margin = (int) UiUtils.dpToPx(navIcon.getContext(), 5);
            p.setMargins(p.leftMargin, p.topMargin, margin, p.bottomMargin);
        }

        if (!isExpanded){
            IconUtils.applyToTextView(navIcon, R.string.gmd_keyboard_arrow_down, R.color.iadt_primary);

        }
        else {
            IconUtils.applyToTextView(navIcon, R.string.gmd_keyboard_arrow_up, R.color.iadt_primary);
        }
    }
}
