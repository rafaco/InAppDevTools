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

package es.rafaco.inappdevtools.library.view.overlay.screens.log;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.appcompat.widget.AppCompatButton;
//@import androidx.appcompat.widget.AppCompatTextView;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
//#endif

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import es.rafaco.compat.CardView;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.logic.navigation.NavigationStep;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.AnrDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.detail.NetworkDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class LogViewHolder extends RecyclerView.ViewHolder {

    LogAdapter.OnLogClickListener adapterClickListener;
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
    LinearLayout extra_wrapper;
    AppCompatTextView extra;
    View buttonsSeparator;
    AppCompatButton extra_button;
    ImageView overflow;

    public LogViewHolder(View view, LogAdapter.OnLogClickListener adapterClickListener) {
        super(view);
        this.adapterClickListener = adapterClickListener;

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
        extra_wrapper = view.findViewById(R.id.extra_wrapper);
        extra = view.findViewById(R.id.extra);
        extra_button = view.findViewById(R.id.extra_button);
        overflow = view.findViewById(R.id.overflow);
    }

    public void bindTo(final Friendly data, boolean isSelected, boolean isBeforeSelected) {
        uid = data.getUid();

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterClickListener.onItemClick(v, getAdapterPosition(), uid);
            }
        });
        //itemView.setOnLongClickListener(this);

        int cardColorId = isSelected ? R.color.iadt_surface_top : android.R.color.transparent;
        int cardColor = ContextCompat.getColor(wrapper.getContext(), cardColorId);
        card.setCardBackgroundColor(cardColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            card.setElevation(isSelected ? UiUtils.dpToPx(card.getContext(), 6) : 0);
            card.setRadius(isSelected ? UiUtils.dpToPx(card.getContext(), 12) : 0);
        }

        int severityColor = ContextCompat.getColor(itemView.getContext(), FriendlyLog.getColor(data));
        decorator.setBackgroundColor(severityColor);
        decorator.setText(data.getSeverity());

        int icon = FriendlyLog.getIcon(data);
        if (icon != -1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.icon.setImageDrawable(itemView.getContext().getDrawable(icon));
            } else {
                this.icon.setImageDrawable(itemView.getContext().getResources().getDrawable(icon));
            }
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
            title.setTypeface(Typeface.create(Typeface.SANS_SERIF, R.style.TextCondensedSmall));
            title.setTypeface(title.getTypeface(), Typeface.BOLD);
            title.setTextColor(severityColor);
        }
        title.setVisibility(View.VISIBLE);
        title.setText(data.getMessage());
        title.setSingleLine(!isSelected);
        title.setEllipsize(!isSelected ? TextUtils.TruncateAt.END : null);
        title.setBackgroundColor(Color.TRANSPARENT);

        titleSeparator.setVisibility(isSelected ? View.VISIBLE : View.GONE);

        if (isSelected){
            String details = getFormattedDetails(data);
            detail.setText(details);
            detailWrapper.setVisibility(View.VISIBLE);
        }
        else{
            detailWrapper.setVisibility(View.GONE);
        }

        if (isSelected){
            boolean showExtra = !isLogcat && !TextUtils.isEmpty(data.getExtra());
            extra_wrapper.setVisibility(showExtra ? View.VISIBLE : View.GONE);
            extra.setText(showExtra ? data.getExtra() : "");
        }
        else{
            extra_wrapper.setVisibility(View.GONE);
        }

        if(isSelected && getLink(data)!=null){
            extra_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OverlayService.performNavigationStep(LogViewHolder.this.getLink(data));
                }
            });
            extra_button.getBackground().setColorFilter(severityColor, PorterDuff.Mode.MULTIPLY);
            extra_button.setVisibility(View.VISIBLE);
            buttonsSeparator.setVisibility(View.VISIBLE);
        }else{
            extra_button.setOnClickListener(null);
            extra_button.setVisibility(View.GONE);
            buttonsSeparator.setVisibility(View.GONE);
        }

        if(isSelected){
            overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterClickListener.onOverflowClick(v, getAdapterPosition(), uid);
                }
            });
        }
        else{
            overflow.setOnClickListener(null);
        }
        overflow.setVisibility(isSelected ? View.VISIBLE : View.GONE);


        logSeparator.setVisibility(isSelected || isBeforeSelected ? View.GONE
                : View.VISIBLE);
    }

    public static String getFormattedDetails(Friendly data) {
        String details = "";
        if (data.isLogcat()){
            details = data.getExtra();
        }
        else{
            details += "Date: " + DateUtils.format(data.getDate()) + Humanizer.newLine();
            details += "Source: " + "Iadt Event" + Humanizer.newLine();
            details += "Category: " + data.getCategory() + Humanizer.newLine();
            details += "Subcategory: " + data.getSubcategory();
        }
        return details;
    }

    private NavigationStep getLink(Friendly data) {
        if(data.getSubcategory().equals("Crash")){
            return new NavigationStep(CrashDetailScreen.class, String.valueOf(data.getLinkedId()));
        }
        else if(data.getSubcategory().equals("Anr")){
            return new NavigationStep(AnrDetailScreen.class, String.valueOf(data.getLinkedId()));
        }
        else if(data.getCategory().equals("Network")){
            return new NavigationStep(NetworkDetailScreen.class, String.valueOf(data.getLinkedId()));
        }

        return null;
    }

    public void showPlaceholder(int position) {
        int color = ContextCompat.getColor(title.getContext(), R.color.rally_gray);

        decorator.setVisibility(View.VISIBLE);
        //decorator.setBackgroundColor(color);

        title.setVisibility(View.VISIBLE);
        //title.setBackgroundColor(color);

        icon.setVisibility(View.VISIBLE);
        //icon.setBackgroundColor(color);

        extra_wrapper.setVisibility(View.GONE);
        buttonsSeparator.setVisibility(View.GONE);
        extra_button.setVisibility(View.GONE);
        overflow.setVisibility(View.GONE);

        if (Humanizer.isEven(position)){
            title.setMaxWidth(title.getWidth()/2);
        }
    }
}
