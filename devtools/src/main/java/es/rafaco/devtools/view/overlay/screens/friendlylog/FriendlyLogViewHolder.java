package es.rafaco.devtools.view.overlay.screens.friendlylog;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.utils.FriendlyLog;
import es.rafaco.devtools.storage.db.entities.Friendly;

public class FriendlyLogViewHolder extends RecyclerView.ViewHolder {
    ImageView icon;
    View decorator;
    TextView title;

    public FriendlyLogViewHolder(View view) {
        super(view);
        decorator = view.findViewById(R.id.decorator);
        title = view.findViewById(R.id.title);
        icon = view.findViewById(R.id.icon);
    }

    public void bindTo(Friendly data) {
        int icon = FriendlyLog.getIcon(data);
        int color = FriendlyLog.getColor(data);

        title.setVisibility(View.VISIBLE);
        title.setText(data.getMessage());

        //data.getUid() + ":" +
        int contextualizedColor = ContextCompat.getColor(itemView.getContext(), color);
        decorator.setBackgroundColor(contextualizedColor);

        if (icon != -1){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                this.icon.setImageDrawable(itemView.getContext().getDrawable(icon));
                this.icon.setColorFilter(contextualizedColor);
            } else {
                this.icon.setImageDrawable(itemView.getContext().getResources().getDrawable(icon));
            }
            this.icon.setVisibility(View.VISIBLE);
        }else{
            this.icon.setVisibility(View.GONE);
        }
    }

    public void showPlaceholder() {
        title.setVisibility(View.VISIBLE);
        title.setText("");
        title.setBackgroundColor(Color.GRAY);
        decorator.setBackgroundColor(Color.GRAY);

        icon.setVisibility(View.VISIBLE);
        icon.setBackgroundColor(Color.GRAY);
    }
}
