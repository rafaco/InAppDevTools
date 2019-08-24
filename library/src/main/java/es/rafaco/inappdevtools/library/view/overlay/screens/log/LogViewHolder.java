package es.rafaco.inappdevtools.library.view.overlay.screens.log;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.appcompat.widget.AppCompatButton;
//@import androidx.appcompat.widget.AppCompatTextView;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
//#endif

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.layers.NavigationStep;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.AnrDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.detail.NetworkDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class LogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final LogAdapter.OnClickListener clickListener;

    long uid;
    ImageView icon;
    View decorator;
    AppCompatTextView title;

    LinearLayout wrapper;
    LinearLayout extra_over;
    AppCompatTextView extras_over;
    LinearLayout extra_details;
    AppCompatTextView extras_details;
    AppCompatButton extra_button;

    public LogViewHolder(View view, final LogAdapter.OnClickListener clickListener) {
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

    public void bindTo(final Friendly data, boolean isSelected) {
        uid = data.getUid();
        boolean isLogcat = data.getCategory().equals("Logcat");

        int bgColorId = isSelected ? R.color.rally_bg_blur : R.color.rally_bg_solid;
        int bgColor = ContextCompat.getColor(wrapper.getContext(), bgColorId);
        wrapper.setBackgroundColor(bgColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemView.setElevation(isSelected ? 5 : 0);
        }

        int severityColor = ContextCompat.getColor(itemView.getContext(), FriendlyLog.getColor(data));

        decorator.setBackgroundColor(severityColor);

        if (isLogcat){
            title.setTypeface(Typeface.create(Typeface.MONOSPACE, R.style.TextMonospaceSmall));
        }else{
            title.setTypeface(Typeface.create(Typeface.SANS_SERIF, R.style.TextCondensedSmall));
            title.setTypeface(title.getTypeface(), Typeface.BOLD);
        }
        title.setTextColor(severityColor);
        title.setVisibility(View.VISIBLE);
        title.setText(data.getMessage());
        title.setSingleLine(!isSelected);
        title.setEllipsize(!isSelected ? TextUtils.TruncateAt.END : null);
        title.setBackgroundColor(Color.TRANSPARENT);

        int icon = FriendlyLog.getIcon(data);
        if (icon != -1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.icon.setImageDrawable(itemView.getContext().getDrawable(icon));
            } else {
                this.icon.setImageDrawable(itemView.getContext().getResources().getDrawable(icon));
            }
            this.icon.setColorFilter(severityColor);
            this.icon.setVisibility(View.VISIBLE);
            this.icon.setBackgroundColor(Color.TRANSPARENT);
        }else{
            this.icon.setVisibility(View.GONE);
        }

        extra_details.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        extras_details.setText(!isSelected ? "" : data.getExtra());

        extra_over.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        extras_over.setText(!isSelected ? "" :
                String.format("%s [%s-%s]",
                        DateUtils.format(data.getDate()),
                        data.getCategory(), data.getSubcategory()));

        if(isSelected && getLink(data)!=null){
            extra_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OverlayUIService.performNavigationStep(LogViewHolder.this.getLink(data));
                }
            });
            extra_button.getBackground().setColorFilter(severityColor, PorterDuff.Mode.MULTIPLY);
            extra_button.setVisibility(View.VISIBLE);
        }else{
            extra_button.setOnClickListener(null);
            extra_button.setVisibility(View.GONE);
        }
    }

    private NavigationStep getLink(Friendly data) {
        if(data.getSubcategory().equals("Crash")){
            return new NavigationStep(CrashDetailScreen.class, String.valueOf(data.getLinkedId()));
        }
        else if(data.getSubcategory().equals("Anr")){
            return new NavigationStep(AnrDetailScreen.class, String.valueOf(data.getLinkedId()));
        }
        else if(data.getCategory().equals("Network")){
            return new NavigationStep(NetworkDetailScreen.class, String.valueOf(data.getLinkedId()));
        }

        return null;
    }

    public void showPlaceholder(int position) {
        int color = ContextCompat.getColor(title.getContext(), R.color.rally_gray);

        title.setVisibility(View.VISIBLE);
        title.setText("");
        title.setBackgroundColor(color);
        decorator.setBackgroundColor(color);

        icon.setVisibility(View.VISIBLE);
        icon.setBackgroundColor(color);

        extra_details.setVisibility(View.GONE);
        extra_over.setVisibility(View.GONE);
        extra_button.setVisibility(View.GONE);

        if (Humanizer.isEven(position)){
            title.setMaxWidth(title.getWidth()/2);
        }
    }

    @Override
    public void onClick(View v) {
        clickListener.onClick(v, getAdapterPosition(), uid);
    }
}
