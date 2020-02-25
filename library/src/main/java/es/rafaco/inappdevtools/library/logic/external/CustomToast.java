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

package es.rafaco.inappdevtools.library.logic.external;

import android.content.Context;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;
import es.rafaco.inappdevtools.library.view.utils.ViewHierarchyUtils;

public class CustomToast {

    public static final int TYPE_INFO = 0;
    public static final int TYPE_SUCCESS = 1;
    public static final int TYPE_WARNING = 2;
    public static final int TYPE_ERROR = 3;

    public static void show(final Context context, final String text, final int type){
        ThreadUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                MDToast toast = MDToast.makeText(context, text, MDToast.LENGTH_LONG, type);
                int gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                int yOffset = context.getResources().getDimensionPixelSize(R.dimen.toast_y_offset);
                toast.setGravity(gravity, 0, yOffset);
                //toast.getView().getParent().getWindow().setType(Layer.getLayoutType());
                toast.show();

                List<Pair<String, View>> rootViews = ViewHierarchyUtils.getRootViews(false);

                if(rootViews != null){
                    for (Pair<String, View> rootView : rootViews){
                        if(rootView.first.contains("Toast")){
                            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) rootView.second.getLayoutParams();
                            layoutParams.type = Layer.getLayoutType();
                            //((View)rootView.second).
                        }
                    }
                }
            }
        });
    }

}
