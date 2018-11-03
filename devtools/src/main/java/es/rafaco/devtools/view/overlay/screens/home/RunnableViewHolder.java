package es.rafaco.devtools.view.overlay.screens.home;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;

public class RunnableViewHolder extends RecyclerView.ViewHolder {

    AppCompatButton button;

    public RunnableViewHolder(View view) {
        super(view);
        button = view.findViewById(R.id.button);
    }

    public void bindTo(RunnableConfig data) {
        int contextualizedColor = ContextCompat.getColor(button.getContext(), R.color.rally_bg_blur);
        button.getBackground().setColorFilter(contextualizedColor, PorterDuff.Mode.MULTIPLY);
        if (data.getIcon()>0){
            Drawable icon = button.getContext().getResources().getDrawable(data.getIcon());
            button.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null);
        }
        button.setText(data.title);
        button.setOnClickListener(v -> DevTools.run(data.key));
    }
}
