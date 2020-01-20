/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.view.activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

//#ifdef ANDROIDX
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
import es.rafaco.inappdevtools.library.logic.config.BuildConfig;
import es.rafaco.inappdevtools.library.logic.documents.Document;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.AppInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.BuildInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.DeviceInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.OSInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.prefs.utils.NewBuildUtil;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PrivacyConsentUtil;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class IadtDialogActivity extends AppCompatActivity {

    public enum IntentAction { AUTO, BUILD_INFO, PRIVACY, OVERLAY, DISABLE }
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
            Context context = IadtController.get().getContext();
            Intent intent = new Intent(context, IadtDialogActivity.class);
            intent.putExtra(EXTRA_INTENT_ACTION, action);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent, null);
        }else{
            if (onSuccess !=null)
                onSuccess.run();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            initialize();
        }
        catch (Exception e){
            String message = "IadtDialogActivity onCreate crashed";
            boolean shouldThrow = IadtController.get().handleInternalException(message, e);
            if (shouldThrow)
                throw e;

            closeAll(false);
        }
    }

    private void initialize() {
        currentAction = (IntentAction) getIntent().getSerializableExtra(EXTRA_INTENT_ACTION);
        if (currentAction != null) {
            if (currentAction.equals(IntentAction.AUTO)) {
                showAuto();
            }
            else if (currentAction.equals(IntentAction.BUILD_INFO)) {
                showNewBuildDialog(false);
            }
            else if (currentAction.equals(IntentAction.PRIVACY)) {
                showPrivacyDialog(false);
            }
            else if (currentAction.equals(IntentAction.OVERLAY)) {
                showOverlayDialog(false);
            }
            else if (currentAction.equals(IntentAction.DISABLE)) {
                showDisableDialog(false);
            }
            else{
                if(Iadt.isDebug())
                    Log.d(Iadt.TAG, "IadtDialogActivity - action not mapped without action");
                closeAll(false);
            }
        } else {
            if(Iadt.isDebug())
                Log.d(Iadt.TAG, "IadtDialogActivity - started without action");
            closeAll(false);
        }
    }

    private void showAuto(){
        Session session = IadtController.get().getSessionManager().getCurrent();

        if (!NewBuildUtil.isBuildInfoSkipped() && !NewBuildUtil.isBuildInfoShown()){
            showNewBuildDialog(true);
        }
        else if (!PrivacyConsentUtil.isAccepted()){
            showPrivacyDialog(true);
        }
        else if (!PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
            showOverlayDialog(true);
        }
        else if (session.isFirstStart()){
            showSuccessDialog();
        }
        else{
            closeAll(true);
        }
    }

    private void showNewBuildDialog(final boolean isAuto) {
        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        String welcomeText = ((AppInfoDocumentGenerator) DocumentRepository.getGenerator(Document.APP_INFO)).getAppNameAndVersions();
        welcomeText += "." + Humanizer.newLine();
        welcomeText += ((BuildInfoDocumentGenerator) DocumentRepository.getGenerator(Document.BUILD_INFO)).getBuildWelcome();
        welcomeText += "." + Humanizer.newLine();
        welcomeText += ((DeviceInfoDocumentGenerator) DocumentRepository.getGenerator(Document.DEVICE_INFO)).getSecondLineOverview();
        welcomeText += " ";
        welcomeText += ((OSInfoDocumentGenerator) DocumentRepository.getGenerator(Document.OS_INFO)).getOneLineOverview();
        welcomeText += "." + Humanizer.newLine();
        welcomeText += Humanizer.fullStop();

        String notes = IadtController.get().getConfig().getString(BuildConfig.NOTES);
        if (!TextUtils.isEmpty(notes)){
            welcomeText += notes + Humanizer.newLine();
        }
        
        builder.setTitle(R.string.welcome_welcome_title)
                .setMessage(welcomeText)
                .setIcon(UiUtils.getAppIconResourceId())
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        if (isAuto)
                            showAuto();
                        else
                            closeAll(true);
                    }
                });

        Session session = IadtController.get().getSessionManager().getCurrent();
        boolean isNotFirstStart = !session.isFirstStart();
        if (isNotFirstStart){
            builder.setNegativeButton(R.string.button_skip_next, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                    NewBuildUtil.saveBuildInfoSkip();
                    if (isAuto)
                        showAuto();
                    else
                        closeAll(true);
                }
            });
        }
        builder.setCancelable(isNotFirstStart);

        buildAndShow(builder);
        NewBuildUtil.saveBuildInfoShown();
    }

    private void showPrivacyDialog(final boolean isAuto) {
        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder
                .setTitle(R.string.welcome_privacy_title)
                .setMessage(R.string.welcome_privacy_content)
                //.setIcon(R.drawable.iadt_logo)
                .setPositiveButton(R.string.button_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        PrivacyConsentUtil.saveAccepted();
                        if (isAuto)
                            showAuto();
                        else
                            closeAll(true);
                    }
                })
                .setNeutralButton(R.string.button_disable, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        showDisableDialog(true);
                    }
                })
                .setCancelable(false);

        buildAndShow(builder);
    }

    private void showOverlayDialog(final boolean isAuto) {
        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setTitle(R.string.welcome_permission_title)
                //.setIcon(R.drawable.iadt_logo)
                .setMessage(R.string.welcome_permission_content)
                .setPositiveButton(R.string.button_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        requestOverlayPermission(isAuto);
                    }
                })
                .setNeutralButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();

                        if (isAuto)
                            showDisableDialog(true);
                        else
                            closeAll(false);
                    }
                })
                .setCancelable(false);

        buildAndShow(builder);
    }

    private void requestOverlayPermission(final boolean isAuto) {
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
                        showOverlayDialog(isAuto);
                    }
                });
    }

    private void showSuccessDialog() {
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
                /*.setNegativeButton(R.string.button_open_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IadtController.get().getOverlayHelper().showMain();
                        closeAll(true);
                    }
                })*/
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        closeAll(true);
                    }
                })
                .setCancelable(true);

        buildAndShow(builder);
    }

    private void showDisableDialog(final boolean isAuto) {
        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setIcon(R.drawable.ic_cancel_red_24dp)
                .setTitle(R.string.welcome_disabled_title)
                .setMessage(R.string.welcome_disabled_content)
                .setPositiveButton(R.string.button_disable_all, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Iadt.getConfig().setBoolean(BuildConfig.ENABLED, false);
                        IadtController.get().restartApp(false);
                        closeAll(true);
                    }
                })
                .setNeutralButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        if (isAuto){
                            showAuto();
                        }
                        else {
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
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);
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
