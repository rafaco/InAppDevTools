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

import org.inappdevtools.compat.AppCompatTextView;
import org.inappdevtools.library.storage.db.entities.AnalysisData;

import org.inappdevtools.library.R;

import org.inappdevtools.library.view.components.FlexAdapter;
import org.inappdevtools.library.view.components.FlexViewHolder;

public class AnalysisViewHolder extends FlexViewHolder {

    private final AppCompatTextView nameView;
    private final AppCompatTextView countView;
    private final AppCompatTextView percentageView;

    public AnalysisViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        this.nameView = view.findViewById(R.id.name);
        this.countView = view.findViewById(R.id.count);
        this.percentageView = view.findViewById(R.id.percentage);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        AnalysisData data = (AnalysisData) abstractData;
        nameView.setText(data.getName());
        countView.setText(String.valueOf(data.getCount()));
        percentageView.setText(String.valueOf(data.getPercentage()) + "% ");
    }
}
