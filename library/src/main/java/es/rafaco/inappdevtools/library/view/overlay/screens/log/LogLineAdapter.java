package es.rafaco.inappdevtools.library.view.overlay.screens.log;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

//#ifdef MODERN
//@import androidx.annotation.NonNull;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class LogLineAdapter
        extends RecyclerView.Adapter<LogLineAdapter.LogViewHolder>
        implements Filterable {

    private final LogScreen manager;
    private Context context;
    private List<LogLine> originalData;
    private List<LogLine> filteredData;

    private LogFilter logFilter;
    private String currentFilterString;

    public LogLineAdapter(LogScreen manager,
                          ArrayList<LogLine> data,
                          LogFilterConfig config)
    {
        this.manager = manager;
        this.context = manager.getView().getContext();
        this.logFilter = new LogFilter(config);
        this.originalData = data;
        this.filteredData = new ArrayList<>(data);
    }


    //region [ ADAPTOR ]

    @Override
    public int getItemCount() {
        return (filteredData != null) ? filteredData.size() : 0;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tool_log_item, parent, false);

        return new LogLineAdapter.LogViewHolder(itemView);
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public LogViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.txtLogString);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogLine itemData = getItemByPosition(position);

        String type = itemData.getLogLevelText();
        String line = itemData.getLogOutput();
        int color = itemData.getLogColor(context);

        String textFilter = ((LogFilter)getFilter()).getConfig().textFilter;
        if(TextUtils.isEmpty(textFilter)){
            holder.text.setText(line);
        }
        else{
            UiUtils.highlightString(context, line, textFilter, holder.text);
        }

        holder.text.setTextColor(color);

    }

    public LogLine getItemByPosition(int position) {
        return filteredData.get(position);
    }

    //endregion


    //region [ FILTER ]

    public Filter getFilter() {
        return logFilter;
    }

    public void updateFilter(LogFilterConfig newConfig) {
        logFilter.update(newConfig);
    }

    private class LogFilter extends Filter {

        private LogFilterConfig config;

        public LogFilter(LogFilterConfig config) {
            super();
            this.config = config;
        }

        public LogFilterConfig getConfig() {
            return config;
        }

        public void update(LogFilterConfig config){
            this.config = config;
            filter("");
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final List<LogLine> originalList = originalData;
            int count = originalList.size();
            final ArrayList<LogLine> filteredList = new ArrayList<>(count);
            LogLine currentLogLine;
            for (int i = 0; i < count; i++) {
                currentLogLine = originalList.get(i);
                if (config.validate(currentLogLine)){
                    filteredList.add(currentLogLine);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<LogLine>) results.values;
            notifyDataSetChanged();
            manager.showFilterOutputToast();
        }
    }

    public String getOriginalSize() {
        return String.valueOf(originalData.size());
    }

    public String getFilteredSize() {
        return String.valueOf(filteredData.size());
    }

    //endregion

    //region [ UPDATE DATA ]

    public void add(String value, int id) {
        LogLine newLine = LogLine.newLogLine(value, false);
        originalData.add(newLine);

        if (logFilter.getConfig().validate(newLine)){

            filteredData.add(LogLine.newLogLine(value, false));
            //notifyDataSetChanged();
            notifyItemInserted(filteredData.size()-1);

            manager.getScreenManager().getMainLayer().scrollBottom();
        }
    }

    public void clear(){
        originalData.clear();
        filteredData.clear();
        notifyDataSetChanged();
    }

    //endregion
}
