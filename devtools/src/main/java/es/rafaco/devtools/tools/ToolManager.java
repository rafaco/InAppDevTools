package es.rafaco.devtools.tools;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.view.utils.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;

public class ToolManager {

    protected Context context;

    private List<Tool> tools = new ArrayList<>();
    private Tool current = null;

    public ToolManager(Context context) {
        this.context = context;
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

    public List<Class<? extends OverlayScreen>> getMainScreens(){
        List<Class<? extends OverlayScreen>> mainScreens = new ArrayList<>();
        for (Tool tool : tools) {
            Class<? extends OverlayScreen> screen = tool.getMainScreen();
            if (screen != null){
                mainScreens.add(screen);
            }
        }
        return mainScreens;
    }

    public List<DecoratedToolInfo> getHomeInfos() {
        List<DecoratedToolInfo> result = new ArrayList<>();
        for (Tool tool : tools) {
            DecoratedToolInfo info = tool.getHomeInfo();
            if (info != null){
                result.add(info);
            }
        }
        return result;
    }

    public List<DecoratedToolInfo> getReportInfos() {
        List<DecoratedToolInfo> result = new ArrayList<>();
        for (Tool tool : tools) {
            DecoratedToolInfo info = tool.getReportInfo();
            if (info != null){
                result.add(info);
            }
        }
        return result;
    }
}
