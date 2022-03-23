/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.logic.navigation;

import android.content.Context;
import android.content.Intent;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.logic.utils.AppUtils;
import org.inappdevtools.library.view.activities.PermissionActivity;
import org.inappdevtools.library.view.dialogs.WelcomeOverlayDialog;
import org.inappdevtools.library.view.overlay.OverlayService;
import org.inappdevtools.library.view.overlay.layers.ScreenLayer;

public class OverlayHelper {

    private final Context context;

    public OverlayHelper(Context context) {
        this.context = context;
        initOverlayService();
    }

    private Context getContext() {
        return context;
    }

    private IadtController getController() {
        return IadtController.get();
    }

    private void initOverlayService() {
        Intent intent = new Intent(getContext(), OverlayService.class);
        getContext().startService(intent);
    }

    private boolean cantShowOverlay() {
        if (!getController().isEnabled()) return true;
        if (!AppUtils.isForegroundImportance(getContext())) return true;

        if (getController().isPendingInitFull) {
            getController().initFullIfPending();
            if (!getController().isPendingInitFull)
                return true;
        }
        else if (!PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
            IadtController.get().getDialogManager().load(new WelcomeOverlayDialog());
            return true;
        }
        return false;
    }

    //region [ PUBLIC METHODS ]

    public void showToggle() {
        if (cantShowOverlay()) return;
        OverlayService.performAction(OverlayService.IntentAction.SHOW_TOGGLE);
    }
    public void showMain() {
        if (cantShowOverlay()) return;
        OverlayService.performAction(OverlayService.IntentAction.SHOW_MAIN);
    }

    public void showIcon() {
        if (cantShowOverlay()) return;
        OverlayService.performAction(OverlayService.IntentAction.SHOW_ICON);
    }

    public void hideAll() {
        if (cantShowOverlay()) return;
        OverlayService.performAction(OverlayService.IntentAction.HIDE_ALL);
    }

    public void restoreAll() {
        if (cantShowOverlay()) return;
        OverlayService.performAction(OverlayService.IntentAction.RESTORE_ALL);
    }

    public void toggleScreenLayout(ScreenLayer.SizePosition newPosition) {
        if (cantShowOverlay()) return;
        OverlayService.performAction(OverlayService.IntentAction.TOGGLE_SCREEN_SIZE, newPosition.name());
    }

    //endregion

}
