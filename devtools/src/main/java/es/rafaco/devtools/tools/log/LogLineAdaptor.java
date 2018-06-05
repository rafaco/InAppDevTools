package es.rafaco.devtools.tools.log;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.R;

public class LogLineAdaptor extends BaseAdapter implements Filterable {

    private final LogTool manager;
    private Context context;
    private List<LogLine> originalData;
    private List<LogLine> filteredData;
    private LayoutInflater mInflater;

    private LogFilter logFilter;
    private String currentFilterString;

    public LogLineAdaptor(LogTool manager, ArrayList<LogLine> data, LogFilterConfig config) {
        this.manager = manager;
        this.context = manager.getView().getContext();
        this.logFilter = new LogFilter(config);
        this.originalData = data;
        this.filteredData = new ArrayList<>(data);
        Log.d("RAFA", "new LogLineAdaptor created with "+data.size()+" data.");
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return (filteredData != null) ? filteredData.size() : 0;
    }

    @Override
    public LogLine getItem(int position) {
        return (filteredData != null) ? filteredData.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(String value, int id) {
        LogLine newLine = LogLine.newLogLine(value, false);
        originalData.add(newLine);

        if (logFilter.getConfig().validate(newLine)){
            filteredData.add(LogLine.newLogLine(value, false));
            notifyDataSetChanged();
        }
    }

    public void clear(){
        originalData.clear();
        filteredData.clear();

        notifyDataSetInvalidated();
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.tool_log_item, null);
            holder = new ViewHolder();
            holder.text = convertView.findViewById(R.id.txtLogString);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        LogLine itemData = filteredData.get(position);
        String type = itemData.getLogLevelText();
        String line = itemData.getLogOutput();

        if(TextUtils.isEmpty(currentFilterString)){
            holder.text.setText(line);
        }
        else{
            highlightString(line, currentFilterString, holder.text);
        }

        holder.text.setTextColor(getLogColor(type));

        return convertView;
    }

    public void updateFilter(LogFilterConfig newConfig) {
        logFilter.update(newConfig);
    }

    static class ViewHolder {
        TextView text;
    }

    //region [ FILTER ]

    public Filter getFilter() {
        return logFilter;
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

            currentFilterString = constraint.toString().toLowerCase();

            final List<LogLine> originalList = originalData;
            int count = originalList.size();
            final ArrayList<LogLine> filteredList = new ArrayList<>(count);
            String filterableString;
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
            manager.showOutputToast(String.format("Showing %s of %s", filteredData.size(), originalData.size()));
        }
    }

    //endregion


    //region [ UI UTIL ]

    public static int getLogColor(String type) {
        int color = Color.BLUE;
        if(type.equals("D")){
            color = Color.rgb(0, 0, 200);
        }
        else if(type.equals("I")){
            color = Color.rgb(0, 128, 0);
        }
        else if(type.equals("W")){
            color = Color.parseColor("#827717");//rgb(255, 234, 0);
        }
        else if(type.equals("E") || type.equals("F")){
            color = Color.rgb(255, 0, 0);
        }
        return color;
    }

    private void highlightString(CharSequence text, String keyword, TextView textView) {

        SpannableString spannableString = new SpannableString(text);

        /* Remove previous spans
        BackgroundColorSpan[] backgroundSpans = spannableString.getSpans(0, spannableString.length(), BackgroundColorSpan.class);
        for (BackgroundColorSpan span: backgroundSpans) {
            spannableString.removeSpan(span);
        }*/

        int indexOfKeyword = spannableString.toString().indexOf(keyword);
        while (indexOfKeyword > 0) {
            BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.parseColor("#DC3ABC00"));
            spannableString.setSpan(backgroundColorSpan, indexOfKeyword, indexOfKeyword + keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //ColorStateList blueColor = new ColorStateList(new int[][] { new int[] {}}, new int[] { Color.BLUE });
            //TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(null, Typeface.BOLD_ITALIC, -1, blueColor, null);
            //spannableString.setSpan(textAppearanceSpan, indexOfKeyword, indexOfKeyword + keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            indexOfKeyword = spannableString.toString().indexOf(keyword, indexOfKeyword + keyword.length());
        }

        textView.setText(spannableString);
    }

    //endregion
}
