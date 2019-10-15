package es.rafaco.inappdevtools.library.view.components.flex;

import android.view.View;
import android.widget.LinearLayout;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.appcompat.widget.AppCompatTextView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
//#endif

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;

public class LinkViewHolder extends FlexibleViewHolder {

    private final LinearLayout wrapper;
    AppCompatTextView icon;
    AppCompatTextView title;

    public LinkViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        wrapper = view.findViewById(R.id.link_wrapper);
        icon = view.findViewById(R.id.icon);
        title = view.findViewById(R.id.title);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final LinkItem data = (LinkItem) abstractData;
        if (data.getIcon()>0){
            IconUtils.set(icon, data.getIcon());
            int contextualizedColor = ContextCompat.getColor(icon.getContext(), data.getColor());
            icon.setTextColor(contextualizedColor);
        }
        title.setText(data.getTitle());

        wrapper.setClickable(true);
        wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.onClick();
            }
        });
    }
}
