package es.rafaco.devtools.view.dialog;

import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.utils.DateUtils;
import es.rafaco.devtools.view.overlay.screens.info.InfoHelper;

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
                        "This app contains a tool to help you communicate with the development team and to gather useful information about what is happening underneath.\n\nYou can send them bugs, screenshot, logs, reproduction steps or just your feedback. To start reporting you can:\n - Shake your device\n - Use the notification\n - Slide up from bottom of screen")
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
