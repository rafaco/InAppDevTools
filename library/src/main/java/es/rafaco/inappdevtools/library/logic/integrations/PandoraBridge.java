package es.rafaco.inappdevtools.library.logic.integrations;

import android.content.Context;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.utils.ReflexionUtils;
import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.inspector.GridLineView;
import tech.linjiang.pandora.ui.Dispatcher;
import tech.linjiang.pandora.ui.connector.Type;
import tech.linjiang.pandora.util.Config;
import tech.linjiang.pandora.util.Utils;

public class PandoraBridge {

    private static Context getContext(){
        return DevTools.getAppContext();
    }

    public static void init() {
        Utils.init(getContext());
        Config.setSANDBOX_DPM(true);    //enable DeviceProtectMode
        Config.setSHAKE_SWITCH(false);  //disable open overlay on shake
    }

    public static void open() {
        Pandora.get().open();
    }

    public static void select() {
        ReflexionUtils.setPrivateField(Pandora.get(), "preventFree", true);
        Dispatcher.start(getContext().getApplicationContext(), Type.SELECT);
    }

    public static void hierarchy() {
        ReflexionUtils.setPrivateField(Pandora.get(), "preventFree", true);
        Dispatcher.start(getContext().getApplicationContext(), Type.HIERARCHY);
    }

    public static void storage() {
        ReflexionUtils.setPrivateField(Pandora.get(), "preventFree", true);
        Dispatcher.start(getContext().getApplicationContext(), Type.FILE);
    }

    public static void grid() {
        GridLineView.toggle();
    }

    public static void measure() {
        Dispatcher.start(getContext().getApplicationContext(), Type.BASELINE);
    }
}
