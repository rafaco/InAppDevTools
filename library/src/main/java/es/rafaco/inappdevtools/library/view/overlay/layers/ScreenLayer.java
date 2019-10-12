package es.rafaco.inappdevtools.library.view.overlay.layers;

import android.animation.LayoutTransition;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

//#ifdef ANDROIDX
//@import androidx.appcompat.widget.AppCompatImageView;
//@import androidx.appcompat.widget.Toolbar;
//@import androidx.core.widget.NestedScrollView;
//#else
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
//#endif

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.ConfigScreen;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;
import es.rafaco.inappdevtools.library.view.overlay.LayerManager;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ScreenLayer extends Layer {

    private NestedScrollView bodyScroll;
    private FrameLayout bodyContainer;
    private Toolbar toolbar;
    private LinearLayout fullContainer;

    public ScreenLayer(LayerManager manager) {
        super(manager);
    }

    @Override
    public Type getType() {
        return Type.SCREEN;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.overlay_layer_screen;
    }

    @Override
    protected WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                getLayoutType(),
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.CENTER;

        return params;
    }

    @Override
    protected void beforeAttachView(View view) {
        initScroll();
        initToolbar(view);

        ((FrameLayout)view).setLayoutTransition(new LayoutTransition());
    }

    @Override
    protected void afterAttachView(View view){
        //Hide full view on start
        view.setVisibility(View.GONE);
    }

    //region [ SCROLL ]

    private void initScroll() {
        bodyScroll = getView().findViewById(R.id.scroll_view);
        bodyContainer = getView().findViewById(R.id.tool_body_container);
        fullContainer = getView().findViewById(R.id.full_container);
    }

    public void scrollTop(){
        bodyScroll.post(new Runnable() {
                @Override
                public void run() {
                    bodyScroll.scrollTo(0, 0);
                }
            });
    }

    public void scrollBottom(){
        if (!isScrollAtBottom()){
            bodyScroll.post(new Runnable() {
                @Override
                public void run() {
                    bodyScroll.scrollTo(0, bodyContainer.getHeight());
                }
            });
        }
    }

    public void scrollToView(final View view){
        bodyScroll.post(new Runnable() {
                @Override
                public void run() {
                    final Rect rect = new Rect(0, 0, view.getWidth(), view.getHeight());
                    view.requestRectangleOnScreen(rect, false);
            }
        });
    }

    public final void focusOnView(final View view){
        bodyScroll.post(new Runnable() {
            @Override
            public void run() {
                bodyScroll.scrollTo(0, view.getBottom());
            }
        });
    }

    public boolean isScrollAtBottom() {
        // Grab the last child placed in the ScrollView, we need it to determinate the bottom position.
        View lastItem = bodyScroll.getChildAt(bodyScroll.getChildCount()-1);

        // Calculate the scrolldiff
        int diff = (lastItem.getBottom()-(bodyScroll.getHeight()+bodyScroll.getScrollY()));

        // if diff is zero, then the bottom has been reached
        return diff == 0;
    }

    //endregion

    //region [ TOOL BAR ]

    private void initToolbar(View view) {
        toolbar = view.findViewById(R.id.main_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onToolbarButtonPressed(item);
                return true;
            }
        });

        toggleBackButton(false);
        toolbar.inflateMenu(R.menu.overlay_screen);
    }

    public void setToolbarTitle(String title){
        if (title == null)
            title = "Iadt";

        toolbar.setTitle(title);

        //TODO: subtitle
        //toolbar.setSubtitle("Sample app");
    }

    public void toggleBackButton(boolean showBack){
        if (showBack){
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackButtonPressed();
                }
            });

            toolbar.setLogo(null);
            toolbar.setLogoDescription(null);
        }else{
            toolbar.setNavigationIcon(null);
            toolbar.setNavigationOnClickListener(null);

            addLogoAndResize();
        }
    }

    private void addLogoAndResize() {
        int appIconResourceId = UiUtils.getAppIconResourceId();
        Drawable logo =  IadtController.get().getContext()
                .getResources().getDrawable(appIconResourceId);
        toolbar.setLogo(logo);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View child = toolbar.getChildAt(i);
            if (child != null)
                if (child.getClass() == AppCompatImageView.class) {
                    AppCompatImageView iv2 = (AppCompatImageView) child;
                    if ( iv2.getDrawable() == logo ) {
                        iv2.setAdjustViewBounds(true);
                        iv2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        //activity_horizontal_margin = 16dp
                        int leftMargin = (int)UiUtils.getPixelsFromDp(iv2.getContext(), 16);
                        int otherMargins = iv2.getHeight()/6;
                        Toolbar.LayoutParams layout = (Toolbar.LayoutParams)iv2.getLayoutParams();
                        layout.setMargins(leftMargin, otherMargins, otherMargins, otherMargins);
                        iv2.requestLayout();
                    }
                }
        }
    }

    private void onToolbarButtonPressed(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_close) {
            IadtController.get().getOverlayHelper().showIcon();
        }
        else if (selected == R.id.action_half_position) {
            toggleSizePosition(item);
        }
        else if (selected == R.id.action_help) {
            ExternalIntentUtils.viewReadme();
        }
        else if (selected == R.id.action_config) {
            OverlayService.performNavigation(ConfigScreen.class);
        }
        else if (selected == R.id.action_share) {
            ExternalIntentUtils.shareLibrary();
        }
    }

    private void onBackButtonPressed() {
        OverlayService.performAction(OverlayService.IntentAction.NAVIGATE_BACK);
    }

    public View getFullContainer() {
        return fullContainer;
    }

    //endregion

    //region [ TOGGLE SIZE POSITION ]

    public enum SizePosition { FULL, HALF_FIRST, HALF_SECOND}
    private SizePosition currentSizePosition = SizePosition.FULL;

    public void toggleSizePosition(MenuItem item) {

        WindowManager.LayoutParams viewLayoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
        LinearLayout child = view.findViewById(R.id.main_container);
        FrameLayout.LayoutParams childLayoutParams = (FrameLayout.LayoutParams) child.getLayoutParams();

        if (currentSizePosition.equals(SizePosition.FULL)) {
            currentSizePosition = SizePosition.HALF_FIRST;
            item.setIcon(R.drawable.ic_arrow_up_white_24dp);

            int halfHeight = UiUtils.getDisplaySize(this.view.getContext()).y / 2;
            viewLayoutParams.height = halfHeight;
            viewLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
            childLayoutParams.gravity = Gravity.BOTTOM;
        }
        else if (currentSizePosition.equals(SizePosition.HALF_FIRST)) {
            currentSizePosition = SizePosition.HALF_SECOND;
            item.setIcon(R.drawable.ic_unfold_more_white_24dp);

            viewLayoutParams.gravity = Gravity.TOP | Gravity.CENTER;
            childLayoutParams.gravity = Gravity.TOP;
        }
        else {
            currentSizePosition = SizePosition.FULL;
            item.setIcon(R.drawable.ic_arrow_down_white_24dp);

            viewLayoutParams.height = MATCH_PARENT;
            viewLayoutParams.gravity = Gravity.TOP | Gravity.CENTER;
            childLayoutParams.gravity = Gravity.TOP;
        }
        child.setLayoutParams(childLayoutParams);
        manager.getWindowManager().updateViewLayout(view, viewLayoutParams);
    }

    //endregion

    @Override
    public void onConfigurationChange(Configuration newConfig) {
        //TODO: adapt half to landscape
        // if half: top is left and bottom is right
    }
}
