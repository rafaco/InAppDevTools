package es.rafaco.devtools.logic.tools;

import es.rafaco.devtools.view.overlay.tools.OverlayTool;

public abstract class Tool {

    public abstract String getName();
    public abstract Class<OverlayTool> getOverlay();
}
