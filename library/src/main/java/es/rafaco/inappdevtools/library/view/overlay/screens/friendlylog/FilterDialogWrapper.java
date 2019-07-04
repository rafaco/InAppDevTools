package es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.log.datasource.LogAnalysisHelper;
import es.rafaco.inappdevtools.library.logic.log.datasource.LogFilterHelper;
import es.rafaco.inappdevtools.library.storage.db.entities.AnalysisItem;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.layers.OverlayLayer;

public class FilterDialogWrapper {

    private final Context context;
    FriendlyLogAdapter adapter;
    LogFilterHelper helper;
    private AlertDialog dialog;

    public FilterDialogWrapper(Context context, FriendlyLogAdapter adapter, LogFilterHelper helper) {
        this.context = context;
        this.adapter = adapter;
        this.helper = helper;
    }

    public AlertDialog prepare() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setTitle("Log filters")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.tool_friendlylog_dialog, null);
        alertDialogBuilder.setView(dialogView);

        AppCompatTextView currentOverview = dialogView.findViewById(R.id.current_label);
        currentOverview.setText(helper.getOverview());

        addFilterLine(dialogView, R.id.session_spinner, helper.getSessionOptions(), helper.getSessionInt(),
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        helper.setSessionInt(position);
                        adapter.getCurrentList().getDataSource().invalidate();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        addFilterLine(dialogView, R.id.type_spinner, helper.getTypeOptions(), helper.getTypeInt(),
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                helper.setTypeInt(position);
                adapter.getCurrentList().getDataSource().invalidate();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addFilterLine(dialogView, R.id.verbosity_spinner, helper.getSeverityOptions(), helper.getSeverityInt(), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                helper.setSeverityInt(position);
                adapter.getCurrentList().getDataSource().invalidate();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addFilterLine(dialogView, R.id.category_spinner, helper.getCategoryOptions(), helper.getCategoryInt(), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String realCategory = "All";
                if (position > 0 && parent.getAdapter().getCount() >= position){
                    String fullString = ((String)parent.getAdapter().getItem(position));
                    realCategory = fullString.substring(0, fullString.indexOf(" "));
                }
                helper.setCategoryInt(position, realCategory);
                adapter.getCurrentList().getDataSource().invalidate();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addFilterLine(dialogView, R.id.logcat_tag_spinner, helper.getTagList(), helper.getTagInt(), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String realCategory = "All";
                if (position > 0 && parent.getAdapter().getCount() >= position){
                    String fullString = ((String)parent.getAdapter().getItem(position));
                    realCategory = fullString.substring(0, fullString.indexOf(" "));
                }
                helper.setTagInt(position, realCategory);
                adapter.getCurrentList().getDataSource().invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dialog = alertDialogBuilder.create();
        dialog.getWindow().setType(OverlayLayer.getLayoutType());

        return dialog;
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
