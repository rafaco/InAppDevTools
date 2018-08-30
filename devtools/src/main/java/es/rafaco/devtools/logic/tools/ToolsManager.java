package es.rafaco.devtools.logic.tools;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.view.overlay.screens.OverlayScreen;

public class ToolsManager {

    protected Context context;

    private List<Tool> tools;
    private Tool current = null;

    public ToolsManager(Context context) {
        this.context = context;
    }

    public void registerTool(Class<? extends Tool> toolClass){
        try {
            Tool toolObject = toolClass.getConstructor(ToolsManager.class).newInstance(this);
            if (toolObject != null) {
                tools.add(toolObject);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
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

/*
    public List<String> getMainScreens(){
        List<String> mainScreens = new ArrayList<>();
        for (OverlayScreen screen : screens) {
            if (screen.isMain()){
                mainScreens.add(screen.getTitle());
            }
        }
        return mainScreens;
    }*/
}
