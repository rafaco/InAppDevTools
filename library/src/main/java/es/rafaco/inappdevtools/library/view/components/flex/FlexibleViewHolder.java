package es.rafaco.inappdevtools.library.view.components.flex;

import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

public abstract class FlexibleViewHolder extends RecyclerView.ViewHolder {

    FlexibleAdapter adapter;

    public FlexibleViewHolder(@NonNull View itemView,@NonNull FlexibleAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
    }
    public void onCreate(ViewGroup viewGroup, int viewType){}
    public abstract void bindTo(Object abstractData, int position);
}
