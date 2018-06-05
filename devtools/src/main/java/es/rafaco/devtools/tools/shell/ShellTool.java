package es.rafaco.devtools.tools.shell;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import es.rafaco.devtools.tools.Tool;
import es.rafaco.devtools.tools.ToolsManager;

public class ShellTool extends Tool {


    private EditText input;
    private Button btn;
    private TextView out;
    private String command;
    private ShellExecuter exe;

    public ShellTool(ToolsManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Shell";
    }

    @Override
    public String getLayoutId() {
        return "tool_shell";
    }

    @Override
    protected void onInit() {

    }

    @Override
    protected void onStart(View toolView) {
        input = (EditText) getView().findViewById(getResourceId(getView(), "id", "txt"));
        btn = (Button) getView().findViewById(getResourceId(getView(), "id", "btn"));
        out = (TextView) getView().findViewById(getResourceId(getView(), "id", "out"));
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
    }

    @Override
    protected void onStop() {
        //TODO!! exe.Executer(cancel previous commmand)
    }

    @Override
    protected void onDestroy() {
    }
}
