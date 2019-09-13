package es.rafaco.inappdevtools.library.view.overlay.layers;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import es.rafaco.inappdevtools.library.view.overlay.LayerManager;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class IconTouchListener implements View.OnTouchListener {

    private final Context context;
    private final LayerManager manager;
    private IconLayer iconLayer;

    private Point displaySize;
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;

    long time_start = 0, time_end = 0;
    boolean isLongClick = false;
    boolean isOverRemove = false;

    Handler handler_longClick = new Handler();
    Runnable onIconLongClick = new Runnable() {
        @Override
        public void run() {
            isLongClick = true;
            manager.getRemoveLayer().show();
        }
    };


    public IconTouchListener(IconLayer iconLayer, LayerManager manager) {
        this.iconLayer = iconLayer;
        this.manager = manager;
        context = manager.getContext();
        displaySize = UiUtils.getDisplaySize(context);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        WindowManager.LayoutParams iconParams = (WindowManager.LayoutParams) iconLayer.getView().getLayoutParams();

        int xTouch = (int) event.getRawX();
        int yTouch = (int) event.getRawY();
        int xDestination, yDestination;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                time_start = System.currentTimeMillis();
                x_init_cord = xTouch;
                y_init_cord = yTouch;
                x_init_margin = iconParams.x;
                y_init_margin = iconParams.y;

                handler_longClick.postDelayed(onIconLongClick, 600);
                return true;

            case MotionEvent.ACTION_UP:
                isLongClick = false;
                handler_longClick.removeCallbacks(onIconLongClick);
                manager.getRemoveLayer().hide();

                int x_diff = xTouch - x_init_cord;
                int y_diff = yTouch - y_init_cord;

                //Detect icon drop into remove
                if (isOverRemove) {
                    onIconDroppedAtRemove();
                    isOverRemove = false;
                    break;
                }

                //Detect click on Icon
                if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) {
                    //The check for x_diff <5 && y_diff< 5 because sometime elements moves a little while clicking.
                    time_end = System.currentTimeMillis();

                    //Also check the difference between start time and end time should be less than 300ms
                    if ((time_end - time_start) < 300)
                        onIconWidgetClick();
                }

                yDestination = y_init_margin + y_diff;

                int barHeight = UiUtils.getStatusBarHeight(context);
                int iconHeight = iconLayer.getView().getHeight();
                if (yDestination < 0) {
                    yDestination = 0;
                }
                else if (yDestination + (iconHeight + barHeight) > displaySize.y) {
                    yDestination = displaySize.y - (iconHeight + barHeight);
                }

                iconParams.y = yDestination;

                iconLayer.updatePositionToBorders(xTouch);
                return true;

            case MotionEvent.ACTION_MOVE:
                int x_diff_move = xTouch - x_init_cord;
                int y_diff_move = yTouch - y_init_cord;

                xDestination = x_init_margin + x_diff_move;
                yDestination = y_init_margin + y_diff_move;

                //While long clicking remove is shown: update icon position and remove color
                if (isLongClick) {
                    if (manager.getRemoveLayer().isOverRemove(xTouch, yTouch)){
                        isOverRemove = true;
                        manager.getRemoveLayer().updateBoundedState(isOverRemove);
                        iconLayer.updatePositionOverRemove();
                        break;
                    }
                    else { //if (isOverRemove) {
                        //Icon was in but get out of Remove
                        isOverRemove = false;
                        manager.getRemoveLayer().updateBoundedState(isOverRemove);
                    }
                }

                iconLayer.updatePosition(xDestination, yDestination);
                return true;
        }
        return false;
    }


    private void onIconWidgetClick() {
        OverlayService.performAction(OverlayService.IntentAction.SHOW_MAIN);
    }

    private void onIconDroppedAtRemove() {
        ((OverlayService)context).stopSelf();
    }
}
