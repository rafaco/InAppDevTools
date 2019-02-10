package es.rafaco.inappdevtools.library.view.components.flex;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.timeline.TimelineView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.sources.SourcesManager;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class TraceViewHolder extends FlexibleViewHolder {

    private final LinearLayout itemContent;
    private final CardView cardView;
    private final TimelineView timeline;
    private final AppCompatButton button;
    TextView exceptionView;
    private final TextView messageView;
    private final TextView whereView;
    private final TextView where2View;
    private final TextView where3View;

    public TraceViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.itemContent = view.findViewById(R.id.item_content);
        this.cardView = view.findViewById(R.id.card_view);
        this.timeline = view.findViewById(R.id.timeline);
        this.exceptionView = view.findViewById(R.id.exception);
        this.messageView = view.findViewById(R.id.message);
        this.whereView = view.findViewById(R.id.where);
        this.where2View = view.findViewById(R.id.where2);
        this.where3View = view.findViewById(R.id.where3);
        this.button = view.findViewById(R.id.button);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        TraceItem data = (TraceItem) abstractData;
        if (data!=null){

            itemView.setActivated(data.isExpanded());
            itemContent.setVisibility(data.isExpanded()? View.VISIBLE : View.GONE);
            cardView.setVisibility(data.isExpanded()? View.VISIBLE : View.GONE);
            timeline.setVisibility(data.isExpanded()? View.VISIBLE : View.GONE);
            if (!data.isExpanded())
                return;

            int indicatorColor = data.isGrouped() ? R.color.rally_gray : R.color.rally_yellow;
            timeline.setIndicatorColor(ContextCompat.getColor(itemView.getContext(), indicatorColor));
            timeline.setIndicatorSize(UiUtils.getPixelsFromDp(itemView.getContext(), 5));

            int cardColor = data.isGrouped() ? R.color.rally_gray : R.color.rally_bg_new;
            cardView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), cardColor));


            timeline.setTimelineAlignment(TimelineView.ALIGNMENT_MIDDLE);
            if (data.getPosition().equals(TraceItem.Position.START)){
                whereView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_orange));
                //timeline.setIndicatorColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_orange));
                timeline.setTimelineType(TimelineView.TYPE_START);
            }
            else if (data.getPosition().equals(TraceItem.Position.END)){
                timeline.setTimelineType(TimelineView.TYPE_END);
            }else{
                timeline.setTimelineType(TimelineView.TYPE_MIDDLE);
            }
            exceptionView.setVisibility(TextUtils.isEmpty(data.getException()) ? View.GONE : View.VISIBLE);
            exceptionView.setText(data.getException());
            messageView.setVisibility(TextUtils.isEmpty(data.getMessage()) ? View.GONE : View.VISIBLE);
            messageView.setText(data.getMessage());

            Sourcetrace traces = data.getSourcetrace();
            whereView.setText(traces.getShortClassName() + "." + traces.getMethodName() + "()");
            where2View.setText(traces.getPackageName());
            where3View.setText(traces.getFileName() + ":" + traces.getLineNumber());

            if (canOpen(traces)){
                itemView.setOnClickListener(v -> OverlayUIService.performNavigation(SourceDetailScreen.class,
                        SourceDetailScreen.buildParams(SourcesManager.DEVTOOLS_SRC,
                                extractPath(traces),
                                traces.getLineNumber())));
                itemView.setClickable(true);
                where3View.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_white));
            }else{
                where3View.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_gray));
            }
        }
    }

    public Boolean canOpen(Sourcetrace stacktrace){
        return stacktrace.getLineNumber()>0
                && stacktrace.getClassName().startsWith("es.rafaco.inappdevtools");
    }

    public String extractPath(Sourcetrace stacktrace){
        String name = stacktrace.getFileName().substring(0, stacktrace.getFileName().indexOf("."));
        int pathEnd = stacktrace.getClassName().indexOf(name) + name.length();
        String path = stacktrace.getClassName().substring(0, pathEnd);
        path = path.replace(".", "/");
        return path + ".java";
    }
}
