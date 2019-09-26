package es.rafaco.inappdevtools.library.view.components.flex;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.info.data.InfoGroupData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.cardview.widget.CardView;
//@import androidx.appcompat.widget.AppCompatButton;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.AppCompatButton;
//#endif

public class InfoGroupViewHolder extends FlexibleViewHolder {

    private final CardView cardView;
    private final TextView iconView;
    private final TextView titleView;
    private final TextView overviewView;
    private final TextView contentView;
    private final ImageView navIcon;
    private final LinearLayout collapsedContentView;
    private final AppCompatButton button;
    private final View buttonSeparator;
    private boolean isExpanded = false;

    public InfoGroupViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.cardView = view.findViewById(R.id.card_view);
        this.iconView = view.findViewById(R.id.icon);
        this.titleView = view.findViewById(R.id.title);
        this.overviewView = view.findViewById(R.id.overview);
        this.collapsedContentView = view.findViewById(R.id.collapsedContent);
        this.contentView = view.findViewById(R.id.content);
        this.navIcon = view.findViewById(R.id.nav_icon);
        this.button = view.findViewById(R.id.button);
        this.buttonSeparator = view.findViewById(R.id.button_separator);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final InfoGroupData data = (InfoGroupData) abstractData;
        if (data!=null){

            itemView.setActivated(true);

            titleView.setText(data.getTitle());
            overviewView.setText(data.getOverview());

            String content = data.entriesToString();
            contentView.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);
            contentView.setText(content);

            int icon = data.getIcon();
            if (icon>0){
                IconUtils.markAsIconContainer(iconView, IconUtils.MATERIAL);
                iconView.setText(icon);
                iconView.setVisibility(View.VISIBLE);
                //iconView.setBackgroundColor(Color.TRANSPARENT);
            }else{
                iconView.setVisibility(View.GONE);
            }

            boolean isExpandable = true;
            if (isExpandable){

                navIcon.setBackground(null);

                if (data.getExpanded() != null){
                    isExpanded = data.getExpanded();
                }
                applyExpandedState(isExpanded);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleExpandedState();
                    }
                });
                
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.iadt_surface_top));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cardView.setElevation(UiUtils.getPixelsFromDp(itemView.getContext(), 3));
                }
                itemView.setClickable(true);
                navIcon.setVisibility(View.VISIBLE);

                if (data.getButtons() == null || data.getButtons().isEmpty()){
                    buttonSeparator.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                }
                else{
                    //TODO: add more buttons dynamically
                    final RunButton buttonData = data.getButtons().get(0);
                    button.setText(buttonData.getTitle());
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonData.getPerformer().run();
                        }
                    });
                    if (buttonData.getIcon()>0){
                        Drawable buttonIcon = button.getContext().getResources().getDrawable(buttonData.getIcon());
                        button.setCompoundDrawablesWithIntrinsicBounds( buttonIcon, null, null, null);
                    }
                    buttonSeparator.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                }
            }
            else{
                //TODO: never used code
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.iadt_surface_bottom));
                cardView.setClickable(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cardView.setElevation(0);
                }
                navIcon.setVisibility(View.GONE);
                cardView.setOnClickListener(null);
                itemView.setClickable(false);
            }
        }
    }

    private void toggleExpandedState() {
        isExpanded = !isExpanded;
        applyExpandedState(isExpanded);
    }

    private void applyExpandedState(boolean isExpanded) {
        if (!isExpanded){
            IconUtils.applyToImageView(navIcon, R.drawable.ic_arrow_down_white_24dp, R.color.rally_white);
            collapsedContentView.setVisibility(View.GONE);
        }
        else {
            IconUtils.applyToImageView(navIcon, R.drawable.ic_arrow_up_white_24dp, R.color.rally_white);
            collapsedContentView.setVisibility(View.VISIBLE);
        }
    }
}
