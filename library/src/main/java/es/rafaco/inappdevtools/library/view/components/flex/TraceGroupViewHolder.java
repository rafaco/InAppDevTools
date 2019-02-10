package es.rafaco.inappdevtools.library.view.components.flex;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.timeline.TimelineView;

import java.util.List;

import androidx.core.content.ContextCompat;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.TraceGrouper;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class TraceGroupViewHolder extends FlexibleViewHolder {

    private final LinearLayout details;
    TimelineView timeline;
    TextView title;
    TextView subtitle;

    public TraceGroupViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.timeline = view.findViewById(R.id.timeline);
        this.details = view.findViewById(R.id.details);
        this.title = view.findViewById(R.id.title);
        this.subtitle = view.findViewById(R.id.subtitle);
        //this.count = view.findViewById(R.id.count);
    }

    @Override
    public void onCreate(ViewGroup viewGroup, int viewType) {
        super.onCreate(viewGroup, viewType);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        TraceGroupItem data = (TraceGroupItem) abstractData;
        if (data!=null){

            itemView.setActivated(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.setExpanded(!data.isExpanded());
                    new TraceGrouper().propagateExpandedState(data, adapter.getItems());
                    adapter.notifyItemRangeChanged(data.getIndex(), data.getCount()+1);
                }
            });

            if (!data.isExpanded()){
                timeline.setIndicatorSize(UiUtils.getPixelsFromDp(itemView.getContext(), 5));
            }else{
                timeline.setIndicatorSize(0);
            }

            timeline.setIndicatorColor(Color.GRAY);
            timeline.setTimelineAlignment(TimelineView.ALIGNMENT_MIDDLE);

            if (!data.isExpanded() && data.getLastOnCollapsed()){
                timeline.setTimelineType(TimelineView.TYPE_END);
            }else{
                timeline.setTimelineType(TimelineView.TYPE_MIDDLE);
            }

            title.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_gray));
            subtitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_gray));
            if (!data.isExpanded()){
                title.setText(data.getCount() + " more from: " + data.getGroupKey());
                subtitle.setVisibility(View.GONE);
            }
            else {
                title.setVisibility(View.GONE);
                subtitle.setText("Hide " + data.getGroupKey());
            }
        }
    }
}
