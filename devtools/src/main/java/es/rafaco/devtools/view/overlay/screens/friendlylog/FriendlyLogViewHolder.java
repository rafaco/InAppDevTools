package es.rafaco.devtools.view.overlay.screens.friendlylog;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.utils.DateUtils;
import es.rafaco.devtools.logic.steps.FriendlyLog;
import es.rafaco.devtools.storage.db.entities.Friendly;
import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.errors.AnrDetailScreen;
import es.rafaco.devtools.view.overlay.screens.errors.CrashDetailScreen;
import es.rafaco.devtools.view.overlay.screens.network.detail.NetworkDetailScreen;

public class FriendlyLogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final FriendlyLogAdapter.OnClickListener clickListener;

    long uid;
    ImageView icon;
    View decorator;
    TextView title;

    LinearLayout wrapper;
    LinearLayout extra_over;
    TextView extras_over;
    LinearLayout extra_details;
    TextView extras_details;
    AppCompatButton extra_button;

    public FriendlyLogViewHolder(View view, final FriendlyLogAdapter.OnClickListener clickListener) {
        super(view);

        this.clickListener = clickListener;
        itemView.setOnClickListener(this);
        //itemView.setOnLongClickListener(this);

        wrapper = view.findViewById(R.id.wrapper);
        decorator = view.findViewById(R.id.decorator);
        title = view.findViewById(R.id.title);
        icon = view.findViewById(R.id.icon);
        extra_over = view.findViewById(R.id.extra_over);
        extras_over = view.findViewById(R.id.extras_over);
        extra_details = view.findViewById(R.id.extra_details);
        extras_details = view.findViewById(R.id.extras_details);
        extra_button = view.findViewById(R.id.extra_button);
    }

    public void bindTo(Friendly data, boolean isSelected) {
        uid = data.getUid();

        int bgColorId = isSelected ? R.color.rally_bg_blur : R.color.rally_bg_solid;
        int contextualizedColor = ContextCompat.getColor(wrapper.getContext(), bgColorId);
        wrapper.setBackgroundColor(contextualizedColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemView.setElevation(isSelected ? 5 : 0);
        }

        title.setVisibility(View.VISIBLE);
        title.setText(data.getMessage());
        title.setSingleLine(!isSelected);
        title.setBackgroundColor(Color.TRANSPARENT);

        contextualizedColor = ContextCompat.getColor(itemView.getContext(), FriendlyLog.getColor(data));
        decorator.setBackgroundColor(contextualizedColor);

        int icon = FriendlyLog.getIcon(data);
        if (icon != -1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.icon.setImageDrawable(itemView.getContext().getDrawable(icon));
                this.icon.setColorFilter(contextualizedColor);
            } else {
                this.icon.setImageDrawable(itemView.getContext().getResources().getDrawable(icon));
            }
            this.icon.setVisibility(View.VISIBLE);
            this.icon.setBackgroundColor(Color.TRANSPARENT);
        }else{
            this.icon.setVisibility(View.GONE);
        }

        extra_details.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        extras_details.setText(!isSelected ? "" : data.getExtra());

        extra_over.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        extras_over.setText(!isSelected ? "" :
                String.format("%s [%s] %s-%s", DateUtils.format(data.getDate()), data.getSeverity(), data.getCategory(), data.getType()));

        if(isSelected && getLink(data)!=null){
            extra_button.setOnClickListener(v -> OverlayUIService.performNavigationStep(getLink(data)));
            extra_button.getBackground().setColorFilter(contextualizedColor, PorterDuff.Mode.MULTIPLY);
            extra_button.setVisibility(View.VISIBLE);
        }else{
            extra_button.setOnClickListener(null);
            extra_button.setVisibility(View.GONE);
        }
    }

    private NavigationStep getLink(Friendly data) {
        if(data.getType().equals("Crash")){
            return new NavigationStep(CrashDetailScreen.class, String.valueOf(data.getLinkedId()));
        }
        else if(data.getType().equals("Anr")){
            return new NavigationStep(AnrDetailScreen.class, String.valueOf(data.getLinkedId()));
        }
        else if(data.getCategory().equals("Network")){
            return new NavigationStep(NetworkDetailScreen.class, String.valueOf(data.getLinkedId()));
        }

        return null;
    }

    public void showPlaceholder() {
        title.setVisibility(View.VISIBLE);
        title.setText("");
        title.setBackgroundColor(Color.GRAY);
        decorator.setBackgroundColor(Color.GRAY);

        icon.setVisibility(View.VISIBLE);
        icon.setBackgroundColor(Color.GRAY);

        extra_details.setVisibility(View.GONE);
        extra_over.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        clickListener.onClick(v, getAdapterPosition(), uid);
    }
}
