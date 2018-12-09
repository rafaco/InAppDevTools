package es.rafaco.inappdevtools.tools;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

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
        registerTool(StorageTool.class);
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
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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
