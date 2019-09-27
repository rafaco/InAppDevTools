package es.rafaco.inappdevtools.library.view.overlay.screens.console;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class ConsoleScreen extends Screen {

    private EditText input;
    private Spinner presets;
    private ImageButton btnRun;
    private ImageButton btnCancel;
    private ImageButton btnClear;
    private TextView out;
    private String command;
    private Shell shell;

    public ConsoleScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Console";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_console; }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup view) {
        init();
    }

    @Override
    protected void onStop() {
        if (shell != null) shell.cancel();
    }

    @Override
    protected void onDestroy() {
        if (shell != null) shell.destroy();
    }



    private void init() {
        input = getView().findViewById(R.id.txt);
        presets = getView().findViewById(R.id.presets);
        btnRun = getView().findViewById(R.id.btnRun);
        btnCancel = getView().findViewById(R.id.btnCancel);
        btnClear = getView().findViewById(R.id.btnClear);
        out = getView().findViewById(R.id.out);

        presets = getView().findViewById(R.id.presets);
        List<String> list = new ArrayList<>();
        for (PresetCommand item : PresetCommand.values()) {
            list.add(item.getLabel());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        presets.setAdapter(dataAdapter);
        presets.setPrompt("Or use presets");
        presets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PresetCommand preset = PresetCommand.values()[position];
                input.setText(preset.getCommand());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
        setupButton(btnRun, R.drawable.ic_play_arrow_white_24dp, new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onRun();
            }
        });
        setupButton(btnCancel, R.drawable.ic_stop_white_24dp, new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });
        setupButton(btnClear, R.drawable.ic_delete_forever_white_24dp, new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                clearOutput();
            }
        });
    }

    private void setupButton(ImageButton button, int iconId, View.OnClickListener listener) {
        button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corner));
        UiUtils.setStrokeToDrawable(button.getContext(), 1, R.color.rally_white, button.getBackground());
        //int contextualizedColor = ContextCompat.getColor(getContext(), R.color.rally_bg_solid);
        //button.getBackground().setColorFilter(contextualizedColor, PorterDuff.Mode.MULTIPLY);
        if (iconId>0){
            //Drawable icon = button.getContext().getResources().getDrawable(iconId);
            button.setImageResource(iconId);
        }
        //button.setText(data.getTitle());

        button.setOnClickListener(listener);
    }

    private void onRun() {
        command = input.getText().toString();
        addOutput("> " + command + Humanizer.fullStop());

        shell = new Shell();
        String[] bashCommand = Shell.formatBashCommand(command);
        String result = shell.run(bashCommand);
        addOutput(result + Humanizer.fullStop());
    }

    private void onCancel() {
        if (shell != null) shell.cancel();
    }

    private void clearOutput() {
        out.setText("");
    }

    private void addOutput(String text) {
        out.setText(out.getText() + text);
    }
}
