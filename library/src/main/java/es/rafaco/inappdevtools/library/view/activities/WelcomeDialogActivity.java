package es.rafaco.inappdevtools.library.view.activities;

import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.appcompat.app.AppCompatActivity;
//@import androidx.appcompat.view.ContextThemeWrapper;
//#else
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
//#endif

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.Config;

public class WelcomeDialogActivity extends AppCompatActivity {

    public enum IntentAction {PRIVACY, OVERLAY, DISABLE}
    public static final String EXTRA_INTENT_ACTION = "EXTRA_INTENT_ACTION";
    private static Runnable onSuccess;
    private static Runnable onFailure;
    private IntentAction currentAction;

    private AlertDialog alertDialog;

    public static void open(IntentAction action, Runnable onSuccessCallback, Runnable onFailureCallback) {
        if (onSuccessCallback!=null)
            onSuccess = onSuccessCallback;

        if (onFailureCallback!=null)
            onFailure = onFailureCallback;

        if (true){ //TODO: pre-checks to avoid opening an unnecessary activity
            Intent intent = new Intent(Iadt.getAppContext(), WelcomeDialogActivity.class);
            intent.putExtra(EXTRA_INTENT_ACTION, action);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Iadt.getAppContext().startActivity(intent, null);
        }else{
            if (onSuccess !=null)
                onSuccess.run();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentAction = (IntentAction) getIntent().getSerializableExtra(EXTRA_INTENT_ACTION);
        if (currentAction != null) {
            if (currentAction.equals(IntentAction.PRIVACY)) {
                showFirstDialog();
            } else if (currentAction.equals(IntentAction.OVERLAY)) {
                showOverlayDialog();
            }
            else if (currentAction.equals(IntentAction.DISABLE)) {
                showDisableDialog();
            }
            else{
                if(Iadt.isDebug())
                    Log.d(Iadt.TAG, "WelcomeDialogActivity - action not mapped without action");
                closeAll(false);
            }
        } else {
            if(Iadt.isDebug())
                Log.d(Iadt.TAG, "WelcomeDialogActivity - started without action");
            closeAll(false);
        }
    }

    private void showFirstDialog() {
        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder
                .setTitle(R.string.welcome_privacy_title)
                .setMessage(R.string.welcome_privacy_content)
                .setIcon(R.drawable.ic_bug_report_white_24dp)
                .setPositiveButton(R.string.button_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        showOverlayDialog();
                    }
                })
                .setNeutralButton(R.string.button_disable, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        showDisableDialog();
                    }
                })
                .setCancelable(false);

        buildAndShow(builder);
    }

    private void showOverlayDialog() {

        if (!PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
            ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
            final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
            builder.setTitle(R.string.welcome_permission_title)
                    .setIcon(R.drawable.ic_bug_report_white_24dp)
                    .setMessage(R.string.welcome_permission_content)
                    .setPositiveButton(R.string.button_continue, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                            requestOverlayPermission();
                        }
                    })
                    .setNeutralButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (currentAction.equals(IntentAction.PRIVACY)){
                                alertDialog.dismiss();
                                showFirstDialog();
                            }else{
                                if(onFailure!=null)
                                    onFailure.run();
                                closeAll(false);
                            }
                        }
                    })
                    .setCancelable(false);

            buildAndShow(builder);
        }
        else{
            showSuccessDialog();
        }
    }

    private void requestOverlayPermission() {
        PermissionActivity.request(PermissionActivity.IntentAction.OVERLAY,
                new Runnable() {
                    @Override
                    public void run() {
                        showSuccessDialog();
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        showOverlayDialog();
                    }
                });
    }

    private void showSuccessDialog() {
        IadtController.get().initForegroundIfPending();

        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setIcon(R.drawable.ic_check_circle_green_24dp)
                .setTitle(R.string.welcome_enabled_title)
                .setMessage(R.string.welcome_enabled_content)
                .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeAll(true);
                    }
                })
                .setNegativeButton(R.string.button_open_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IadtController.get().showMain();
                        closeAll(true);
                    }
                })
                .setCancelable(false);

        buildAndShow(builder);
    }

    private void showDisableDialog() {
        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setIcon(R.drawable.ic_cancel_red_24dp)
                .setTitle(R.string.welcome_disabled_title)
                .setMessage(R.string.welcome_disabled_content)
                .setPositiveButton(R.string.button_disable_all, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Iadt.getConfig().setBoolean(Config.ENABLED, false);
                        IadtController.get().restartApp(false);
                        closeAll(false);
                    }
                })
                .setNeutralButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (currentAction.equals(IntentAction.PRIVACY)){
                            alertDialog.dismiss();
                            showFirstDialog();
                        }else{
                            closeAll(false);
                        }
                    }
                })
                .setCancelable(false);

        buildAndShow(builder);
    }

    private void buildAndShow(AlertDialog.Builder builder) {
        if (builder == null)
            return;

        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_layer_main_middle);
        alertDialog.show();
    }

    private void closeAll(boolean success) {
        if (alertDialog != null){
            alertDialog.dismiss();
        }
        if(success && onSuccess!=null){
            onSuccess.run();
        }
        else if (!success && onFailure!=null){
            onFailure.run();
        }
        finish();
    }
}
