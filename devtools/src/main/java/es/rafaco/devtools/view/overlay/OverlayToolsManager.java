package es.rafaco.devtools.view.overlay;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.view.OverlayUIService;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayer;
import es.rafaco.devtools.view.overlay.tools.OverlayTool;
import es.rafaco.devtools.view.overlay.tools.commands.CommandsTool;
import es.rafaco.devtools.view.overlay.tools.errors.ErrorsTool;
import es.rafaco.devtools.view.overlay.tools.home.HomeTool;
import es.rafaco.devtools.view.overlay.tools.info.InfoTool;
import es.rafaco.devtools.view.overlay.tools.log.LogTool;
import es.rafaco.devtools.view.overlay.tools.report.ReportTool;
import es.rafaco.devtools.view.overlay.tools.screenshot.ScreenTool;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class OverlayToolsManager {
    private final MainOverlayLayer mainLayer;
    protected Context context;
    private final LayoutInflater inflater;
    private List<OverlayTool> tools;
    private OverlayTool currentOverlayTool = null;

    public OverlayToolsManager(Context context, MainOverlayLayer mainLayer) {
        this.context = context;
        this.mainLayer = mainLayer;
        this.inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.tools = new ArrayList<>();
        initTools();
    }

    private void initTools() {
        getToolList();

        addTool(new HomeTool(this));
        addTool(new InfoTool(this));
        addTool(new ErrorsTool(this));
        addTool(new LogTool(this));
        addTool(new CommandsTool(this));
        addTool(new ScreenTool(this));
        addTool(new ReportTool(this));
    }

    public ArrayList<String> getToolList() {
        ArrayList<String> toolsList = new ArrayList<>();
        /*for (Pair<String,String> pair: presetFilters) {
            toolsList.addLayer(pair.first);
        }*/
        toolsList.add("Home");
        toolsList.add("Info");
        toolsList.add("Errors");
        toolsList.add("Log");
        toolsList.add("Commands");
        toolsList.add("Screen");
        toolsList.add("Report");

        return toolsList;
    }

    public void addTool(OverlayTool OverlayTool){
        tools.add(OverlayTool);
        //OverlayTool.init();
    }

    public View getView(Class<?> toolClass){
        OverlayTool overlayTool = getTool(toolClass);
        if (overlayTool != null)
            return overlayTool.getView();
        return null;
    }

    public OverlayTool getTool(Class<?> toolClass){
        for (OverlayTool overlayTool : tools) {
            if (toolClass.isInstance(overlayTool)){
                return overlayTool;
            }
        }
        return null;
    }

    public OverlayTool getCurrent(){
        //if(currentOverlayTool < 0 || currentOverlayTool > tools.size() - 1){
        //    return null;
        //}
        return currentOverlayTool;//.get(currentOverlayTool);
    }



    public void selectTool(String title) {
        Log.d(DevTools.TAG, "Requested new tool: " + title);

        //Ignore if already selected
        if (getCurrent() != null && title.equals(getCurrent().getTitle())) {
            return;
        }

        //Destroy current tool
        if (getCurrent() != null) {
            getCurrent().stop();
        }

        for(OverlayTool overlayTool : tools){
            if (overlayTool.getTitle().equals(title)){
                currentOverlayTool = overlayTool;
                overlayTool.start();
            }
        }
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    private void stopService() {
        ((OverlayUIService)context).stopSelf();
    }

    public void destroy() {
        for (OverlayTool overlayTool : tools) {
            overlayTool.destroy();
        }
    }


    public ViewGroup getToolWrapper() {
        return mainLayer.getToolWrapper();
    }


    //TODO:
    public void updateHomeInfoContent(Class<?> toolClass, String content){
        ((HomeTool)getTool(HomeTool.class)).updateContent(toolClass, content);
    }

}
