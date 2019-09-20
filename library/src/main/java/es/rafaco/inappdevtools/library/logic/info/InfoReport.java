package es.rafaco.inappdevtools.library.logic.info;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.info.reporters.AbstractInfoReporter;
import es.rafaco.inappdevtools.library.logic.info.reporters.DeviceInfoReporter;
import es.rafaco.inappdevtools.library.logic.info.reporters.AppInfoReporter;
import es.rafaco.inappdevtools.library.logic.info.reporters.BuildInfoReporter;
import es.rafaco.inappdevtools.library.logic.info.reporters.LiveInfoReporter;
import es.rafaco.inappdevtools.library.logic.info.reporters.OSInfoReporter;
import es.rafaco.inappdevtools.library.logic.info.reporters.ToolsInfoReporter;

public enum InfoReport {

    LIVE("Live", R.drawable.ic_live_tv_white_24dp, LiveInfoReporter.class),
    APP("App", R.drawable.ic_application_white_24dp, AppInfoReporter.class),
    BUILD("Build", R.drawable.ic_build_white_24dp, BuildInfoReporter.class),
    TOOLS("Iadt", R.drawable.ic_extension_white_24dp, ToolsInfoReporter.class),
    OS("OS", R.drawable.ic_android_white_24dp, OSInfoReporter.class),
    DEVICE("Device", R.drawable.ic_phone_android_white_24dp, DeviceInfoReporter.class);

    private String title;
    private final int icon;
    private final Class<? extends AbstractInfoReporter> reporterClass;

    InfoReport(String title, int icon, Class<? extends AbstractInfoReporter> reporterClass) {
        this.title = title;
        this.icon = icon;
        this.reporterClass = reporterClass;
    }

    public String getTitle() {
        return title;
    }
    public int getIcon() {
        return icon;
    }
    public Class<? extends AbstractInfoReporter> getReporterClass() {
        return reporterClass;
    }

    public AbstractInfoReporter getReporter() {
        try {
            Class[] cArg = new Class[2];
            cArg[0] = Context.class;
            cArg[1] = InfoReport.class;
            Context context = IadtController.get().getContext();
            return reporterClass.getDeclaredConstructor(cArg).newInstance(context, this);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
