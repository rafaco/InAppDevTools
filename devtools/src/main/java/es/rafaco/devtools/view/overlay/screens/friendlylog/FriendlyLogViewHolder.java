package es.rafaco.devtools.view.overlay.screens.friendlylog;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.utils.FriendlyLog;
import es.rafaco.devtools.storage.db.entities.Friendly;

public class FriendlyLogViewHolder extends RecyclerView.ViewHolder {
    ImageView headIcon;
    View decorator;
    TextView title, message;
    ImageView icon;
    Switch switchButton;

    public FriendlyLogViewHolder(View view) {
        super(view);
        decorator = view.findViewById(R.id.decorator);
        title = view.findViewById(R.id.title);
        message = view.findViewById(R.id.message);
        headIcon = view.findViewById(R.id.head_icon);
        icon = view.findViewById(R.id.icon);
        switchButton = view.findViewById(R.id.switch_button);
    }

    public void bindTo(Friendly data) {
        int icon = FriendlyLog.getIcon(data);
        int color = FriendlyLog.getColor(data);

        title.setVisibility(View.GONE);

        message.setVisibility(View.VISIBLE);
        message.setText(data.getMessage());

        //data.getUid() + ":" +
        int contextualizedColor = ContextCompat.getColor(itemView.getContext(), color);
        title.setTextColor(contextualizedColor);
        decorator.setBackgroundColor(contextualizedColor);


        if (icon != -1){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                headIcon.setImageDrawable(itemView.getContext().getDrawable(icon));
                headIcon.setColorFilter(contextualizedColor);
            } else {
                headIcon.setImageDrawable(itemView.getContext().getResources().getDrawable(icon));
            }
            headIcon.setVisibility(View.VISIBLE);
        }else{
            headIcon.setVisibility(View.GONE);
        }
    }

    public void clear() {

    }
}
