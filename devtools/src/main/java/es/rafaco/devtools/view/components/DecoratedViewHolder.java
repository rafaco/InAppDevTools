package es.rafaco.devtools.view.components;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.OverlayUIService;

public class DecoratedViewHolder extends RecyclerView.ViewHolder {

    Context context;
    boolean switchMode;
    ImageView headIcon;
    View decorator;
    TextView title, message;
    ImageView icon;
    Switch switchButton;

    public DecoratedViewHolder(ViewGroup parent, boolean switchMode) {
        super(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.decorated_tool_info_item, parent, false));

        context = itemView.getContext();
        this.switchMode = switchMode;
        decorator = itemView.findViewById(R.id.decorator);
        title = itemView.findViewById(R.id.title);
        message = itemView.findViewById(R.id.message);
        headIcon = itemView.findViewById(R.id.head_icon);
        icon = itemView.findViewById(R.id.icon);
        switchButton = itemView.findViewById(R.id.switch_button);
    }


    public void bindTo(DecoratedToolInfo data) {

        if (TextUtils.isEmpty(data.title))
            title.setVisibility(View.GONE);
        else{
            title.setVisibility(View.VISIBLE);
            title.setText(data.title);
        }

        if (TextUtils.isEmpty(data.message))
            title.setVisibility(View.GONE);
        else{
            message.setVisibility(View.VISIBLE);
            message.setText(data.message);
        }

        int contextualizedColor = ContextCompat.getColor(itemView.getContext(),data.color);
        title.setTextColor(contextualizedColor);
        decorator.setBackgroundColor(contextualizedColor);

        if (data.icon != -1){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                headIcon.setImageDrawable(context.getApplicationContext().getDrawable(data.icon));
                headIcon.setColorFilter(contextualizedColor);
            } else {
                headIcon.setImageDrawable(context.getResources().getDrawable(data.icon));
            }
            headIcon.setVisibility(View.VISIBLE);
        }else{
            headIcon.setVisibility(View.GONE);
        }

        if(!switchMode){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(data);
                }
            });
        }else{
            icon.setVisibility(View.GONE);
            switchButton.setVisibility(View.VISIBLE);
        }

        //TODO: temp
        if (data.icon != -1){
            icon.setVisibility(View.GONE);
        }
    }

    protected void onItemClick(DecoratedToolInfo data) {
        if (data.getNavigationStep() != null)
            OverlayUIService.performNavigationStep(data.getNavigationStep());
        else
            data.getRunnable().run();
    }
}
