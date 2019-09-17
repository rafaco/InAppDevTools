package es.rafaco.inappdevtools.library.view.components.flex;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;

//#ifdef ANDROIDX
//@import androidx.appcompat.widget.AppCompatButton;
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
//#endif

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;

public class RunButtonViewHolder extends FlexibleViewHolder {

    AppCompatButton button;

    public RunButtonViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        button = view.findViewById(R.id.button);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final RunButton data = (RunButton) abstractData;
        int color = (data.getColor() >  0) ? data.getColor() : R.color.rally_bg_blur;
        int contextualizedColor = ContextCompat.getColor(button.getContext(), color);
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
