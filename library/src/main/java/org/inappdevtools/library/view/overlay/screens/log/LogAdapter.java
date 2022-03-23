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

package org.inappdevtools.library.view.overlay.screens.log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//@import androidx.paging.PagedListAdapter;
//@import androidx.recyclerview.widget.DiffUtil;
//#else
import android.support.annotation.NonNull;
import android.arch.paging.PagedListAdapter;
import  android.support.v7.util.DiffUtil;
//#endif

import org.inappdevtools.library.storage.db.entities.Friendly;

import org.inappdevtools.library.R;

public class LogAdapter extends PagedListAdapter<Friendly, LogViewHolder> {

    private static DiffUtil.ItemCallback<Friendly> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Friendly>() {
                @Override
                public boolean areItemsTheSame(Friendly oldItem, Friendly newItem) {
                    return oldItem.getUid() == newItem.getUid();
                }
                @Override
                public boolean areContentsTheSame(Friendly oldItem,
                                                  Friendly newItem) {
                    return oldItem.equalContent(newItem);
                }
            };

    private final LogViewHolder.Listener listener;

    protected LogAdapter(LogViewHolder.Listener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getDate();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tool_log_item, viewGroup, false);
        LogViewHolder logViewHolder = new LogViewHolder(itemView, listener);

        return logViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        Friendly data = getItem(position);
        if (data != null) {
            holder.bindTo(data, position);
        } else {
            holder.showPlaceholder(position);
        }
    }

}
