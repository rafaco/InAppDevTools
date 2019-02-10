package es.rafaco.inappdevtools.library.view.components.flex;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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
    private final ImageView navIcon;
    TextView exceptionView;
    private final TextView messageView;
    private final TextView whereView;
    private final TextView where2View;
    private final TextView where3View;
    private final TextView tag;

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
        this.navIcon = view.findViewById(R.id.icon);
        this.tag = view.findViewById(R.id.tag);
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


            timeline.setIndicatorColor(ContextCompat.getColor(itemView.getContext(), data.getColor()));
            timeline.setIndicatorSize(UiUtils.getPixelsFromDp(itemView.getContext(), 5));

            timeline.setTimelineAlignment(TimelineView.ALIGNMENT_MIDDLE);
            if (data.getPosition().equals(TraceItem.Position.START)){
                whereView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_orange));
                timeline.setIndicatorColor(ContextCompat.getColor(itemView.getContext(), data.getColor()));
                timeline.setTimelineType(TimelineView.TYPE_START);
            }
            else if (data.getPosition().equals(TraceItem.Position.END)){
                whereView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_yellow));
                timeline.setTimelineType(TimelineView.TYPE_END);
            }else{
                whereView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_yellow));
                timeline.setTimelineType(TimelineView.TYPE_MIDDLE);
            }
            exceptionView.setVisibility(TextUtils.isEmpty(data.getException()) ? View.GONE : View.VISIBLE);
            exceptionView.setText(data.getException());
            messageView.setVisibility(TextUtils.isEmpty(data.getMessage()) ? View.GONE : View.VISIBLE);
            messageView.setText(data.getMessage());

            Sourcetrace traces = data.getSourcetrace();
            whereView.setText(traces.getShortClassName() + "." + traces.getMethodName() + "()");
            where2View.setText(traces.getPackageName());
            where2View.setTextColor(ContextCompat.getColor(itemView.getContext(), data.getColor()));

            tag.setText(data.getTag());
            tag.setTextColor(ContextCompat.getColor(itemView.getContext(), data.getColor()));
            UiUtils.setStrokeToDrawable(tag.getContext(), 1, data.getColor(), tag.getBackground());

            where3View.setText(traces.getFileName() + ":" + traces.getLineNumber());

            if (data.isOpenable()){
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_bg_new));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cardView.setElevation(UiUtils.getPixelsFromDp(itemView.getContext(), 3));
                }
                where3View.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_white));
                UiUtils.setCardViewClickable(itemView.getContext(), cardView, true);
                cardView.setOnClickListener(v -> OverlayUIService.performNavigation(SourceDetailScreen.class,
                        SourceDetailScreen.buildParams(SourcesManager.DEVTOOLS_SRC,
                                extractPath(traces),
                                traces.getLineNumber())));
                itemView.setClickable(false);
                navIcon.setVisibility(View.VISIBLE);
            }else{
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_bg_new_alpha));
                cardView.setClickable(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cardView.setElevation(0);
                }
                where3View.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_gray));
                UiUtils.setCardViewClickable(itemView.getContext(), cardView, false);
                cardView.setOnClickListener(null);
                itemView.setClickable(false);
                navIcon.setVisibility(View.GONE);
            }
        }
    }

    public String extractPath(Sourcetrace stacktrace){
        String name = stacktrace.getFileName().substring(0, stacktrace.getFileName().indexOf("."));
        int pathEnd = stacktrace.getClassName().indexOf(name) + name.length();
        String path = stacktrace.getClassName().substring(0, pathEnd);
        path = path.replace(".", "/");
        return path + ".java";
    }
}
