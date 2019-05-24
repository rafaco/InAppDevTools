package es.rafaco.inappdevtools.library.view.components.flex;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//#ifdef MODERN
import androidx.core.content.ContextCompat;
//#else
//@import android.support.v4.content.ContextCompat;
//#endif

import com.alorma.timeline.TimelineView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.TraceGrouper;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class TraceGroupViewHolder extends FlexibleViewHolder {

    private final LinearLayout details;
    TimelineView timeline;
    TextView tag;
    TextView afterTag;
    TextView iconLabel;
    ImageView icon;

    public TraceGroupViewHolder(View view, FlexibleAdapter adapter) {
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
                    new TraceGrouper().propagateExpandedState(data, adapter.getItems());
                    adapter.notifyItemRangeChanged(data.getIndex(), data.getCount()+1);
                }
            });

            if (!data.isExpanded()){
                timeline.setIndicatorSize(UiUtils.getPixelsFromDp(itemView.getContext(), 5));
            }else{
                timeline.setIndicatorSize(0);
            }

            timeline.setIndicatorColor(ContextCompat.getColor(itemView.getContext(), data.getColor()));
            timeline.setTimelineAlignment(TimelineView.ALIGNMENT_MIDDLE);

            if (!data.isExpanded() && data.isLastOnCollapsed()){
                timeline.setTimelineType(TimelineView.TYPE_END);
            }else{
                timeline.setTimelineType(TimelineView.TYPE_MIDDLE);
            }

            tag.setText(data.getTag());
            tag.setTextColor(ContextCompat.getColor(itemView.getContext(), data.getColor()));
            UiUtils.setStrokeToDrawable(tag.getContext(), 1, data.getColor(), tag.getBackground());

            tag.setVisibility(!data.isExpanded() ? View.VISIBLE : View.GONE);
            afterTag.setVisibility(View.GONE);
            iconLabel.setVisibility(View.VISIBLE);
            if (!data.isExpanded()){
                iconLabel.setText(data.getCount() + " more ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    icon.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_arrow_down_rally_24dp));
                }
            }
            else {
                iconLabel.setText("Hide " + data.getCount() + " ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    icon.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_arrow_up_rally_24dp));
                }
            }
        }
    }
}
