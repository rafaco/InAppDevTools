/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.inappdevtools.library.view.overlay.screens.device;

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

import org.inappdevtools.library.R;
import org.inappdevtools.library.view.overlay.ScreenManager;
import org.inappdevtools.library.view.overlay.screens.Screen;
import org.inappdevtools.library.view.utils.Humanizer;
import org.inappdevtools.library.view.utils.UiUtils;

public class TerminalScreen extends Screen {

    private EditText input;
    private Spinner presets;
    private ImageButton btnRun;
    private ImageButton btnCancel;
    private ImageButton btnClear;
    private TextView out;
    private String command;
    private Shell shell;

    public TerminalScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Terminal";
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
        for (TerminalPreset item : TerminalPreset.values()) {
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
                TerminalPreset preset = TerminalPreset.values()[position];
                input.setText(preset.getCommand());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
        UiUtils.setupIconButton(btnRun, R.drawable.ic_play_arrow_white_24dp, new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onRun();
            }
        });
        UiUtils.setupIconButton(btnCancel, R.drawable.ic_stop_white_24dp, new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });
        UiUtils.setupIconButton(btnClear, R.drawable.ic_delete_forever_white_24dp, new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                clearOutput();
            }
        });
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
