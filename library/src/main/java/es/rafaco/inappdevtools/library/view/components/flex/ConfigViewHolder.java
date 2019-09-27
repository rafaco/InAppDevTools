package es.rafaco.inappdevtools.library.view.components.flex;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;

//#ifdef ANDROIDX
//@import androidx.appcompat.widget.AppCompatTextView;
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v7.widget.AppCompatTextView;
import android.support.v4.content.ContextCompat;
//#endif

public class ConfigViewHolder extends FlexibleViewHolder {

    AppCompatTextView title;
    AppCompatTextView subtitle;
    Switch switchButton;
    ImageView editButton;
    AppCompatTextView textValue;

    public ConfigViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        title = view.findViewById(R.id.title);
        subtitle = view.findViewById(R.id.subtitle);
        switchButton = view.findViewById(R.id.switch_button);
        editButton = view.findViewById(R.id.edit_button);
        textValue = view.findViewById(R.id.text_value);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final ConfigItem configItem = (ConfigItem) abstractData;
        Context context = title.getContext();
        title.setText(configItem.getConfig().getKey());
        subtitle.setText(context.getText(configItem.getConfig().getDesc()));

        if (configItem.getConfig().getValueType() == boolean.class){
            switchButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
            textValue.setVisibility(View.GONE);

            Boolean value = (Boolean) configItem.getInitialValue();
            switchButton.setChecked(value);
            switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    configItem.setNewValue(isChecked);
                }
            });
        }
        else{
            switchButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
            textValue.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                editButton.setImageDrawable(editButton.getContext().getDrawable(R.drawable.pd_edit));
            } else {
                editButton.setImageDrawable(editButton.getContext().getResources().getDrawable(R.drawable.pd_edit));
            }
            int contextualizedColor = ContextCompat.getColor(editButton.getContext(), R.color.rally_white);
            editButton.setColorFilter(contextualizedColor);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Iadt.showMessage("TODO: Not already implemented");
                }
            });
            textValue.setText((String) configItem.getInitialValue());
        }
    }
}
