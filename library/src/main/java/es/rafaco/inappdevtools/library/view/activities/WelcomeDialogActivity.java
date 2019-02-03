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
        String message = helper.getFormattedAppName() + "\n" + "Build "  + elapsedTimeLowered + "\n\n" +
                        "This app contains a set of tool to log, report and inspect.\n\n";


        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        LayoutInflater inflater = getLayoutInflater();
        //View dialogView = inflater.inflate(R.layout.dialog_welcome, null);
        //builder.setView(dialogView)
        builder.setTitle("DevTools")
                .setIcon(R.drawable.ic_warning_yellow_24dp)
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
                        DevTools.showMessage("Disabled coming soon");
                        finish();
                    }
                })
                .setCancelable(true);

        alertDialog = builder.create();
        alertDialog.show();
    }
}
