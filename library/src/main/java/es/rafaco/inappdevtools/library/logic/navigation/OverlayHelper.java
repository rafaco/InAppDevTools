package es.rafaco.inappdevtools.library.logic.navigation;

import android.content.Context;
import android.content.Intent;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;
import es.rafaco.inappdevtools.library.view.activities.PermissionActivity;
import es.rafaco.inappdevtools.library.view.activities.WelcomeDialogActivity;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;

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

        if (getController().isPendingForegroundInit) {
            getController().initForegroundIfPending();
            if (!getController().isPendingForegroundInit)
                return true;
        }
        else if (!PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
            if (!PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
                WelcomeDialogActivity.open(WelcomeDialogActivity.IntentAction.OVERLAY,
                        new Runnable() {
                            @Override
                            public void run() {
                                showMain();
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                Iadt.showMessage(R.string.draw_other_app_permission_denied);
                            }
                        });
            }
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

    //endregion

}
