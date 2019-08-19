package es.rafaco.inappdevtools.library.logic.log.filter;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//#else
import android.support.v7.app.AlertDialog;
//#endif

import java.util.List;

import es.rafaco.compat.AppCompatTextView;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.layers.OverlayLayer;
import es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog.FriendlyLogAdapter;

public class LogFilterDialog {

    private final Context context;
    FriendlyLogAdapter adapter;
    LogUiFilter helper;
    private AlertDialog dialog;
    private AppCompatTextView currentOverview;

    public LogFilterDialog(Context context, FriendlyLogAdapter adapter, LogUiFilter helper) {
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
                        helper.applyPreset(LogUiFilter.Preset.ALL);
                        adapter.getCurrentList().getDataSource().invalidate();
                        dialog.dismiss();
                    }
                });

        LayoutInflater inflater = LayoutInflater.from(ctw);
        View dialogView = inflater.inflate(R.layout.tool_friendlylog_dialog, null);
        alertDialogBuilder.setView(dialogView);

        currentOverview = dialogView.findViewById(R.id.current_label);
        updateOverview();

        addFilterLine(dialogView, R.id.session_spinner, helper.getSessionOptions(), helper.getSessionInt(),
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        helper.setSessionInt(position);
                        updateOverview();
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
                updateOverview();
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
                updateOverview();
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
                updateOverview();
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
                updateOverview();
                adapter.getCurrentList().getDataSource().invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dialog = alertDialogBuilder.create();
        dialog.getWindow().setType(OverlayLayer.getLayoutType());
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.widget_full_shape);

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
