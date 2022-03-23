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

package org.inappdevtools.library.view.components.items;

import android.view.View;
import android.widget.CheckBox;

import org.inappdevtools.library.R;
import org.inappdevtools.library.view.components.FlexAdapter;
import org.inappdevtools.library.view.components.FlexViewHolder;

public class CheckboxViewHolder extends FlexViewHolder {

    CheckBox checkBox;

    public CheckboxViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        this.checkBox = view.findViewById(R.id.checkBox);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final CheckboxData data = (CheckboxData) abstractData;
        checkBox.setText(data.getTitle());
        checkBox.setSelected(data.isValue());
        checkBox.setOnCheckedChangeListener(data.getOnChange());
    }
}
