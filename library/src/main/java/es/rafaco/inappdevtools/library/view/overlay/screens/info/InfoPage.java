package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.AbstractInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.AppInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.BuildInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.DeviceInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.LiveInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.OSInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.ToolsInfoHelper;

public enum InfoPage {

    LIVE("Live", R.drawable.ic_live_tv_white_24dp, LiveInfoHelper.class),
    APP("App", R.drawable.ic_application_white_24dp, AppInfoHelper.class),
    BUILD("Build", R.drawable.ic_build_white_24dp, BuildInfoHelper.class),
    LIBRARY("Iadt", R.drawable.ic_extension_white_24dp, ToolsInfoHelper.class),
    OS("OS", R.drawable.ic_android_white_24dp, OSInfoHelper.class),
    DEVICE("Device", R.drawable.ic_phone_android_white_24dp, DeviceInfoHelper.class);

    private AbstractInfoHelper helper;
    private InfoPageViewHolder viewHolder;
    private final int icon;
    private String title;
    private String overview;
    private String content;

    InfoPage(String title, int icon, Class<? extends AbstractInfoHelper> helperClass) {
        this.title = title;
        this.icon = icon;

        initHelper(helperClass);
        updateFromHelper();

        viewHolder = new InfoPageViewHolder(title, overview, content);
    }

    public void updateFromHelper() {
        //Iadt.Log.v("updateFromHelper for " + title);
        overview = helper.getOverview();
        content = helper.getInfoReport().toString();
    }

    private void initHelper(Class<? extends AbstractInfoHelper> helperClass) {
        try {
            Class[] cArg = new Class[1];
            cArg[0] = Context.class;
            Context context = IadtController.get().getContext();
            helper = helperClass.getDeclaredConstructor(cArg)
                    .newInstance(context);
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

    public int getIcon() {
        return icon;
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
