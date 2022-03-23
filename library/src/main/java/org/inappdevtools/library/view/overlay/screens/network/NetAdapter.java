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

package org.inappdevtools.library.view.overlay.screens.network;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.inappdevtools.library.R;

import org.inappdevtools.library.storage.db.entities.NetSummary;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//@import androidx.paging.PagedListAdapter;
//@import androidx.recyclerview.widget.DiffUtil;
//#else
import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
//#endif

public class NetAdapter extends PagedListAdapter<NetSummary, NetViewHolder> {

    private static DiffUtil.ItemCallback<NetSummary> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<NetSummary>() {
                @Override
                public boolean areItemsTheSame(NetSummary oldItem, NetSummary newItem) {
                    return oldItem.uid == newItem.uid;
                }
                @Override
                public boolean areContentsTheSame(NetSummary oldItem,
                                                  NetSummary newItem) {
                    return oldItem.equalContent(newItem);
                }
            };

    private final NetViewHolder.Listener listener;

    protected NetAdapter(NetViewHolder.Listener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).uid;
    }

    @NonNull
    @Override
    public NetViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tool_net_item, viewGroup, false);
        NetViewHolder viewHolder = new NetViewHolder(itemView, listener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NetViewHolder holder, int position) {
        NetSummary data = getItem(position);
        if (data != null) {
            holder.bindTo(data, position);
        } else {
            holder.showPlaceholder(position);
        }
    }
}
