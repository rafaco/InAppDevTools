package es.rafaco.inappdevtools.library.view.components;

import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.integrations.ThinItem;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;

public class LinkViewHolder extends RecyclerView.ViewHolder {

    TextView icon;
    TextView title;

    public LinkViewHolder(View view) {
        super(view);
        icon = view.findViewById(R.id.icon);
        title = view.findViewById(R.id.title);
    }

    public void bindTo(ThinItem data) {

        if (data.getIcon()>0){
            IconUtils.set(icon, data.getIcon());
            int contextualizedColor = ContextCompat.getColor(icon.getContext(), data.getColor());
            icon.setTextColor(contextualizedColor);
        }
        title.setText(data.getTitle());

        itemView.setClickable(true);
        itemView.setOnClickListener(v -> data.onClick());
    }
}
