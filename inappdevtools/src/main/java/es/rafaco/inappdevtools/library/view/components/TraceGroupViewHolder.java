package es.rafaco.inappdevtools.library.view.components;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.alorma.timeline.TimelineView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import es.rafaco.inappdevtools.library.R;

class TraceGroupViewHolder extends RecyclerView.ViewHolder {
    TimelineView timeline;
    TextView title;
    TextView subtitle;

    public TraceGroupViewHolder(View view) {
        super(view);
        this.timeline = view.findViewById(R.id.timeline);
        this.title = view.findViewById(R.id.title);
        this.subtitle = view.findViewById(R.id.subtitle);
        //this.count = view.findViewById(R.id.count);
    }

    public void bindTo(TraceGroupItem data) {
        if (data!=null){

            if (data.isExpanded()){
                title.setText("Tap to collapse");
                subtitle.setText("");
            }
            else {
                title.setText(data.getTitle());
                subtitle.setText(data.getSubtitle());
            }

            title.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_blue));
            subtitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_blue));
            timeline.setIndicatorColor(Color.GRAY);
            timeline.setTimelineAlignment(TimelineView.ALIGNMENT_START);
            timeline.setTimelineType(TimelineView.TYPE_MIDDLE);

            itemView.setOnClickListener(v -> data.getRunnable().run());
        }
    }
}
