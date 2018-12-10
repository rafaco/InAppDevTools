package es.rafaco.inappdevtools.library.logic.sources;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.jetbrains.annotations.NotNull;

import io.github.kbiakov.codeview.adapters.AbstractCodeAdapter;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;
import io.github.kbiakov.codeview.highlight.Font;

public class SourceAdapter extends AbstractCodeAdapter<String> {

    public SourceAdapter(@NotNull Context context, @NotNull String code, String language) {
        super(context, Options.Default.get(context)
                .withCode(code)
                .withShadows()
                .withLanguage(language)
                //.withFormat(Format.Default.getExtraCompact())
                .withFont(Font.Inconsolata)
                .withTheme(ColorTheme.MONOKAI));
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
