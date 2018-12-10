package es.rafaco.inappdevtools.library.view.activities;

import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import android.view.LayoutInflater;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoHelper;

public class WelcomeDialogActivity extends AppCompatActivity {
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildDialog();
    }

    private void buildDialog() {

        InfoHelper helper = new InfoHelper();
        String elapsedTimeLowered = DateUtils.getElapsedTimeLowered(helper.getAppBuildTime(getApplicationContext()));
        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        LayoutInflater inflater = getLayoutInflater();
        //View dialogView = inflater.inflate(R.layout.dialog_welcome, null);
        //builder.setView(dialogView)
        builder.setTitle("Test version")
                .setMessage(helper.getFormattedAppName() + "\n" + "Build "  + elapsedTimeLowered + "\n\n" +
                        "This app contains a tool to inspect your running app. It's also auto-logging what is happening underneath and it allow you to report straight to the developer team.\n\nYou can send them bugs, screenshot, logs, reproduction steps or just your feedback. To start reporting you can:\n - Shake your device\n - Use the notification\n - Slide up from bottom of screen")
                .setIcon(R.drawable.ic_warning_yellow_24dp)
                .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("OPEN TOOLS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        DevTools.openTools(true);
                        finish();
                    }
                })
                .setCancelable(true);

        alertDialog = builder.create();
        alertDialog.show();
    }
}
