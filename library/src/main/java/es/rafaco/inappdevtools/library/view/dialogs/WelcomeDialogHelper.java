/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.view.dialogs;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.dialogs.DialogManager;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.prefs.utils.NewBuildUtil;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PrivacyConsentUtil;
import es.rafaco.inappdevtools.library.view.activities.PermissionActivity;

public class WelcomeDialogHelper {

    private static Runnable onSuccess;

    public void showIfNeededThen(Runnable onSuccessCallback) {
        if (!shouldShowInitialDialog()){
            onSuccessCallback.run();
        }
        onSuccess = onSuccessCallback;
        showAuto();
    }

    private boolean shouldShowInitialDialog() {
        if (isWelcomeCompulsory() || shouldShowNewBuild()){
            return true;
        }
        return false;
    }

    private boolean shouldShowNewBuild() {
        Session session = IadtController.get().getSessionManager().getCurrent();
        return session.isNewBuild() && !NewBuildUtil.isBuildInfoSkipped() && !NewBuildUtil.isBuildInfoShown();
    }

    private boolean isWelcomeCompulsory() {
        return !PrivacyConsentUtil.isAccepted()
                || !PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY);
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
        getDialogManager().load(new NewBuildDialog() {
            @Override
            public void onDismiss() {
                if (isAuto)
                    showAuto();
                else
                    closeAll(true);
            }
        }.setCancelable(false));
    }

    private void showPrivacyDialog(final boolean isAuto) {
        getDialogManager().load(new WelcomePrivacyDialog() {
            @Override
            public void onAccepted() {
                if (isAuto)
                    showAuto();
                else
                    closeAll(true);
            }

            @Override
            public void onDisable() {
                showDisableDialog(true);
            }
        });
    }

    private void showOverlayDialog(final boolean isAuto) {
        getDialogManager().load(new WelcomeOverlayDialog() {
            @Override
            public void onPermissionGranted() {
                showSuccessDialog();
            }

            @Override
            public void onPermissionRevoked() {
                showOverlayDialog(isAuto);
            }

            @Override
            public void onCancel() {
                if (isAuto)
                    showDisableDialog(true);
                else
                    closeAll(false);
            }
        });
    }

    private void showSuccessDialog() {
        getDialogManager().load(new WelcomeSuccessDialog() {
            @Override
            public void onDismiss() {
                closeAll(false);
            }
        }.setCancelable(true));
    }

    private void showDisableDialog(final boolean isAuto) {
        getDialogManager().load(new DisableDialog() {
            @Override
            public void onPositive() {
                closeAll(false);
            }
            @Override
            public void onNeutral() {
                if (isAuto){
                    showAuto();
                }
                else {
                    closeAll(false);
                }
            }
        });
    }

    private void closeAll(boolean success) {
        if(success && onSuccess!=null){
            onSuccess.run();
        }
    }

    private DialogManager getDialogManager() {
        return IadtController.get().getDialogManager();
    }
}
