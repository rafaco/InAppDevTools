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

package es.rafaco.inappdevtools.library.view.components.items;

import android.view.View;
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

import com.alorma.timeline.TimelineView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexViewHolder;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class TraceViewHolder extends FlexViewHolder {

    private final LinearLayout itemContent;
    private final CardView cardView;
    private final TimelineView timeline;
    private final ImageView navIcon;
    private final TextView title;
    private final TextView subtitle;
    private final TextView link;
    private final TextView tag;

    public TraceViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        this.itemContent = view.findViewById(R.id.item_content);
        this.cardView = view.findViewById(R.id.card_view);
        this.timeline = view.findViewById(R.id.timeline);
        this.title = view.findViewById(R.id.title);
        this.subtitle = view.findViewById(R.id.subtitle);
        this.tag = view.findViewById(R.id.tag);
        this.link = view.findViewById(R.id.link);
        this.navIcon = view.findViewById(R.id.icon);
    }

    @Override
    public void bindTo(Object abstractData, final int position) {
        final TraceItemData data = (TraceItemData) abstractData;
        if (data!=null){

            itemView.setActivated(data.isExpanded());
            itemContent.setVisibility(data.isExpanded()? View.VISIBLE : View.GONE);
            cardView.setVisibility(data.isExpanded()? View.VISIBLE : View.GONE);
            timeline.setVisibility(data.isExpanded()? View.VISIBLE : View.GONE);
            if (!data.isExpanded())
                return;

            int color = (data.getColor()>0) ? data.getColor() : R.color.iadt_text_low;

            timeline.setLineColor(ContextCompat.getColor(getContext(), R.color.iadt_trace_line));
            timeline.setIndicatorColor(ContextCompat.getColor(getContext(), color));
            timeline.setIndicatorSize(UiUtils.getPixelsFromDp(getContext(), 6));

            timeline.setTimelineAlignment(TimelineView.ALIGNMENT_MIDDLE);
            if (data.getPosition().equals(TraceItemData.Position.START)){
                title.setTextColor(ContextCompat.getColor(getContext(), R.color.rally_orange));
                timeline.setIndicatorColor(ContextCompat.getColor(getContext(), color));
                timeline.setTimelineType(TimelineView.TYPE_START);
            }
            else if (data.getPosition().equals(TraceItemData.Position.END)){
                title.setTextColor(ContextCompat.getColor(getContext(), R.color.rally_yellow));
                timeline.setTimelineType(TimelineView.TYPE_END);
            }else{
                title.setTextColor(ContextCompat.getColor(getContext(), R.color.rally_yellow));
                timeline.setTimelineType(TimelineView.TYPE_MIDDLE);
            }

            title.setText(data.getTitle());
            subtitle.setText(data.getSubtitle());
            if (data.getColor()>0)
                subtitle.setTextColor(ContextCompat.getColor(getContext(), color));

            tag.setText(data.getTag());
            tag.setTextColor(ContextCompat.getColor(getContext(), color));
            UiUtils.setStrokeToDrawable(getContext(), 1, color, tag.getBackground());

            link.setText(data.getLink());

            if (data.isOpenable() && data.getPerformer()!=null){
                cardView.setClickable(true);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.getPerformer().run();
                    }
                });
                
                cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.iadt_surface_top));
                cardView.setCardElevation(UiUtils.getPixelsFromDp(getContext(), 12));

                link.setTextColor(ContextCompat.getColor(getContext(), R.color.iadt_text_high));
                navIcon.setVisibility(View.VISIBLE);
            }
            else{
                cardView.setOnClickListener(null);
                cardView.setClickable(false);

                cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.iadt_surface_medium));
                cardView.setCardElevation(UiUtils.getPixelsFromDp(getContext(), 6));

                link.setTextColor(ContextCompat.getColor(getContext(), R.color.iadt_text_low));
                navIcon.setVisibility(View.GONE);
            }
        }
    }
}
