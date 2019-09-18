package es.rafaco.inappdevtools.library.view.components.flex;

import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.cardview.widget.CardView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
//#endif

public class CardViewHolder extends FlexibleViewHolder {

    private final LinearLayout itemContent;
    private final CardView cardView;
    private final ImageView iconView;
    private final TextView titleView;
    private final TextView contentView;
    private final ImageView navIcon;

    public CardViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.itemContent = view.findViewById(R.id.item_content);
        this.cardView = view.findViewById(R.id.card_view);
        this.iconView = view.findViewById(R.id.icon);
        this.titleView = view.findViewById(R.id.title);
        this.contentView = view.findViewById(R.id.content);
        this.navIcon = view.findViewById(R.id.nav_icon);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final CardData data = (CardData) abstractData;
        if (data!=null){

            itemView.setActivated(true);

            titleView.setText(data.getTitle());

            contentView.setVisibility(TextUtils.isEmpty(data.getContent()) ? View.GONE : View.VISIBLE);
            contentView.setText(data.getContent());

            if (data.getIcon()>0){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    iconView.setImageDrawable(itemView.getContext().getDrawable(data.getIcon()));
                } else {
                    iconView.setImageDrawable(itemView.getContext().getResources().getDrawable(data.getIcon()));
                }
                int contextualizedColor = ContextCompat.getColor(iconView.getContext(), R.color.rally_white);
                iconView.setColorFilter(contextualizedColor);
                iconView.setVisibility(View.VISIBLE);
                iconView.setBackgroundColor(Color.TRANSPARENT);
            }else{
                iconView.setVisibility(View.GONE);
            }

            if (data.getPerformer() != null){
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.iadt_surface_top));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cardView.setElevation(UiUtils.getPixelsFromDp(itemView.getContext(), 3));
                }
                navIcon.setVisibility(View.VISIBLE);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.getPerformer().run();
                    }
                });
                itemView.setClickable(true);
            }
            else{
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
}
