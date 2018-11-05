package es.rafaco.devtools.logic.crash;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.storage.db.entities.Crash;
import es.rafaco.devtools.storage.db.DevToolsDatabase;


public class CrashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DevTools", "CrashActivity created");

        String title = getIntent().getStringExtra("TITLE");
        String message = getIntent().getStringExtra("MESSAGE");
        Crash crash = (Crash) getIntent().getSerializableExtra("CRASH");
        showDialog(title, message);

        storeCrash(crash);
    }

    private void storeCrash(final Crash crash) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DevToolsDatabase db = DevTools.getDatabase();
                db.crashDao().insertAll(crash);
                Log.d(DevTools.TAG, "Crash stored in db");
            }
        });
    }

    private void showDialog(String title, String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle(title)
                //.setTitle("Ups, I did it again")
                .setMessage(message)
                .setCancelable(false)
                .setNeutralButton("REPORT",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        callServiceAction(OverlayUIService.IntentAction.REPORT);
                    }
                })
                .setNegativeButton("RESTART_APP",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        callServiceAction(OverlayUIService.IntentAction.RESTART_APP);
                    }
                })
                .setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        callServiceAction(OverlayUIService.IntentAction.CLOSE_APP);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void callServiceAction(OverlayUIService.IntentAction action) {
        Intent intent = OverlayUIService.buildIntentAction(action, null);
        startService(intent);
        finish();
    }
}
