/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.view.overlay.screens.log;

import android.graphics.Color;
import android.graphics.Typeface;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.appcompat.widget.AppCompatTextView;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
//#endif

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.inappdevtools.compat.CardView;
import org.inappdevtools.library.logic.log.FriendlyLog;
import org.inappdevtools.library.storage.db.entities.Friendly;
import org.inappdevtools.library.view.components.FlexDescriptor;
import org.inappdevtools.library.view.components.FlexLoader;
import org.inappdevtools.library.view.components.base.FlexData;
import org.inappdevtools.library.view.components.groups.LinearGroupFlexData;
import org.inappdevtools.library.view.components.items.ButtonFlexData;
import org.inappdevtools.library.view.components.items.HeaderFlexData;
import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.R;

import org.inappdevtools.library.view.overlay.OverlayService;
import org.inappdevtools.library.view.utils.Humanizer;
import org.inappdevtools.library.view.utils.UiUtils;

public class LogViewHolder extends RecyclerView.ViewHolder {

    Listener listener;
    long uid;

    ImageView icon;
    AppCompatTextView decorator;
    AppCompatTextView title;

    LinearLayout wrapper;
    CardView card;
    View logSeparator;
    View titleSeparator;
    LinearLayout detailWrapper;
    AppCompatTextView detail;
    View buttonsSeparator;
    FrameLayout buttonGroupContainer;

    public LogViewHolder(View view, Listener listener) {
        super(view);
        this.listener = listener;

        wrapper = view.findViewById(R.id.wrapper);
        card = view.findViewById(R.id.card_view);
        logSeparator = view.findViewById(R.id.log_separator);
        decorator = view.findViewById(R.id.decorator);
        title = view.findViewById(R.id.title);
        icon = view.findViewById(R.id.icon);
        titleSeparator = view.findViewById(R.id.title_separator);
        buttonsSeparator = view.findViewById(R.id.buttons_separator);
        detailWrapper = view.findViewById(R.id.detail_wrapper);
        detail = view.findViewById(R.id.detail);
        buttonGroupContainer = view.findViewById(R.id.button_group_container);
    }

    public interface Listener {
        boolean isWrapLines();
        boolean isSelected(long id);
        boolean isBeforeSelected(int position);
        void onItemClick(View itemView, int position, long id);
        void onOverflowClick(long id);
    }

    public void bindTo(final Friendly data, int position) {
        boolean isSelected = listener.isSelected(data.getUid());
        boolean isBeforeSelected = listener.isBeforeSelected(position);

        final LogLineFormatter formatter = new LogLineFormatter(data);

        if(LogScreen.isLogDebug() && isSelected){
            Log.d(Iadt.TAG, "LogScreen - bindTo selection " + data.getUid() + " at " + position);
        }

        uid = data.getUid();
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, getAdapterPosition(), uid);
            }
        });

        int cardColorId = isSelected ? R.color.iadt_surface_top : android.R.color.transparent;
        int cardColor = ContextCompat.getColor(wrapper.getContext(), cardColorId);
        card.setCardBackgroundColor(cardColor);
        card.setRadius(isSelected ? UiUtils.dpToPx(card.getContext(), 6) : 0);
        card.setCardElevation(isSelected ? UiUtils.dpToPx(card.getContext(), 6) : 0);

        int severityColor = ContextCompat.getColor(itemView.getContext(), FriendlyLog.getColor(data));
        decorator.setBackgroundColor(severityColor);
        decorator.setText(data.getSeverity());

        int icon = FriendlyLog.getIcon(data);
        if (icon != -1){
            this.icon.setImageDrawable(UiUtils.getDrawable(icon));
            this.icon.setColorFilter(severityColor);
            this.icon.setVisibility(View.VISIBLE);
            this.icon.setBackgroundColor(Color.TRANSPARENT);
        }else{
            this.icon.setVisibility(View.GONE);
        }

        boolean isLogcat = data.isLogcat();
        if (isLogcat){
            title.setTypeface(Typeface.create(Typeface.MONOSPACE, R.style.TextMonospaceSmall));
            title.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_white));
        }else{
            title.setTypeface(Typeface.create(Typeface.SANS_SERIF, R.style.TextCondensed));
            title.setTypeface(title.getTypeface(), Typeface.BOLD);
            title.setTextColor(severityColor);
        }
        title.setVisibility(View.VISIBLE);
        title.setBackgroundColor(Color.TRANSPARENT);
        boolean wrapMessage = isSelected || listener.isWrapLines();
        title.setSingleLine(!wrapMessage);
        title.setEllipsize(!wrapMessage ? TextUtils.TruncateAt.END : null);
        title.setText(data.getMessage());

        titleSeparator.setVisibility(isSelected ? View.VISIBLE : View.GONE);

        if (isSelected){
            String details = formatter.getOneLineHeader();
            detail.setText(details);
            detailWrapper.setVisibility(View.VISIBLE);
        }
        else{
            detailWrapper.setVisibility(View.GONE);
        }

        if(isSelected){
            LinearGroupFlexData linearGroupData = new LinearGroupFlexData();
            linearGroupData.setHorizontal(true);
            linearGroupData.setChildLayout(FlexData.LayoutType.SAME_WIDTH);

            if (!isLogcat && formatter.getLinkStep() !=null){
                ButtonFlexData linkedButton = new ButtonFlexData(formatter.getLinkName(),
                        formatter.getLinkIcon(),
                        new Runnable() {
                            @Override
                            public void run() {
                                OverlayService.performNavigationStep(formatter.getLinkStep());
                            }
                        });
                linkedButton.setColor(R.color.material_blue_900);
                linearGroupData.add(linkedButton);
            }
            else{
                HeaderFlexData placeholder = new HeaderFlexData(" ");
                placeholder.setHorizontalMargin(false);
                placeholder.setVerticalMargin(false);
                linearGroupData.add(placeholder);
            }

            ButtonFlexData moreButton = new ButtonFlexData("Details",
                    R.drawable.ic_info_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            listener.onOverflowClick(uid);
                        }
                    });
            moreButton.setColor(R.color.rally_dark_green);
            linearGroupData.add(moreButton);

            FlexDescriptor desc = FlexLoader.getDescriptor(LinearGroupFlexData.class);
            desc.addToView(linearGroupData, buttonGroupContainer);
            buttonGroupContainer.setVisibility(View.VISIBLE);
            buttonsSeparator.setVisibility(View.VISIBLE);
        }else{
            buttonGroupContainer.removeAllViews();
            buttonGroupContainer.setVisibility(View.GONE);
            buttonsSeparator.setVisibility(View.GONE);
        }

        logSeparator.setVisibility(isSelected || isBeforeSelected ? View.GONE
                : View.VISIBLE);
    }

    public void showPlaceholder(int position) {
        decorator.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);

        buttonsSeparator.setVisibility(View.GONE);
        buttonGroupContainer.setVisibility(View.GONE);

        if (Humanizer.isEven(position)){
            title.setMaxWidth(title.getWidth()/2);
        }
    }
}
