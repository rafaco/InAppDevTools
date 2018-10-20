package es.rafaco.devtools.view.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import es.rafaco.devtools.R;
import es.rafaco.devtools.tools.ErrorsTool;
import es.rafaco.devtools.logic.utils.ThreadUtils;
import es.rafaco.devtools.view.overlay.OverlayUIService;

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
        ImageView headIcon;
        View decorator;
        TextView title, message;
        ImageView icon;
        Switch switchButton;

        public DecoratedViewHolder(View view) {
            super(view);
            decorator = view.findViewById(R.id.decorator);
            title = view.findViewById(R.id.title);
            message = view.findViewById(R.id.message);
            headIcon = view.findViewById(R.id.head_icon);
            icon = view.findViewById(R.id.icon);
            switchButton = view.findViewById(R.id.switch_button);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull DecoratedViewHolder holder, int position) {

        final DecoratedToolInfo data = originalData.get(position);

        if (TextUtils.isEmpty(data.title))
            holder.title.setVisibility(View.GONE);
        else{
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(data.title);
        }

        if (TextUtils.isEmpty(data.message))
            holder.title.setVisibility(View.GONE);
        else{
            holder.message.setVisibility(View.VISIBLE);
            holder.message.setText(data.message);
        }

        int contextualizedColor = ContextCompat.getColor(holder.itemView.getContext(),data.color);
        holder.title.setTextColor(contextualizedColor);
        holder.decorator.setBackgroundColor(contextualizedColor);

        if (data.icon != -1){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                holder.headIcon.setImageDrawable(context.getApplicationContext().getDrawable(data.icon));
                holder.headIcon.setColorFilter(contextualizedColor);
            } else {
                holder.headIcon.setImageDrawable(context.getResources().getDrawable(data.icon));
            }
            holder.headIcon.setVisibility(View.VISIBLE);
        }else{
            holder.headIcon.setVisibility(View.GONE);
        }

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

        //TODO: temp
        if (data.icon != -1){
            holder.icon.setVisibility(View.GONE);
        }
    }

    protected void onItemClick(DecoratedToolInfo data) {
        if (data.getNavigationStep() != null)
            OverlayUIService.performNavigationStep(data.getNavigationStep());
        else
            data.getRunnable().run();
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
