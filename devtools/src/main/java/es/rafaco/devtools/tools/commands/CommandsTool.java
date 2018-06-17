package es.rafaco.devtools.tools.commands;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import es.rafaco.devtools.R;
import es.rafaco.devtools.tools.Tool;
import es.rafaco.devtools.tools.ToolsManager;
import es.rafaco.devtools.utils.AppUtils;

public class CommandsTool extends Tool {

    private EditText input;
    private Button btn;
    private TextView out;
    private String command;
    private ShellExecuter exe;
    private Button infoButton;
    private Button devOptionsButton;
    private Button forceCloseButton;
    private Button fullRestartButton;
    private Button installedButton;

    public CommandsTool(ToolsManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Commands";
    }

    @Override
    public String getLayoutId() {
        return "tool_commands";
    }

    @Override
    protected void onInit() {

    }

    @Override
    protected void onStart(View toolView) {
        input = getView().findViewById(R.id.txt);
        btn = getView().findViewById(R.id.btn);
        out = getView().findViewById(R.id.out);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                exe = new ShellExecuter();
                command = input.getText().toString();

                String outp = exe.Executer(command);
                out.setText(outp);
                Log.d("Output", outp);
            }
        });

        infoButton = toolView.findViewById(R.id.info_button);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openAppSettings(getContext());
            }
        });

        installedButton = toolView.findViewById(R.id.installed_button);
        installedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openInstalledApps(getContext());
            }
        });

        devOptionsButton = toolView.findViewById(R.id.dev_options_button);
        devOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openDeveloperOptions(getContext());
            }
        });

        forceCloseButton = toolView.findViewById(R.id.force_close_button);
        forceCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.exit();
            }
        });

        fullRestartButton = toolView.findViewById(R.id.full_restart_button);
        fullRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openDeveloperOptions(getContext());
            }
        });

    }

    @Override
    protected void onStop() {
        //TODO!! exe.Executer(cancel previous commmand)
    }

    @Override
    protected void onDestroy() {
    }
}
