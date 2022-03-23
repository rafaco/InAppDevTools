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

package org.inappdevtools.compat;

import android.content.Context;
import android.util.AttributeSet;

//#ifdef ANDROIDX
//@public class CollapsingToolbarLayout extends com.google.android.material.appbar.CollapsingToolbarLayout {
//#else
public class CollapsingToolbarLayout extends android.support.design.widget.CollapsingToolbarLayout {
//#endif

    public CollapsingToolbarLayout(Context context) {
        super(context);
    }

    public CollapsingToolbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
