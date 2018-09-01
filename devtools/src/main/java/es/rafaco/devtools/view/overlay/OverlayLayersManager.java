package es.rafaco.devtools.view.overlay;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.utils.UiUtils;
import es.rafaco.devtools.view.OverlayUIService;
import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.layers.IconOverlayLayer;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayer;
import es.rafaco.devtools.view.overlay.layers.OverlayLayer;
import es.rafaco.devtools.view.overlay.layers.RemoveOverlayLayer;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

public class OverlayLayersManager {

    private Context context;
    private WindowManager windowManager;
    private LayoutInflater inflater;
    private List<OverlayLayer> overlayLayers;

    private Point szWindow = new Point();
    private boolean isLeft = true;
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;

    public OverlayLayersManager(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        this.inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.overlayLayers = new ArrayList<>();

        initDisplaySize();
        initLayers();
    }


    //region [ LAYERS MANAGER ]

    private void initLayers() {
        if (DevTools.getConfig().overlayUiIconEnabled){
            addLayer(new RemoveOverlayLayer(this));
            addLayer(new IconOverlayLayer(this));
            implementTouchListenerToIconWidgetView();
        }
        addLayer(new MainOverlayLayer(this));
    }

    public void addLayer(OverlayLayer overlayLayer){
        overlayLayers.add(overlayLayer);
        overlayLayer.addView();
    }

    public View getView(OverlayLayer.Type widgetType){
        OverlayLayer overlayLayer = getLayer(widgetType);
        if (overlayLayer != null)
            return overlayLayer.getView();
        return null;
    }

    public OverlayLayer getLayer(OverlayLayer.Type widgetType){
        for (OverlayLayer overlayLayer : overlayLayers) {
            if (overlayLayer.getType().equals(widgetType)){
                return overlayLayer;
            }
        }
        return null;
    }

    public MainOverlayLayer getMainLayer(){
        return (MainOverlayLayer) getLayer(OverlayLayer.Type.MAIN);
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public void destroy() {
        for (OverlayLayer overlayLayer : overlayLayers) {
            overlayLayer.destroy();
        }
    }

    //endregion

    //region [ TOGGLE LAYER VISIBILITY ]

    public void setMainVisibility(boolean mainVisible) {
        if (mainVisible) {
            getView(OverlayLayer.Type.MAIN).setVisibility(View.VISIBLE);
            if (DevTools.getConfig().overlayUiIconEnabled)
                getView(OverlayLayer.Type.ICON).setVisibility(View.GONE);
        } else {
            getView(OverlayLayer.Type.MAIN).setVisibility(View.GONE);
            if (DevTools.getConfig().overlayUiIconEnabled)
                getView(OverlayLayer.Type.ICON).setVisibility(View.VISIBLE);
        }
    }

    //endregion

    //region [ ICON WIDGET MOVEMENT ]

    private void initDisplaySize() {
        szWindow = UiUtils.getDisplaySize(context);
    }

    private void onIconWidgetClick() {
        Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.MAIN, null);
        context.startService(intent);
    }

    private void onIconDroppedAtRemove() {
        ((OverlayUIService)context).stopSelf();
    }

    /*  Implement Touch Listener to Icon OverlayLayer Root View
     *   Control Drag and move icon view using user's touch action.  */
    private void implementTouchListenerToIconWidgetView() {
        final View iconWidgetView = getView(OverlayLayer.Type.ICON);
        final View removeWidgetView = getView(OverlayLayer.Type.REMOVE);
        final ImageView remove_image_view = ((RemoveOverlayLayer) getLayer(OverlayLayer.Type.REMOVE)).remove_image_view;

        View rootContainer = iconWidgetView.findViewById(R.id.root_container);
        rootContainer.setOnTouchListener(new View.OnTouchListener() {

            long time_start = 0, time_end = 0;

            boolean isLongClick = false;//variable to judge if user click long press
            boolean inBounded = false;//variable to judge if floating view is bounded to remove view
            int remove_img_width = 0, remove_img_height = 0;

            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {
                @Override
                public void run() {
                    //On Floating OverlayLayer Long Click

                    //Set isLongClick as true
                    isLongClick = true;

                    //Set remove widget view visibility to VISIBLE
                    removeWidgetView.setVisibility(View.VISIBLE);

                    onIconWidgetLongClick();
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Get Floating widget view params
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) iconWidgetView.getLayoutParams();

                //get the touch location coordinates
                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();

                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();

                        handler_longClick.postDelayed(runnable_longClick, 600);

                        remove_img_width = remove_image_view.getLayoutParams().width;
                        remove_img_height = remove_image_view.getLayoutParams().height;

                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        //remember the initial position.
                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;

                        return true;
                    case MotionEvent.ACTION_UP:
                        isLongClick = false;
                        removeWidgetView.setVisibility(View.GONE);
                        remove_image_view.getLayoutParams().height = remove_img_height;
                        remove_image_view.getLayoutParams().width = remove_img_width;
                        handler_longClick.removeCallbacks(runnable_longClick);

                        //If user drag and drop the floating widget view into remove view then stop the service
                        if (inBounded) {
                            onIconDroppedAtRemove();
                            inBounded = false;
                            break;
                        }


                        //Get the difference between initial coordinate and current coordinate
                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        //The check for x_diff <5 && y_diff< 5 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) {
                            time_end = System.currentTimeMillis();

                            //Also check the difference between start time and end time should be less than 300ms
                            if ((time_end - time_start) < 300)
                                onIconWidgetClick();
                        }

                        y_cord_Destination = y_init_margin + y_diff;

                        int barHeight = getStatusBarHeight();
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0;
                        } else if (y_cord_Destination + (iconWidgetView.getHeight() + barHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (iconWidgetView.getHeight() + barHeight);
                        }

                        layoutParams.y = y_cord_Destination;

                        inBounded = false;

                        //reset position if user drags the floating view
                        resetPosition(x_cord);

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;

                        //If user long click the floating view, update remove view
                        if (isLongClick) {
                            int x_bound_left = szWindow.x / 2 - (int) (remove_img_width * 1.5);
                            int x_bound_right = szWindow.x / 2 + (int) (remove_img_width * 1.5);
                            int y_bound_top = szWindow.y - (int) (remove_img_height * 1.5);

                            //If Floating view comes under Remove View update Window Manager
                            if ((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top) {
                                inBounded = true;

                                int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
                                int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight()));

                                if (remove_image_view.getLayoutParams().height == remove_img_height) {
                                    remove_image_view.getLayoutParams().height = (int) (remove_img_height * 1.5);
                                    remove_image_view.getLayoutParams().width = (int) (remove_img_width * 1.5);

                                    WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeWidgetView.getLayoutParams();
                                    param_remove.x = x_cord_remove;
                                    param_remove.y = y_cord_remove;

                                    windowManager.updateViewLayout(removeWidgetView, param_remove);
                                }

                                layoutParams.x = x_cord_remove + (Math.abs(removeWidgetView.getWidth() - iconWidgetView.getWidth())) / 2;
                                layoutParams.y = y_cord_remove + (Math.abs(removeWidgetView.getHeight() - iconWidgetView.getHeight())) / 2;

                                //Update the layout with new X & Y coordinate
                                windowManager.updateViewLayout(iconWidgetView, layoutParams);
                                break;
                            } else {
                                //If Floating window gets out of the Remove view update Remove view again
                                inBounded = false;
                                remove_image_view.getLayoutParams().height = remove_img_height;
                                remove_image_view.getLayoutParams().width = remove_img_width;
                                //onIconWidgetClick();
                            }

                        }

                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;

                        //Update the layout with new X & Y coordinate
                        windowManager.updateViewLayout(iconWidgetView, layoutParams);
                        return true;
                }
                return false;
            }
        });
    }

    /*  Reset position of Floating OverlayLayer view on dragging  */
    private void resetPosition(int x_cord_now) {
        if (x_cord_now <= szWindow.x / 2) {
            isLeft = true;
            moveToLeft(x_cord_now);
        } else {
            isLeft = false;
            moveToRight(x_cord_now);
        }
    }

    /*  Method to move the Floating widget view to Left  */
    private void moveToLeft(final int current_x_cord) {
        final int x = szWindow.x - current_x_cord;
        final View iconWidgetView = getView(OverlayLayer.Type.ICON);

        new CountDownTimer(500, 5) {
            //get params of Floating OverlayLayer view
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) iconWidgetView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;

                //If you want bounce effect toogle following lines
                mParams.x = 0 - (int) (double) bounceValue(step, x);
                // mParams.x = 0 - (int) (current_x_cord * current_x_cord * step);

                //Update window manager for Floating OverlayLayer
                windowManager.updateViewLayout(iconWidgetView, mParams);
            }

            public void onFinish() {
                mParams.x = 0;

                //Update window manager for Floating OverlayLayer
                windowManager.updateViewLayout(iconWidgetView, mParams);
            }
        }.start();
    }

    /*  Method to move the Floating widget view to Right  */
    private void moveToRight(final int current_x_cord) {
        final View iconWidgetView = getView(OverlayLayer.Type.ICON);

        new CountDownTimer(500, 5) {
            //get params of Floating OverlayLayer view
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) iconWidgetView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;

                //If you want bounce effect uncomment below line and comment above line
                //mParams.x = szWindow.x + (int) (double) bounceValue(step, x_cord_now) - iconWidgetView.getWidth();
                mParams.x = (int) (szWindow.x + (current_x_cord * current_x_cord * step) - iconWidgetView.getWidth());

                //Update window manager for Floating OverlayLayer
                windowManager.updateViewLayout(iconWidgetView, mParams);
            }

            public void onFinish() {
                mParams.x = szWindow.x - iconWidgetView.getWidth();

                //Update window manager for Floating OverlayLayer
                windowManager.updateViewLayout(iconWidgetView, mParams);
            }
        }.start();
    }

    private double bounceValue(long step, long scale) {
        double value = scale * Math.exp(-0.055 * step) * Math.cos(0.08 * step);
        return value;
    }

    private int getStatusBarHeight() {
        return (int) Math.ceil(25 * context.getApplicationContext().getResources().getDisplayMetrics().density);
    }

    private void onIconWidgetLongClick() {
        View removeWidgetView = getView(OverlayLayer.Type.REMOVE);
        //Get remove Floating view params
        WindowManager.LayoutParams removeParams = (WindowManager.LayoutParams) removeWidgetView.getLayoutParams();

        //get x and y coordinates of remove view
        int x_cord = (szWindow.x - removeWidgetView.getWidth()) / 2;
        int y_cord = szWindow.y - (removeWidgetView.getHeight() + getStatusBarHeight());

        removeParams.x = x_cord;
        removeParams.y = y_cord;

        //Update Remove view params
        windowManager.updateViewLayout(removeWidgetView, removeParams);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        initDisplaySize();

        if (getMainLayer() != null){
            getMainLayer().onConfigurationChange(newConfig);
        }

        if (DevTools.getConfig().overlayUiIconEnabled){
            View iconWidgetView = getView(OverlayLayer.Type.ICON);
            if (iconWidgetView != null) {

                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) iconWidgetView.getLayoutParams();

                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (layoutParams.y + (iconWidgetView.getHeight() + getStatusBarHeight()) > szWindow.y) {
                        layoutParams.y = szWindow.y - (iconWidgetView.getHeight() + getStatusBarHeight());
                        windowManager.updateViewLayout(iconWidgetView, layoutParams);
                    }

                    if (layoutParams.x != 0 && layoutParams.x < szWindow.x) {
                        resetPosition(szWindow.x);
                    }

                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

                    if (layoutParams.x > szWindow.x) {
                        resetPosition(szWindow.x);
                    }
                }
            }
        }
    }

    //endregion

}
