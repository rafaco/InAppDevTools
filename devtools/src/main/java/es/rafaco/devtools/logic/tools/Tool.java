package es.rafaco.devtools.logic.tools;

import java.util.List;

import es.rafaco.devtools.view.overlay.screens.OverlayScreen;

public abstract class Tool {

    public abstract String getName();

    public abstract Object getManager();
    public abstract List<OverlayScreen> getMainScreens();
    public abstract List<OverlayScreen> getSecondaryScreens();
}
