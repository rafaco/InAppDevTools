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

package es.rafaco.inappdevtools.library.view.components.deco;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
//#endif

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;

public class DecoratedViewHolder extends RecyclerView.ViewHolder {

    Context context;
    boolean switchMode;
    ImageView headIcon;
    View decorator;
    TextView title, message;
    ImageView icon;
    Switch switchButton;

    public DecoratedViewHolder(ViewGroup parent, boolean switchMode) {
        super(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.decorated_tool_info_item, parent, false));

        context = itemView.getContext();
        this.switchMode = switchMode;
        decorator = itemView.findViewById(R.id.decorator);
        title = itemView.findViewById(R.id.title);
        message = itemView.findViewById(R.id.message);
        headIcon = itemView.findViewById(R.id.head_icon);
        icon = itemView.findViewById(R.id.icon);
        switchButton = itemView.findViewById(R.id.switch_button);
    }


    public void bindTo(final DecoratedToolInfo data) {

        if (TextUtils.isEmpty(data.title))
            title.setVisibility(View.GONE);
        else{
            title.setVisibility(View.VISIBLE);
            title.setText(data.title);
        }

        if (TextUtils.isEmpty(data.message))
            title.setVisibility(View.GONE);
        else{
            message.setVisibility(View.VISIBLE);
            message.setText(data.message);
        }

        int contextualizedColor = ContextCompat.getColor(itemView.getContext(),data.color);
        title.setTextColor(contextualizedColor);
        decorator.setBackgroundColor(contextualizedColor);

        if (data.icon != -1){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                headIcon.setImageDrawable(context.getApplicationContext().getDrawable(data.icon));
                headIcon.setColorFilter(contextualizedColor);
            } else {
                headIcon.setImageDrawable(context.getResources().getDrawable(data.icon));
            }
            headIcon.setVisibility(View.VISIBLE);
        }else{
            headIcon.setVisibility(View.GONE);
        }

        if(!switchMode){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(data);
                }
            });
        }else{
            icon.setVisibility(View.GONE);
            switchButton.setVisibility(View.VISIBLE);
        }

        //TODO: temp
        if (data.icon != -1){
            icon.setVisibility(View.GONE);
        }
    }

    protected void onItemClick(DecoratedToolInfo data) {
        if (data.getNavigationStep() != null)
            OverlayService.performNavigationStep(data.getNavigationStep());
        else
            data.getRunnable().run();
    }
}
