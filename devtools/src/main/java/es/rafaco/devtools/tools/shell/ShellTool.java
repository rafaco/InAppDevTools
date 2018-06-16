package es.rafaco.devtools.tools.shell;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import es.rafaco.devtools.R;
import es.rafaco.devtools.tools.Tool;
import es.rafaco.devtools.tools.ToolsManager;
import es.rafaco.devtools.tools.DecoratedToolInfo;

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
    }

    @Override
    protected void onStop() {
        //TODO!! exe.Executer(cancel previous commmand)
    }

    @Override
    protected void onDestroy() {
    }

    @Override
    public DecoratedToolInfo getHomeInfo(){
        DecoratedToolInfo info = new DecoratedToolInfo(ShellTool.class,
                getFullTitle(),
                "Uncaught exception handler is activated.", // You will be prompted if something go wrong. \n No exceptions stored.",
                ContextCompat.getColor(getContext(), R.color.rally_orange));
        return  info;
    }
}
