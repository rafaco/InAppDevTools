package es.rafaco.inappdevtools.library.view.components.flex;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;

//#ifdef MODERN
//@import androidx.appcompat.widget.AppCompatButton;
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
//#endif

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.integrations.RunnableConfig;

public class RunnableViewHolder extends FlexibleViewHolder {

    AppCompatButton button;

    public RunnableViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        button = view.findViewById(R.id.button);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final RunnableConfig data = (RunnableConfig) abstractData;
        int contextualizedColor = ContextCompat.getColor(button.getContext(), R.color.rally_bg_blur);
        button.getBackground().setColorFilter(contextualizedColor, PorterDuff.Mode.MULTIPLY);
        if (data.getIcon()>0){
            Drawable icon = button.getContext().getResources().getDrawable(data.getIcon());
            button.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null);
        }
        button.setText(data.getTitle());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.run();
            }
        });
    }
}
