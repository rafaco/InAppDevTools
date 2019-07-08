package es.rafaco.inappdevtools.library.view.activities;

import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.os.Bundle;

//#ifdef MODERN
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.appcompat.app.AppCompatActivity;
//@import androidx.appcompat.view.ContextThemeWrapper;
//#else
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
//#endif

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.Config;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.AppInfoHelper;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class WelcomeDialogActivity extends AppCompatActivity {
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildDialog();
    }

    private void buildDialog() {
        AppInfoHelper helper = new AppInfoHelper(getApplicationContext());
        String elapsedTimeLowered = Humanizer.getElapsedTimeLowered(helper.getAppBuildTime(getApplicationContext()));
        String message = helper.getAppNameAndVersions() + "\n" + "Compiled "  + elapsedTimeLowered + ".\n\n"
                + "This is a developer compilation that contains tools to inspect, log and report. They are locally recording everything happening underneath, but only you can send any of this data using 'Share' or 'Report' features. You can disable this tools now." + "\n\n"

                + "Tip: Shake your device to start!\n\n";

        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setTitle("Developer Tools")
                .setIcon(R.drawable.ic_bug_report_white_24dp)
                .setMessage(message)
                .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("DISABLE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        Iadt.getConfig().setBoolean(Config.ENABLED, false);
                        showConfirmationDialog();
                    }
                })
                .setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showConfirmationDialog() {
        String message = "InAppDevTools has been disabled and your app need to be restarted.\n\n"
            + "To re-enable it clean your application data.\n\n";

        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setTitle("Developer Tools disabled")
                .setIcon(R.drawable.ic_bug_report_white_24dp)
                .setMessage(message)
                .setPositiveButton("RESTART", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        IadtController.get().restartApp(false);
                        finish();
                    }
                })
                .setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
    }
}
