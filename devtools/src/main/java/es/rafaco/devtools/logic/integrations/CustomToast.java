package es.rafaco.devtools.logic.integrations;

import android.content.Context;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.List;

import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.utils.ThreadUtils;
import es.rafaco.devtools.view.overlay.layers.OverlayLayer;
import es.rafaco.devtools.view.utils.ViewHierarchyUtils;

public class CustomToast {

    public static final int TYPE_INFO = 0;
    public static final int TYPE_SUCCESS = 1;
    public static final int TYPE_WARNING = 2;
    public static final int TYPE_ERROR = 3;

    public static void show(final Context context, final String text, final int type){
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MDToast toast = MDToast.makeText(context, text, MDToast.LENGTH_LONG, type);
                int gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                int yOffset = context.getResources().getDimensionPixelSize(R.dimen.toast_y_offset);
                toast.setGravity(gravity, 0, yOffset);
                //toast.getView().getParent().getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                toast.show();

                List<Pair<String, View>> rootViews = ViewHierarchyUtils.getRootViews(false);

                if(rootViews != null){
                    for (Pair<String, View> rootView : rootViews){
                        if(rootView.first.contains("Toast")){
                            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) rootView.second.getLayoutParams();
                            layoutParams.type = OverlayLayer.getLayoutType();
                            //((View)rootView.second).
                        }
                    }
                }
            }
        });
    }

}
