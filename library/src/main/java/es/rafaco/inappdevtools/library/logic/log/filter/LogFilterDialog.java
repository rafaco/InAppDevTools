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
import android.widget.Spinner;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.appcompat.view.ContextThemeWrapper;
//#else
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
//#endif

import java.util.List;

import es.rafaco.compat.AppCompatTextView;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogAdapter;

public class LogFilterDialog {

    private final Context context;
    LogAdapter adapter;
    LogFilterHelper helper;
    private AlertDialog dialog;
    private AppCompatTextView currentOverview;

    public LogFilterDialog(Context context, LogAdapter adapter, LogFilterHelper helper) {
        this.context = context;
        this.adapter = adapter;
        this.helper = helper;
    }

    public AlertDialog prepare() {
        ContextWrapper ctw = new ContextThemeWrapper(context, R.style.LibTheme_Dialog);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw)
                .setTitle("Log filters")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Show all", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        helper.applyPreset(LogFilterHelper.Preset.ALL);
                        dialog.dismiss();
                    }
                });

        LayoutInflater inflater = LayoutInflater.from(ctw);
        View dialogView = inflater.inflate(R.layout.tool_log_filter, null);
        alertDialogBuilder.setView(dialogView);

        currentOverview = dialogView.findViewById(R.id.current_label);
        updateOverview();

        final LogUiFilter filter = helper.getUiFilter();

        addFilterLine(dialogView, R.id.session_spinner, helper.getSessionOptions(), filter.getSessionInt(),
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        filter.setSessionInt(position);
                        updateOverview();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        addFilterLine(dialogView, R.id.type_spinner, helper.getTypeOptions(), filter.getTypeInt(),
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter.setTypeInt(position);
                updateOverview();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addFilterLine(dialogView, R.id.verbosity_spinner, helper.getSeverityOptions(), filter.getSeverityInt(),
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter.setSeverityInt(position);
                updateOverview();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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

        dialog = alertDialogBuilder.create();
        dialog.getWindow().setType(Layer.getLayoutType());
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_layer_screen_middle);

        return dialog;
    }

    private void updateOverview() {
        currentOverview.setText(helper.getOverview());
    }

    public AlertDialog getDialog() {
        if (dialog == null){
            prepare();
        }
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
}
