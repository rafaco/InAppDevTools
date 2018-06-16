package es.rafaco.devtools.logic.exception;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.DevToolsService;
import es.rafaco.devtools.db.Crash;
import es.rafaco.devtools.db.DevToolsDatabase;


public class ExceptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DevTools", "ExceptionActivity created");

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
                        callServiceAction(DevToolsService.IntentAction.REPORT);
                    }
                })
                .setNegativeButton("RESTART",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        callServiceAction(DevToolsService.IntentAction.RESTART);
                    }
                })
                .setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        callServiceAction(DevToolsService.IntentAction.CLOSE);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void callServiceAction(DevToolsService.IntentAction action) {
        Intent intent = DevToolsService.buildIntentAction(action, null);
        startService(intent);
        finish();
    }
}
