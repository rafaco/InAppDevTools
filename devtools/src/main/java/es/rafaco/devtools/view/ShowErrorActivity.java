package es.rafaco.devtools.view;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.utils.ThreadUtils;

public class ShowErrorActivity extends AppCompatActivity {

    private TextView crashTitle;
    private TextView crashSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_show_error);

        //crashTitle = findViewById(R.id.crash_title);
        //crashSubtitle = findViewById(R.id.crash_subtitle);

        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                final Crash crash = DevTools.getDatabase().crashDao().getLast();
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onData(crash);
                    }
                });
            }
        });

    }

    private void onData(Crash crash) {
        //crashTitle.setText(crash.getException());
        //crashSubtitle.setText(crash.getStacktrace());
        buildDialog(crash.getException(), crash.getStacktrace());
    }


    private void buildDialog(String ti, String su) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_show_error, null);
        builder.setView(dialogView)
                .setTitle("Your app crashed")
                .setMessage("The app has been restarted and the error is stored. You can report it now.")
                .setPositiveButton("REPORT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DevTools.sendReport();
                    }
                })
                .setNegativeButton("CONTINUE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setCancelable(false);

        crashTitle = dialogView.findViewById(R.id.crash_title);
        crashTitle.setText(ti);
        crashSubtitle = dialogView.findViewById(R.id.crash_subtitle);
        crashSubtitle.setText(su);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
