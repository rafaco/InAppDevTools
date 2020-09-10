/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.view.components.items;


import android.content.Context;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class HeaderFlexData extends TextFlexData {

    public HeaderFlexData(String text) {
        super(text, -1, Size.LARGE);

        Context context = IadtController.get().getContext();
        int horizontalMargin = (int) UiUtils.dpToPx(context, 14); //standard+innerCard
        int topMargin = (int) UiUtils.dpToPx(context, 10);
        int buttonMargin = (int) UiUtils.dpToPx(context, -4);
        setMargins(horizontalMargin, topMargin, horizontalMargin, buttonMargin);
    }
}
