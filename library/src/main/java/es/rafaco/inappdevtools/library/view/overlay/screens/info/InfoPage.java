package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.AbstractInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.AppInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.BuildInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.DeviceInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.LiveInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.OSInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.ToolsInfoHelper;

public enum InfoPage {

    LIVE("Live", LiveInfoHelper.class),
    CONFIG("Build", BuildInfoHelper.class),
    APP("App", AppInfoHelper.class),
    OS("OS", OSInfoHelper.class),
    DEVICE("Device", DeviceInfoHelper.class),
    LIBRARY("Tools", ToolsInfoHelper.class);

    private AbstractInfoHelper helper;
    private InfoPageViewHolder viewHolder;
    private String title;
    private String overview;
    private String content;

    InfoPage(String title, Class<? extends AbstractInfoHelper> helperClass) {
        this.title = title;

        initHelper(helperClass);
        updateFromHelper();

        viewHolder = new InfoPageViewHolder(title, overview, content);
    }

    public void updateFromHelper() {
        //DevTools.Log.v("updateFromHelper for " + title);
        overview = helper.getOverview();
        content = helper.getInfoReport().toString();
    }

    private void initHelper(Class<? extends AbstractInfoHelper> helperClass) {
        try {
            Class[] cArg = new Class[1];
            cArg[0] = Context.class;
            helper = helperClass.getDeclaredConstructor(cArg)
                    .newInstance(DevTools.getAppContext());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }
    public String getOverview() {
        return overview;
    }
    public String getContent() {
        return content;
    }
    public InfoPageViewHolder getViewHolder() {
        return viewHolder;
    }
    
}