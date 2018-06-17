package es.rafaco.devtools.tools;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevToolsUiService;
import es.rafaco.devtools.R;
import es.rafaco.devtools.utils.ThreadUtils;

public class DecoratedToolInfoAdapter extends BaseAdapter {

    private final Tool tool;
    private Context context;
    private List<DecoratedToolInfo> originalData;
    private LayoutInflater mInflater;
    private boolean switchMode = false;

    public DecoratedToolInfoAdapter(Tool tool, ArrayList<DecoratedToolInfo> data) {
        this.tool = tool;
        this.context = tool.getContext();
        this.originalData = data;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return originalData.size();
    }

    @Override
    public Object getItem(int position) {
        return originalData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        View decorator;
        TextView title;
        TextView message;
        ImageView icon;
        Switch switchButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

       ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.decorated_tool_info_item, null);
            holder = new ViewHolder();
            holder.decorator = convertView.findViewById(R.id.decorator);
            holder.title = convertView.findViewById(R.id.title);
            holder.message = convertView.findViewById(R.id.message);
            holder.icon = convertView.findViewById(R.id.icon);
            holder.switchButton = convertView.findViewById(R.id.switch_button);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final DecoratedToolInfo data = originalData.get(position);

        holder.title.setText(data.title);
        holder.message.setText(data.message);

        holder.title.setTextColor(data.color);
        holder.decorator.setBackgroundColor(data.color);

        if(!switchMode){
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTool(data.title);
                }
            });
        }else{
            holder.icon.setVisibility(View.GONE);
            holder.switchButton.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public void enableSwitchMode(){
        switchMode = true;
    }

    public void startTool(String title){
        Intent intent = DevToolsUiService.buildIntentAction(DevToolsUiService.IntentAction.TOOL, title);
        context.startService(intent);
    }

    public void add(DecoratedToolInfo data){
        originalData.add(data);
        notifyDataSetChanged();
    }

    public void replaceAll(final List<DecoratedToolInfo> data){
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //originalData.clear();
                //notifyDataSetInvalidated();
                originalData = data;
                notifyDataSetChanged();
            }
        });
    }
}
