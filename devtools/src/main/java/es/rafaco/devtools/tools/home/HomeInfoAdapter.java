package es.rafaco.devtools.tools.home;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevToolsService;
import es.rafaco.devtools.R;
import es.rafaco.devtools.tools.Tool;

public class HomeInfoAdapter extends BaseAdapter {

    private final HomeTool tool;
    private Context context;
    private List<HomeInfo> originalData;
    private LayoutInflater mInflater;

    public HomeInfoAdapter(HomeTool tool, ArrayList<HomeInfo> data) {
        this.tool = tool;
        this.context = tool.getView().getContext();
        this.originalData = data;
        Log.d("RAFA", "new HomeInfoAdapter created with "+data.size()+" data.");
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
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

       ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.home_info_item, null);
            holder = new ViewHolder();
            holder.decorator = convertView.findViewById(R.id.decorator);
            holder.title = convertView.findViewById(R.id.title);
            holder.message = convertView.findViewById(R.id.message);
            holder.icon = convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final HomeInfo data = originalData.get(position);

        holder.title.setText(data.title + " Tool");
        holder.message.setText(data.message);

        holder.title.setTextColor(data.color);
        holder.decorator.setBackgroundColor(data.color);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTool(data.title);
            }
        });

        return convertView;
    }

    public void startTool(String title){
        Intent intent = DevToolsService.buildIntentAction(DevToolsService.IntentAction.TOOL, title);
        context.startService(intent);
    }

}
