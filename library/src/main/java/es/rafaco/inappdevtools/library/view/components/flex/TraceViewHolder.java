package es.rafaco.inappdevtools.library.view.components.flex;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alorma.timeline.TimelineView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.sources.SourcesManager;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;

public class TraceViewHolder extends FlexibleViewHolder {

    private final TimelineView timeline;
    private final AppCompatButton button;
    TextView exceptionView;
    private final TextView messageView;
    private final TextView whereView;
    private final TextView where2View;
    private final TextView where3View;

    public TraceViewHolder(View view) {
        super(view);
        this.timeline = view.findViewById(R.id.timeline);
        this.exceptionView = view.findViewById(R.id.exception);
        this.messageView = view.findViewById(R.id.message);
        this.whereView = view.findViewById(R.id.where);
        this.where2View = view.findViewById(R.id.where2);
        this.where3View = view.findViewById(R.id.where3);
        this.button = view.findViewById(R.id.button);
    }
    @Override
    public void bindTo(Object abstractData) {
        TraceItem data = (TraceItem) abstractData;
        if (data!=null){

            timeline.setIndicatorSize(8); //"8dp"?
            timeline.setIndicatorColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_yellow));

            if (data.getPosition().equals(TraceItem.Position.START)){
                whereView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_orange));
                timeline.setIndicatorColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_orange));
                timeline.setTimelineAlignment(TimelineView.ALIGNMENT_START);
                timeline.setTimelineType(TimelineView.TYPE_MIDDLE);
            }
            else if (data.getPosition().equals(TraceItem.Position.END)){
                timeline.setTimelineType(TimelineView.TYPE_END);
                timeline.setTimelineAlignment(TimelineView.ALIGNMENT_START);
            }else{
                timeline.setTimelineAlignment(TimelineView.ALIGNMENT_START);
                timeline.setTimelineType(TimelineView.TYPE_MIDDLE);
            }
            exceptionView.setVisibility(TextUtils.isEmpty(data.getException()) ? View.GONE : View.VISIBLE);
            exceptionView.setText(data.getException());
            messageView.setVisibility(TextUtils.isEmpty(data.getMessage()) ? View.GONE : View.VISIBLE);
            messageView.setText(data.getMessage());

            Sourcetrace stacktrace = data.getSourcetrace();
            int limit = stacktrace.getClassName().lastIndexOf(".");
            String packageName = stacktrace.getClassName().substring(0, limit);
            String className = stacktrace.getClassName().substring(limit+1);

            whereView.setText(stacktrace.getMethodName() + ", " + className);
            where2View.setText(packageName);
            where3View.setText(stacktrace.getFileName() + ":" + stacktrace.getLineNumber());

            if (canOpen(stacktrace)){
                itemView.setOnClickListener(v -> OverlayUIService.performNavigation(SourceDetailScreen.class,
                        SourceDetailScreen.buildParams(SourcesManager.DEVTOOLS_SRC,
                                extractPath(stacktrace),
                                stacktrace.getLineNumber())));
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
