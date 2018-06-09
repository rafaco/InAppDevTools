package es.rafaco.devtools.tools;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.DevToolsService;
import es.rafaco.devtools.tools.info.InfoTool;
import es.rafaco.devtools.tools.log.LogTool;
import es.rafaco.devtools.tools.report.ReportTool;
import es.rafaco.devtools.tools.shell.ShellTool;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ToolsManager {
    protected Context context;
    private final LayoutInflater inflater;
    private List<Tool> tools;
    private Tool currentTool = null;

    public ToolsManager(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.tools = new ArrayList<>();
        initTools();
    }

    private void initTools() {
        getToolList();

        addTool(new InfoTool(this));
        addTool(new LogTool(this));
        addTool(new ShellTool(this));
        addTool(new ReportTool(this));
    }

    public ArrayList<String> getToolList() {
        ArrayList<String> toolsList = new ArrayList<>();
        /*for (Pair<String,String> pair: presetFilters) {
            toolsList.addWidget(pair.first);
        }*/
        toolsList.add("Info");
        toolsList.add("Log");
        toolsList.add("Shell");
        toolsList.add("Report");
        toolsList.add("Close");

        return toolsList;
    }

    public void addTool(Tool Tool){
        tools.add(Tool);
        //Tool.init();
    }

    public View getView(Class<?> toolClass){
        Tool tool = getTool(toolClass);
        if (tool != null)
            return tool.getView();
        return null;
    }

    public Tool getTool(Class<?> toolClass){
        for (Tool tool : tools) {
            if (toolClass.isInstance(tool)){
                return tool;
            }
        }
        return null;
    }

    public Tool getCurrent(){
        //if(currentTool < 0 || currentTool > tools.size() - 1){
        //    return null;
        //}
        return currentTool;//.get(currentTool);
    }



    public void selectTool(String title) {
        Log.d(DevTools.TAG, "Requested new tool: " + title);

        if (title.equals("Close")){
            stopService();
            return;
        }

        //Ignore if already selected
        if (getCurrent() != null && title.equals(getCurrent().getTitle())) {
            return;
        }

        //Destroy current tool
        if (getCurrent() != null) {
            getCurrent().stop();
        }

        for(Tool tool : tools){
            if (tool.getTitle().equals(title)){
                currentTool = tool;
                tool.start();
            }
        }
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public ViewGroup getContainer() {
        return ((DevToolsService)context).getToolContainer();
    }

    private void stopService() {
        ((DevToolsService)context).stopSelf();
    }

    public void destroy() {
        for (Tool tool : tools) {
            tool.destroy();
        }
    }
}
