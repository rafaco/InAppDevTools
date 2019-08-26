package es.rafaco.inappdevtools.library.view.overlay;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.config.Config;
import es.rafaco.inappdevtools.library.view.icons.IconDrawable;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.layers.*;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

public class OverlayLayersManager {

    private Context context;
    private WindowManager windowManager;
    private LayoutInflater inflater;
    private List<OverlayLayer> overlayLayers;

    private Point szWindow = new Point();
    private static boolean isBouncing;
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
        if (isOverlayIconEnabled()){
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
            if (isOverlayIconEnabled())
                getView(OverlayLayer.Type.ICON).setVisibility(View.GONE);
        } else {
            getView(OverlayLayer.Type.MAIN).setVisibility(View.GONE);
            if (isOverlayIconEnabled())
                getView(OverlayLayer.Type.ICON).setVisibility(View.VISIBLE);
        }
    }

    //endregion

    //region [ ICON WIDGET MOVEMENT ]

    private void initDisplaySize() {
        szWindow = UiUtils.getDisplaySize(context);
    }

    private void onIconWidgetClick() {
        Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.SHOW, null);
        context.startService(intent);
    }

    private void onIconDroppedAtRemove() {
        ((OverlayUIService)context).stopSelf();
    }

    /*  Implement Touch Listener2 to Icon OverlayLayer Root View
     *   Control Drag and move icon view using user's touch action.  */
    private void implementTouchListenerToIconWidgetView() {
        final View iconWidgetView = getView(OverlayLayer.Type.ICON);
        final View removeLayerView = getView(OverlayLayer.Type.REMOVE);

        final ImageView removeImage = ((RemoveOverlayLayer) getLayer(OverlayLayer.Type.REMOVE)).remove_image_view;

        View iconContainer = iconWidgetView.findViewById(R.id.icon_wrapper);
        iconContainer.setOnTouchListener(new View.OnTouchListener() {

            long time_start = 0, time_end = 0;

            boolean isLongClick = false;//variable to judge if user click long press
            boolean inBounded = false;//variable to judge if floating view is bounded to remove view
            int remove_img_width = 0, remove_img_height = 0;

            Handler handler_longClick = new Handler();
            Runnable onIconLongClick = new Runnable() {
                @Override
                public void run() {
                    isLongClick = true;

                    updateRemoveToInitial();
                    removeLayerView.setVisibility(View.VISIBLE);
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Get Floating widget view params
                WindowManager.LayoutParams iconParams = (WindowManager.LayoutParams) iconWidgetView.getLayoutParams();

                //get the touch location coordinates
                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();

                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();

                        handler_longClick.postDelayed(onIconLongClick, 600);

                        remove_img_width = removeImage.getLayoutParams().width;
                        remove_img_height = removeImage.getLayoutParams().height;

                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        //remember the initial position.
                        x_init_margin = iconParams.x;
                        y_init_margin = iconParams.y;
                        return true;

                    case MotionEvent.ACTION_UP:
                        isLongClick = false;
                        removeLayerView.setVisibility(View.GONE);
                        removeImage.getLayoutParams().height = remove_img_height;
                        removeImage.getLayoutParams().width = remove_img_width;
                        handler_longClick.removeCallbacks(onIconLongClick);

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

                        iconParams.y = y_cord_Destination;

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

                            //If icon go over removeImage
                            if ((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top) {
                                inBounded = true;

                                int xCordRemove = (int) ((szWindow.x - (remove_img_width * 1.5)) / 2)
                                        - IconDrawable.dpToPx(context.getResources(), 10);
                                int yCordRemove = (int) (szWindow.y - ((remove_img_height * 1.5) + getStatusBarHeight()))
                                        - IconDrawable.dpToPx(context.getResources(), 10);

                                if (removeImage.getLayoutParams().height == remove_img_height) {
                                    //Increase removeImage size
                                    ViewGroup.LayoutParams removeImageParams = removeImage.getLayoutParams();
                                    removeImageParams.height = (int) (remove_img_height * 1.5);
                                    removeImageParams.width = (int) (remove_img_width * 1.5);
                                    UiUtils.setBackgroundColorToDrawable(context, R.color.rally_orange, removeImage.getBackground());

                                    //Reposition removeImage for new size
                                    WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeLayerView.getLayoutParams();
                                    param_remove.x = xCordRemove;
                                    param_remove.y = yCordRemove;
                                    windowManager.updateViewLayout(removeLayerView, param_remove);
                                }

                                //Put icon over remove image
                                iconParams.x = xCordRemove + (Math.abs(removeLayerView.getWidth() - iconWidgetView.getWidth())) / 2;
                                iconParams.y = yCordRemove + (Math.abs(removeLayerView.getHeight() - iconWidgetView.getHeight())) / 2;
                                windowManager.updateViewLayout(iconWidgetView, iconParams);
                                break;
                            } else { //if (inBounded) {
                                //Icon was in but get out of Remove
                                inBounded = false;

                                UiUtils.setBackgroundColorToDrawable(context, R.color.rally_bg_solid, removeImage.getBackground());
                                removeImage.getLayoutParams().height = remove_img_height;
                                removeImage.getLayoutParams().width = remove_img_width;
                                updateRemoveToInitial();
                                
                                /*WindowManager.LayoutParams removeParams = (WindowManager.LayoutParams) removeLayerView.getLayoutParams();
                                removeParams.x = (szWindow.x - removeLayerView.getWidth()) / 2;
                                removeParams.y = szWindow.y - (removeLayerView.getHeight() + getStatusBarHeight());
                                windowManager.updateViewLayout(removeLayerView, removeParams);*/
                            }
                        }

                        //Update icon position
                        iconParams.x = x_cord_Destination;
                        iconParams.y = y_cord_Destination;
                        windowManager.updateViewLayout(iconWidgetView, iconParams);
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
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) iconWidgetView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;

                if (isBouncing)
                    mParams.x = 0 - (int) (double) bounceValue(step, x);
                else
                    mParams.x = 0 - (int) (current_x_cord * current_x_cord * step);

                windowManager.updateViewLayout(iconWidgetView, mParams);
            }

            public void onFinish() {
                mParams.x = 0;
                windowManager.updateViewLayout(iconWidgetView, mParams);
            }
        }.start();
    }

    /*  Method to move the Floating widget view to Right  */
    private void moveToRight(final int current_x_cord) {
        final View iconWidgetView = getView(OverlayLayer.Type.ICON);

        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) iconWidgetView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;

                if (isBouncing)
                    mParams.x = szWindow.x + (int) (double) bounceValue(step, current_x_cord) - iconWidgetView.getWidth();
                else
                    mParams.x = (int) (szWindow.x + (current_x_cord * current_x_cord * step) - iconWidgetView.getWidth());

                windowManager.updateViewLayout(iconWidgetView, mParams);
            }

            public void onFinish() {
                mParams.x = szWindow.x - iconWidgetView.getWidth();
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

    private void updateRemoveToInitial() {
        View removeLayer = getView(OverlayLayer.Type.REMOVE);
        WindowManager.LayoutParams removeParams = (WindowManager.LayoutParams) removeLayer.getLayoutParams();
        removeParams.x = (szWindow.x - removeLayer.getWidth()) / 2;
        removeParams.y = szWindow.y - (removeLayer.getHeight() + getStatusBarHeight());
        windowManager.updateViewLayout(removeLayer, removeParams);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        initDisplaySize();

        if (getMainLayer() != null){
            getMainLayer().onConfigurationChange(newConfig);
        }

        if (isOverlayIconEnabled()){
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

    private boolean isOverlayIconEnabled() {
        return Iadt.getConfig().getBoolean(Config.INVOCATION_BY_ICON);
    }
}
