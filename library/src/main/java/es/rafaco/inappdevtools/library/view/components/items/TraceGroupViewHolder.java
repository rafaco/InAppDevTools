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

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
//#endif

import com.alorma.timeline.TimelineView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexViewHolder;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.TraceGrouper;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class TraceGroupViewHolder extends FlexViewHolder {

    private final LinearLayout details;
    TimelineView timeline;
    TextView tag;
    TextView afterTag;
    TextView iconLabel;
    ImageView icon;

    public TraceGroupViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        this.timeline = view.findViewById(R.id.timeline);
        this.details = view.findViewById(R.id.details);
        this.tag = view.findViewById(R.id.tag);
        this.afterTag = view.findViewById(R.id.after_tag);
        this.iconLabel = view.findViewById(R.id.icon_label);
        this.icon = view.findViewById(R.id.icon);
    }

    @Override
    public void onCreate(ViewGroup viewGroup, int viewType) {
        super.onCreate(viewGroup, viewType);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final TraceGroupItem data = (TraceGroupItem) abstractData;
        if (data!=null){

            itemView.setActivated(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.setExpanded(!data.isExpanded());
                    new TraceGrouper().propagateExpandedState(data, parentAdapter.getItems());
                    parentAdapter.notifyItemRangeChanged(data.getIndex(), data.getCount()+1);
                }
            });

            if (!data.isExpanded()){
                timeline.setIndicatorSize(UiUtils.getPixelsFromDp(getContext(), 6));
            }else{
                timeline.setIndicatorSize(0);
            }

            timeline.setIndicatorColor(ContextCompat.getColor(getContext(), data.getColor()));
            timeline.setTimelineAlignment(TimelineView.ALIGNMENT_MIDDLE);
            timeline.setLineColor(ContextCompat.getColor(getContext(), R.color.iadt_trace_line));

            if (!data.isExpanded() && data.isLastOnCollapsed()){
                timeline.setTimelineType(TimelineView.TYPE_END);
            }else{
                timeline.setTimelineType(TimelineView.TYPE_MIDDLE);
            }

            tag.setText(data.getTag());
            tag.setTextColor(ContextCompat.getColor(getContext(), data.getColor()));
            UiUtils.setStrokeToDrawable(getContext(), 1, data.getColor(), tag.getBackground());

            tag.setVisibility(!data.isExpanded() ? View.VISIBLE : View.GONE);
            afterTag.setVisibility(View.GONE);
            iconLabel.setVisibility(View.VISIBLE);
            if (!data.isExpanded()){
                iconLabel.setText(data.getCount() + " more ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    UiUtils.setBackground(icon, ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_down_white_24dp));
                }
            }
            else {
                iconLabel.setText("Hide " + data.getCount() + " ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    UiUtils.setBackground(icon, ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_up_white_24dp));
                }
            }
        }
    }
}
