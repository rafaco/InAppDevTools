/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.logic.log.filter;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.appcompat.view.ContextThemeWrapper;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.compat.AppCompatTextView;
import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.flex.CardData;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;

public class LogFilterDialog {

    private final Context context;
    private final Runnable updateLister;
    LogFilterHelper helper;
    private AlertDialog dialog;
    private AppCompatTextView currentOverview;
    private int lastSelectedSessionId;

    public LogFilterDialog(Context context, LogFilterHelper helper, Runnable updateListener) {
        this.context = context;
        this.helper = helper;
        this.updateLister = updateListener;
    }

    public void show() {
        if (LogFilterStore.get() == null){
            showStandardDialog();
        }else{
            showAdvancedDialog();
        }
    }

    public void showStandardDialog() {
        dialog = buildPresetDialog();
        dialog.getWindow().setType(Layer.getLayoutType());
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_layer_screen_middle);
        dialog.show();
    }

    public void showAdvancedDialog() {
        dialog = buildCustomDialog();
        dialog.getWindow().setType(Layer.getLayoutType());
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_layer_screen_middle);
        dialog.show();
    }

    private void onPresetSelected(LogFilterHelper.Preset preset) {
        helper.applyPreset(preset);
        dialog.dismiss();
        updateLister.run();
    }

    private void onBackToPresets() {
        dialog.dismiss();

        dialog = buildPresetDialog();
        dialog.getWindow().setType(Layer.getLayoutType());
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_layer_screen_middle);
        dialog.show();
    }

    private AlertDialog buildPresetDialog() {
        ContextWrapper ctw = new ContextThemeWrapper(context, R.style.LibTheme_Dialog);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw)
                .setTitle("Filter profiles")
                .setIcon(R.drawable.ic_format_list_bulleted_white_24dp)
                .setCancelable(true);

        LayoutInflater inflater = LayoutInflater.from(ctw);
        View dialogView = inflater.inflate(R.layout.flexible_container, null);
        alertDialogBuilder.setView(dialogView);

        List<Object> data = new ArrayList<>();

        data.add(new CardData("Repro Steps",
                "Important events from current session",
                R.string.gmd_format_list_numbered,
                new Runnable() {
                    @Override
                    public void run() {
                        onPresetSelected(LogFilterHelper.Preset.REPRO_STEPS);
                    }
                }));

        /*data.add(new CardData("Network",
                "Data request from current session",
                R.string.gmd_cloud,
                new Runnable() {
                    @Override
                    public void run() {
                        onPresetSelected(LogFilterHelper.Preset.NETWORK);
                    }
                }));*/

        data.add(new CardData("Debug",
                "Full logs from current session",
                R.string.gmd_android,
                new Runnable() {
                    @Override
                    public void run() {
                        onPresetSelected(LogFilterHelper.Preset.DEBUG);
                    }
                }));

        data.add(new CardData("Crashes",
                "Crash events from all session",
                R.string.gmd_bug_report,
                new Runnable() {
                    @Override
                    public void run() {
                        onPresetSelected(LogFilterHelper.Preset.CRASHES);
                    }
                }));

        data.add("");
        data.add(new CardData("Custom",
                "Fine tune your filter",
                R.string.gmd_tune,
                new Runnable() {
                    @Override
                    public void run() {
                        onPresetSelected(LogFilterHelper.Preset.CUSTOM);
                        showAdvancedDialog();
                    }
                }));

        FlexibleAdapter presetAdapter = new FlexibleAdapter(1, data);
        RecyclerView recyclerView = dialogView.findViewById(R.id.flexible);
        recyclerView.setAdapter(presetAdapter);

        return alertDialogBuilder.create();
    }

    private AlertDialog buildCustomDialog() {
        ContextWrapper ctw = new ContextThemeWrapper(context, R.style.LibTheme_Dialog);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw)
                .setTitle("Custom filter")
                .setIcon(R.drawable.ic_tune_white_24dp)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        updateLister.run();
                    }
                })
                .setNeutralButton("Profiles", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackToPresets();
                        dialog.dismiss();
                    }
                });

        LayoutInflater inflater = LayoutInflater.from(ctw);
        View dialogView = inflater.inflate(R.layout.tool_log_filter, null);
        alertDialogBuilder.setView(dialogView);

        final LogUiFilter filter = helper.getUiFilter();
        currentOverview = dialogView.findViewById(R.id.current_label);
        updateOverview();

        RadioGroup typeGroup = dialogView.findViewById(R.id.type_group);
        final List<Integer> types = new ArrayList<>();
        types.add(R.id.type_mixed);
        types.add(R.id.type_event);
        types.add(R.id.type_logcat);
        typeGroup.check(types.get(filter.getTypeInt()));
        typeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                filter.setTypeInt(types.indexOf(checkedId));
                updateOverview();
            }
        });

        final RadioGroup sessionGroup = dialogView.findViewById(R.id.session_group);
        final Spinner sessionSpinner = dialogView.findViewById(R.id.session_spinner);
        final List<Integer> sessions = new ArrayList<>();
        sessions.add(R.id.session_all);
        sessions.add(R.id.session_current);
        sessions.add(R.id.session_previous);
        sessions.add(R.id.session_other);
        lastSelectedSessionId = (filter.getSessionInt() < 3)? sessions.get(filter.getSessionInt())
            : R.id.session_other;

        final boolean isFirstSession = IadtController.getDatabase().sessionDao().count()==1;
        if (isFirstSession) sessionGroup.findViewById(R.id.session_previous).setVisibility(View.GONE);
        sessionGroup.check(lastSelectedSessionId);
        sessionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.session_other){
                    //sessionSpinner.performClick();
                    return;
                }
                else {
                    lastSelectedSessionId = checkedId;
                    filter.setSessionInt(sessions.indexOf(checkedId));
                }
                updateOverview();
            }
        });

        RadioButton chooseButton = sessionGroup.findViewById(R.id.session_other);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionSpinner.performClick();
                return;
            }
        });
        addFilterLine(dialogView, R.id.session_spinner, helper.getSessionOptions(), filter.getSessionInt(),
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < 3) {
                    RadioButton button = sessionGroup.findViewById(sessions.get(position));
                    button.setChecked(true);
                }
                else{
                    filter.setSessionInt(position);
                    updateOverview();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //TODO: PopUp dismiss should restore previous selection
                RadioButton button = sessionGroup.findViewById(lastSelectedSessionId);
                button.setChecked(true);
            }
        });


        RadioGroup severityGroup = dialogView.findViewById(R.id.severity_group);
        final List<Integer> severities = new ArrayList<>();
        severities.add(R.id.severity_v);
        severities.add(R.id.severity_d);
        severities.add(R.id.severity_i);
        severities.add(R.id.severity_w);
        severities.add(R.id.severity_e);
        severityGroup.check(severities.get(filter.getSeverityInt()));
        severityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                filter.setSeverityInt(severities.indexOf(checkedId));
                updateOverview();
            }
        });


        addFilterLine(dialogView, R.id.category_spinner, helper.getCategoryOptions(), filter.getCategoryInt(), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String realCategory = "All";
                if (position > 0 && parent.getAdapter().getCount() >= position){
                    String fullString = ((String)parent.getAdapter().getItem(position));
                    realCategory = fullString.substring(0, fullString.indexOf(" "));
                }
                filter.setCategoryInt(position);
                filter.setCategoryName(realCategory);
                updateOverview();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addFilterLine(dialogView, R.id.logcat_tag_spinner, helper.getTagOptions(), filter.getTagInt(), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String realCategory = "All";
                if (position > 0 && parent.getAdapter().getCount() >= position){
                    String fullString = ((String)parent.getAdapter().getItem(position));
                    realCategory = fullString.substring(0, fullString.indexOf(" "));
                }
                filter.setTagInt(position);
                filter.setTagName(realCategory);
                updateOverview();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return alertDialogBuilder.create();
    }

    private void updateOverview() {
        currentOverview.setText(helper.getOverview());
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    private void addFilterLine(View dialogView, int spinnerResId, List<String> list, int selected, AdapterView.OnItemSelectedListener listener) {
        Spinner typeSpinner = dialogView.findViewById(spinnerResId);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(dataAdapter);
        typeSpinner.setSelection(selected, false);
        typeSpinner.setOnItemSelectedListener(listener);
    }

    /*private void showFilterPopup(View v) {
        PopupMenu popup = new PopupMenu(context, v);

        // Inflate the menu from xml
        popup.inflate(R.menu.popup_filter);
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_keyword:
                        Toast.makeText(MainActivity.this, "Keyword!", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_popularity:
                        Toast.makeText(MainActivity.this, "Popularity!", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }*/
}
