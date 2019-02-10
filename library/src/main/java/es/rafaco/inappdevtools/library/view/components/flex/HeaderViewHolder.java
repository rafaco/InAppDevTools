package es.rafaco.inappdevtools.library.view.components.flex;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;

public class HeaderViewHolder extends FlexibleViewHolder {

    TextView titleView;

    public HeaderViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.titleView = view.findViewById(R.id.title);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        String data = (String) abstractData;
        titleView.setVisibility(TextUtils.isEmpty(data) ? View.GONE : View.VISIBLE);
        if (data != null) titleView.setText(data);
    }
}
