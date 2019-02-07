package es.rafaco.inappdevtools.library.view.components.flex;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class FlexibleViewHolder extends RecyclerView.ViewHolder {

    public FlexibleViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bindTo(Object abstractData);
}
