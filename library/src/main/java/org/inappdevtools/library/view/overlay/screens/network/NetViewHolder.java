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

package org.inappdevtools.library.view.overlay.screens.network;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
//#endif

import org.inappdevtools.library.storage.db.entities.NetSummary;

import org.inappdevtools.library.R;

import org.inappdevtools.library.view.utils.Humanizer;

public class NetViewHolder extends RecyclerView.ViewHolder {

    Listener listener;
    long uid;

    LinearLayout wrapper;
    TextView title;
    TextView content;
    View separator;

    public NetViewHolder(View view, Listener listener) {
        super(view);
        this.listener = listener;
        wrapper = view.findViewById(R.id.wrapper);
        title = view.findViewById(R.id.title);
        content = view.findViewById(R.id.content);
        separator = view.findViewById(R.id.separator);
    }

    public interface Listener {
        void onItemClick(View itemView, int position, long id);
    }

    public void bindTo(final NetSummary data, int position) {

        uid = data.uid;
        wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, getAdapterPosition(), uid);
            }
        });

        NetFormatter formatter = new NetFormatter(data);
        int titleColor = formatter.getColor();
        title.setTextColor(ContextCompat.getColor(itemView.getContext(), titleColor));
        title.setText(data.url);

        content.setText(formatter.getComposedLine());

        separator.setVisibility(false ? View.GONE : View.VISIBLE);
    }

    public void showPlaceholder(int position) {
        int color = ContextCompat.getColor(title.getContext(), R.color.rally_gray);

        title.setVisibility(View.VISIBLE);
        title.setBackgroundColor(color);

        content.setVisibility(View.VISIBLE);
        content.setBackgroundColor(color);

        if (Humanizer.isEven(position)){
            title.setMaxWidth(title.getWidth()/2);
        }
    }
}
