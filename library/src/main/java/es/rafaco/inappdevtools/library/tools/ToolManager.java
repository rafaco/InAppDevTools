package es.rafaco.inappdevtools.library.tools;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class ToolManager {

    protected Context context;

    private List<Tool> tools = new ArrayList<>();
    private Tool current = null;

    public ToolManager(Context context) {
        this.context = context;

        //TODO: remove this!
        registerTool(HomeTool.class);
        registerTool(InfoTool.class);
        registerTool(FriendlyLogTool.class);
        registerTool(LogTool.class);
        registerTool(CommandsTool.class);
        registerTool(NetworkTool.class);
        registerTool(ErrorsTool.class);
        registerTool(ScreenTool.class);
        registerTool(ReportTool.class);
    }

    public void registerTool(Class<? extends Tool> toolClass){
        try {
            Tool toolObject = toolClass.newInstance();
            if (toolObject != null) {
                tools.add(toolObject);
                toolObject.onRegister();
            }
        } catch (InstantiationException e) {
            FriendlyLog.logException("Exception", e);
        } catch (IllegalAccessException e) {
            FriendlyLog.logException("Exception", e);
        }
    }

    public Tool getCurrent(){
        return current;
    }

    public Context getContext() {
        return context;
    }


    public Tool get(String name){
        for (Tool tool : tools){
            if (tool.getName().equals(name)){
                return tool;
            }
        }
        return null;
    }

    public Tool get(Class<?> className){
        for (Tool tool : tools) {
            if (className.isInstance(tool)){
                return tool;
            }
        }
        return null;
    }
}
