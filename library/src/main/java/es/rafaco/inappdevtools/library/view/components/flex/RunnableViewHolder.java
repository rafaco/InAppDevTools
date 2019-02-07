package es.rafaco.inappdevtools.library.view.components.flex;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import android.view.View;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.integrations.RunnableConfig;

public class RunnableViewHolder extends FlexibleViewHolder {

    AppCompatButton button;

    public RunnableViewHolder(View view) {
        super(view);
        button = view.findViewById(R.id.button);
    }

    @Override
    public void bindTo(Object abstractData) {
        RunnableConfig data = (RunnableConfig) abstractData;
        int contextualizedColor = ContextCompat.getColor(button.getContext(), R.color.rally_bg_blur);
        button.getBackground().setColorFilter(contextualizedColor, PorterDuff.Mode.MULTIPLY);
        if (data.getIcon()>0){
            Drawable icon = button.getContext().getResources().getDrawable(data.getIcon());
            button.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null);
        }
        button.setText(data.getTitle());
        button.setOnClickListener(v -> data.run());
    }
}
