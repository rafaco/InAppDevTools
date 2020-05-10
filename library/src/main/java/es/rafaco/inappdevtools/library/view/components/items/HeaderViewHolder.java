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

package es.rafaco.inappdevtools.library.view.components.items;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexibleViewHolder;

public class HeaderViewHolder extends FlexibleViewHolder {

    TextView titleView;

    public HeaderViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.titleView = view.findViewById(R.id.title);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        String data = (String) abstractData;
        titleView.setVisibility(TextUtils.isEmpty(data) ? View.GONE : View.VISIBLE);
        if (data != null) titleView.setText(data);
    }
}
