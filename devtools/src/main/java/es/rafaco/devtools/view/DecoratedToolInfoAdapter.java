package es.rafaco.devtools.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.tools.ErrorsTool;
import es.rafaco.devtools.utils.ThreadUtils;

public class DecoratedToolInfoAdapter
        extends RecyclerView.Adapter<DecoratedToolInfoAdapter.DecoratedViewHolder> {

    private Context context;
    private List<DecoratedToolInfo> originalData;
    private boolean switchMode = false;

    public DecoratedToolInfoAdapter(Context context, List<DecoratedToolInfo> data) {
        this.context = context;
        this.originalData = data;
    }

    @Override
    public int getItemCount() {
        return originalData.size();
    }

    @NonNull
    @Override
    public DecoratedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.decorated_tool_info_item, parent, false);

        return new DecoratedViewHolder(itemView);
    }

    public void update(final int i, Class<ErrorsTool> errorsToolClass) {
        new ErrorsTool().updateHomeInfo(this);
    }

    static class DecoratedViewHolder extends RecyclerView.ViewHolder {
        View decorator;
        TextView title, message;
        ImageView icon;
        Switch switchButton;

        public DecoratedViewHolder(View view) {
            super(view);
            decorator = view.findViewById(R.id.decorator);
            title = view.findViewById(R.id.title);
            message = view.findViewById(R.id.message);
            icon = view.findViewById(R.id.icon);
            switchButton = view.findViewById(R.id.switch_button);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull DecoratedViewHolder holder, int position) {

        final DecoratedToolInfo data = originalData.get(position);

        holder.title.setText(data.title);
        holder.message.setText(data.message);

        int contextualizedColor = ContextCompat.getColor(holder.itemView.getContext(),data.color);
        holder.title.setTextColor(contextualizedColor);
        holder.decorator.setBackgroundColor(contextualizedColor);

        if(!switchMode){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(data);
                }
            });
        }else{
            holder.icon.setVisibility(View.GONE);
            holder.switchButton.setVisibility(View.VISIBLE);
        }
    }

    protected void onItemClick(DecoratedToolInfo data) {
        OverlayUIService.performNavigationStep(data.getNavigationStep());
    }


    public void enableSwitchMode(){
        switchMode = true;
    }

    public void add(DecoratedToolInfo data){
        originalData.add(data);
        notifyDataSetChanged();
    }

    public void replaceAll(final List<DecoratedToolInfo> data){
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                originalData.clear();
                //notifyDataSetInvalidated();
                originalData.addAll(data);
                notifyDataSetChanged();
            }
        });
    }
}