package es.rafaco.devtools.view.components;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
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
        extends RecyclerView.Adapter<DecoratedViewHolder> {

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
        return new DecoratedViewHolder(parent, switchMode);
    }

    public void update(final int i, Class<ErrorsTool> errorsToolClass) {
        new ErrorsTool().updateHomeInfo(this);
    }

    @Override
    public void onBindViewHolder(@NonNull DecoratedViewHolder holder, int position) {
        final DecoratedToolInfo data = originalData.get(position);
        holder.bindTo(data);
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
