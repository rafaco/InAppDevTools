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

package org.inappdevtools.library.view.components.composers;

import android.view.Gravity;

//#ifdef ANDROIDX
//@import androidx.annotation.StringRes;
//@import androidx.annotation.ColorRes;
//#else
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
//#endif

import org.inappdevtools.library.R;
import org.inappdevtools.library.view.components.base.FlexData;
import org.inappdevtools.library.view.components.groups.LinearGroupFlexData;
import org.inappdevtools.library.view.components.items.ButtonSecondaryFlexData;
import org.inappdevtools.library.view.components.items.TextFlexData;
import org.inappdevtools.library.view.utils.MarginUtils;

public class SecondaryButtonsComposer {

    private final LinearGroupFlexData linearGroupData;

    public SecondaryButtonsComposer(String title) {
        linearGroupData = new LinearGroupFlexData();
        linearGroupData.setFullSpan(true);
        linearGroupData.setShowDividers(true);
        linearGroupData.setLayoutType(FlexData.LayoutType.FULL_WIDTH);
        linearGroupData.setChildLayout(FlexData.LayoutType.FULL_WIDTH);
        linearGroupData.setGravity(Gravity.LEFT);

        TextFlexData titleData = new TextFlexData(title, R.color.iadt_text_high);
        titleData.setMargins(MarginUtils.getHorizontalMargin(), 4*MarginUtils.getVerticalMargin(),
                MarginUtils.getHorizontalMargin(), MarginUtils.getVerticalMargin());
        linearGroupData.add(titleData);
    }

    public FlexData getContainer(){
        return linearGroupData;
    }

    public void add(String text, @StringRes int icon, @ColorRes int colorRes, Runnable performer){
        add(new ButtonSecondaryFlexData(text,
                icon,
                colorRes, //R.color.iadt_text_high,
                performer));
    }

    public void add(ButtonSecondaryFlexData button){
        linearGroupData.add(button);
    }

    public void setHorizontalMargins(Boolean enabled){
        linearGroupData.setHorizontalMargin(enabled);
    }

    public LinearGroupFlexData compose() {
        return linearGroupData;
    }
}
