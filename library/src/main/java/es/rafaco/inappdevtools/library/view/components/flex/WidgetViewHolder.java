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

package es.rafaco.inappdevtools.library.view.components.flex;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.cardview.widget.CardView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
//#endif

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class WidgetViewHolder extends FlexibleViewHolder {

    private final CardView cardView;
    private final TextView mainContent;
    private final TextView title;
    private final TextView secondContent;

    public WidgetViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.cardView = view.findViewById(R.id.card_view);
        this.title = view.findViewById(R.id.title);
        this.mainContent = view.findViewById(R.id.main_content);
        this.secondContent = view.findViewById(R.id.second_content);
    }

    @Override
    public void bindTo(Object abstractData, final int position) {
        final WidgetData data = (WidgetData) abstractData;
        if (data!=null){

            itemView.setActivated(true);

            title.setText(data.getTitle());
            title.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));

            mainContent.setText(data.getMainContent());
            mainContent.setText(data.getMainContent());

            secondContent.setVisibility(TextUtils.isEmpty(data.getSecondContent()) ? View.GONE : View.VISIBLE);
            secondContent.setText(data.getSecondContent());
            secondContent.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));

            if (data.getPerformer() != null) {
                itemView.setClickable(true);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.getPerformer().run();
                    }
                });
            }
            cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.material_surface_bottom));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setElevation(UiUtils.getPixelsFromDp(itemView.getContext(), 3));
            }
        }
    }
}
