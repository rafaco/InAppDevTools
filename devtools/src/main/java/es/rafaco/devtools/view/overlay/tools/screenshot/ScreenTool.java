package es.rafaco.devtools.view.overlay.tools.screenshot;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.PermissionActivity;
import es.rafaco.devtools.view.overlay.tools.Tool;
import es.rafaco.devtools.view.overlay.tools.ToolsManager;

public class ScreenTool extends Tool {

    private Button shotButton;

    public ScreenTool(ToolsManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Screen";
    }

    @Override
    public String getLayoutId() {
        return "tool_screen";
    }

    @Override
    protected void onInit() {

    }

    @Override
    protected void onStart(View toolView) {
        initView(toolView);
    }


    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }


    private void initView(View toolView) {
        shotButton = toolView.findViewById(R.id.shot_button);
        shotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScreenshotButton();
            }
        });
    }

    private void onScreenshotButton() {
        DevTools.takeScreenshot();
    }
}
