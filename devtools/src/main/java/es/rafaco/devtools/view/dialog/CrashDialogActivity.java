package es.rafaco.devtools.view.dialog;

import android.content.ContextWrapper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.utils.ThreadUtils;
import es.rafaco.devtools.view.overlay.screens.report.ReportHelper;

public class CrashDialogActivity extends AppCompatActivity {

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                final Crash crash = DevTools.getDatabase().crashDao().getLast();
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buildDialog(crash);
                    }
                });
            }
        });
    }

    private void buildDialog(final Crash crash) {
        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_crash, null);
        builder.setView(dialogView)
                .setTitle("Your app crashed")
                .setMessage("It has been restarted and the error is stored.")
                .setCancelable(false);

        TextView crashTitle = dialogView.findViewById(R.id.detail_title);
        TextView crashSubtitle = dialogView.findViewById(R.id.detail_subtitle);

        crashTitle.setText(crash.getException());
        crashSubtitle.setText(crash.getStacktrace());

        AppCompatButton crashContinueButton = dialogView.findViewById(R.id.crash_continue_buttons);
        AppCompatButton crashReportButton = dialogView.findViewById(R.id.crash_report_buttons);

        crashContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroyDialog();
            }
        });
        crashReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCrashReport(crash);
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void onCrashReport(Crash crash) {
        DevTools.sendReport(ReportHelper.ReportType.CRASH, crash.getUid());
        destroyDialog();
    }

    private void destroyDialog() {
        alertDialog.dismiss();
        finish();
    }
}
