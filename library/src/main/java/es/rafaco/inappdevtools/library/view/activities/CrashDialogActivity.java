package es.rafaco.inappdevtools.library.view.activities;

import android.content.ContextWrapper;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportHelper;

public class CrashDialogActivity extends AppCompatActivity {

    private Crash crash;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                crash = DevTools.getDatabase().crashDao().getLast();
                CrashDialogActivity.this.buildDialog(crash);
            }
        });
    }

    private void buildDialog(final Crash crash) {
        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_crash, null);
        builder.setView(dialogView)
                .setTitle("Your app crashed and we restarted it")
                .setMessage("Here you have some information about what went wrong.\nPlease report it")
                .setCancelable(false);

        TextView crashTitle = dialogView.findViewById(R.id.detail_title);
        TextView crashSubtitle = dialogView.findViewById(R.id.detail_subtitle);
        TextView crashConsole = dialogView.findViewById(R.id.detail_console);

        crashTitle.setText(crash.getException());
        crashSubtitle.setText(crash.getMessage());
        crashConsole.setText(crash.getStacktrace());

        AppCompatButton crashContinueButton = dialogView.findViewById(R.id.crash_continue_button);
        AppCompatButton crashReportButton = dialogView.findViewById(R.id.crash_report_button);
        AppCompatButton crashDetailButton = dialogView.findViewById(R.id.crash_detail_button);

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
        crashDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCrashDetail(crash);
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void onCrashDetail(Crash crash) {
        if (DevTools.getConfig().overlayUiEnabled){
            Intent intent = OverlayUIService.buildScreenIntentAction(CrashDetailScreen.class, String.valueOf(crash.getUid()));
            DevTools.getAppContext().startService(intent);
            destroyDialog();
        }
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
