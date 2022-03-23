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

package org.inappdevtools.library.view.components.items;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alorma.timeline.TimelineView;

import org.inappdevtools.library.R;
import org.inappdevtools.library.view.components.FlexAdapter;
import org.inappdevtools.library.view.components.base.ContainerFlexViewHolder;
import org.inappdevtools.library.view.utils.UiUtils;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
//#endif

public class TimelineFlexViewHolder extends ContainerFlexViewHolder {

    private final TimelineView timeline;
    private final FrameLayout timecontent;

    public TimelineFlexViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        this.timeline = view.findViewById(R.id.timeline);
        this.timecontent = view.findViewById(R.id.timecontent);
    }

    @Override
    public void bindTo(Object abstractData, final int position) {
        super.bindTo(abstractData, position);

        final TimelineFlexData data = (TimelineFlexData) abstractData;
        if (data!=null){
            int lineColor = (data.getLineColor()>0) ? data.getLineColor() : R.color.iadt_trace_line;
            int indicatorColor = (data.getIndicatorColor()>0) ? data.getIndicatorColor() : R.color.iadt_text_low;
            float indicatorSize = (data.getIndicatorSize()>0) ? data.getIndicatorSize() : 8;

            timeline.setLineColor(ContextCompat.getColor(getContext(), lineColor));

//#ifdef SUPPORT
            timeline.setIndicatorColor(ContextCompat.getColor(getContext(), indicatorColor));
            timeline.setIndicatorSize(UiUtils.getPixelsFromDp(getContext(), indicatorSize));
            timeline.setTimelineAlignment(TimelineView.ALIGNMENT_MIDDLE);

            if (data.getPosition().equals(TimelineFlexData.Position.START)){
                timeline.setIndicatorColor(ContextCompat.getColor(getContext(), indicatorColor));
                timeline.setTimelineType(TimelineView.TYPE_START);
            }
            else if (data.getPosition().equals(TimelineFlexData.Position.END)){
                timeline.setTimelineType(TimelineView.TYPE_END);
            }
            else{
                timeline.setTimelineType(TimelineView.TYPE_MIDDLE);
            }
//#endif
        }
    }

    @Override
    public ViewGroup getChildContainer() {
        return timecontent;
    }
}
