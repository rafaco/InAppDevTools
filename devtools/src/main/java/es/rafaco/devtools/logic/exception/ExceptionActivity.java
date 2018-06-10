package es.rafaco.devtools.logic.exception;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import es.rafaco.devtools.DevToolsService;


public class ExceptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DevTools", "ExceptionActivity created");

        String title = getIntent().getStringExtra("TITLE");
        String message = getIntent().getStringExtra("MESSAGE");
        showDialog(title, message);
    }

    private void showDialog(String title, String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle(title)
                //.setTitle("Ups, I did it again")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("REPORT",new DialogInterface.OnClickListener() {
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
                .setNeutralButton("CLOSE", new DialogInterface.OnClickListener() {
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
