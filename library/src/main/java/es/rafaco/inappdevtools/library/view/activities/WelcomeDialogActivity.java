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
        String message = helper.getAppNameAndVersions() + "\n" + " compiled "  + elapsedTimeLowered + "\n\n"
                + "This compilation contains tools to inspect, log and report. Just shake your app to start inspecting it!" + "\n\n"
                + "We are recording your usage of this app locally but only you can share it using the report feature. You can disable all tools now.\n\n";

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
