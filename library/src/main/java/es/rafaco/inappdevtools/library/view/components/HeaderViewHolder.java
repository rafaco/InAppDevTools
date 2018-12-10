package es.rafaco.inappdevtools.library.view.components;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;

class HeaderViewHolder extends RecyclerView.ViewHolder {

    TextView titleView;

    public HeaderViewHolder(View view) {
        super(view);
        this.titleView = view.findViewById(R.id.title);
    }

    public void bindTo(String data) {
        titleView.setVisibility(TextUtils.isEmpty(data) ? View.GONE : View.VISIBLE);
        if (data != null) titleView.setText(data);
    }
}
