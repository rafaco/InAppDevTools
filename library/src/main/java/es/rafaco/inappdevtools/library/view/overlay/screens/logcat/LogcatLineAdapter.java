package es.rafaco.inappdevtools.library.view.overlay.screens.logcat;

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

public class LogcatLineAdapter
        extends RecyclerView.Adapter<LogcatLineAdapter.LogViewHolder>
        implements Filterable {

    private final LogcatScreen manager;
    private Context context;
    private List<LogcatLine> originalData;
    private List<LogcatLine> filteredData;

    private LogFilter logFilter;
    private String currentFilterString;

    public LogcatLineAdapter(LogcatScreen manager,
                             ArrayList<LogcatLine> data,
                             LogcatFilterConfig config)
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
                .inflate(R.layout.tool_logcat_item, parent, false);

        return new LogcatLineAdapter.LogViewHolder(itemView);
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
        LogcatLine itemData = getItemByPosition(position);

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

    public LogcatLine getItemByPosition(int position) {
        return filteredData.get(position);
    }

    //endregion


    //region [ FILTER ]

    public Filter getFilter() {
        return logFilter;
    }

    public void updateFilter(LogcatFilterConfig newConfig) {
        logFilter.update(newConfig);
    }

    private class LogFilter extends Filter {

        private LogcatFilterConfig config;

        public LogFilter(LogcatFilterConfig config) {
            super();
            this.config = config;
        }

        public LogcatFilterConfig getConfig() {
            return config;
        }

        public void update(LogcatFilterConfig config){
            this.config = config;
            filter("");
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final List<LogcatLine> originalList = originalData;
            int count = originalList.size();
            final ArrayList<LogcatLine> filteredList = new ArrayList<>(count);
            LogcatLine currentLogLine;
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
            filteredData = (ArrayList<LogcatLine>) results.values;
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
        LogcatLine newLine = LogcatLine.newLogLine(value, false);
        originalData.add(newLine);

        if (logFilter.getConfig().validate(newLine)){

            filteredData.add(LogcatLine.newLogLine(value, false));
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
