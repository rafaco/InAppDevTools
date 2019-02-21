package es.rafaco.inappdevtools.library.logic.sources;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.jetbrains.annotations.NotNull;

import android.support.v4.content.ContextCompat;
import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import io.github.kbiakov.codeview.adapters.AbstractCodeAdapter;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;
import io.github.kbiakov.codeview.highlight.Font;

public class SourceAdapter extends AbstractCodeAdapter<String> {

    private final SourceDetailScreen.InnerParams params;
    private final OverlayScreen screen;

    public SourceAdapter(@NotNull OverlayScreen screen, @NotNull String code, String language, SourceDetailScreen.InnerParams params) {
        super(screen.getContext(), Options.Default.get(screen.getContext())
                .withCode(code)
                .withShadows()
                .withLanguage(language)
                //.withFormat(Format.Default.getExtraCompact())
                .withFont(Font.Inconsolata)
                .withTheme(ColorTheme.MONOKAI));
        this.params = params;
        this.screen = screen;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        ViewHolder holder = super.onCreateViewHolder(parent, viewType);
        adjustMargins(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int pos) {
        super.onBindViewHolder(holder, pos);

        int contextualizedColor;
        if (params != null && params.lineNumber > 0
                && params.lineNumber == pos){
            contextualizedColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.rally_orange_alpha);
            holder.itemView.setBackgroundColor(contextualizedColor);
            //TODO: scrolling
            screen.getScreenManager().getMainLayer().focusOnView(holder.itemView);
        }
        /*String text = holder.getTvLineContent().getText().toString();
        if (TextUtils.isEmpty(text) || text.contains("//")){
            int contextualizedColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.cv_default_alpha);
            holder.itemView.setBackgroundColor(Color.parseColor("#5E5339"));
            //holder.getTvLineNum().setTextColor(Color.parseColor("#93A1A1"));
            //holder.getTvLineNum().setBackgroundColor(Color.parseColor("#EEE8D5"));
        }*/
    }

    @NotNull
    @Override
    public View createFooter(@NotNull Context context, String entity, boolean isFirst) {
        return null;
    }

    private void adjustMargins(ViewHolder holder) {
        RelativeLayout.LayoutParams parameter = (RelativeLayout.LayoutParams) holder.getTvLineContent().getLayoutParams();
        parameter.setMargins(8, 0, 0, 8);
        holder.getTvLineContent().setLayoutParams(parameter);
    }
}
