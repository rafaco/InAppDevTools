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

package org.inappdevtools.library.view.utils;

import android.view.View;

import org.inappdevtools.compat.AppCompatButton;

public class ButtonUtils {

    private ButtonUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void setEnabled(AppCompatButton button, View.OnClickListener onClickListener){
        toggleEnabled(button, true, onClickListener);
    }

    public static void setDisabled(AppCompatButton button){
        toggleEnabled(button, false, null);
    }

    public static void toggleEnabled(AppCompatButton button, boolean enabled, View.OnClickListener onClickListener) {
        button.setOnClickListener(enabled ? onClickListener : null);
        button.setAlpha(enabled ? 1F : 0.5F);
        button.setEnabled(enabled);
        button.setActivated(enabled);
    }
}
