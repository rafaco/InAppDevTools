package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.AbstractInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.AppInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.BuildInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.DeviceInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.LiveInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.OSInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.ToolsInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;

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

        this.title = title;
        overview = helper.getOverview();
        content = helper.getInfoReport().toString();

        viewHolder = new InfoPageViewHolder(title, overview, content);
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